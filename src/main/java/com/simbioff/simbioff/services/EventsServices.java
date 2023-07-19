package com.simbioff.simbioff.services;

import com.simbioff.simbioff.dto.EventsApprove;
import com.simbioff.simbioff.dto.EventsDto;
import com.simbioff.simbioff.dto.UpdateEventsDto;
import com.simbioff.simbioff.enums.DayoffType;
import com.simbioff.simbioff.enums.StatusDayOff;
import com.simbioff.simbioff.models.EventsModel;
import com.simbioff.simbioff.models.UserModel;
import com.simbioff.simbioff.repositories.EventsRepository;
import com.simbioff.simbioff.repositories.UserRepository;
import org.apache.commons.math3.util.Precision;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.mail.MessagingException;

@Service
public class EventsServices {

	@Autowired
	private EventsRepository eventsRepository;

	@Autowired
	private UserServices userServices;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private EmailService emailService;
	
	private int eventId;

	public boolean existsByID(int id) {
		return eventsRepository.existsById(id);
	}
	
	public Optional<EventsModel> findEventById(int id) {
		return Optional.of(eventsRepository.findById(id));
	}

	public double daysWorking(UserModel user) throws Exception {
		// This function counts how many days off the employee has in general without
		// any discount
		//UserModel user = userRepository.findById(idUser).orElseThrow(() -> new Exception("user not found"));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date startWorking = sdf.parse(user.getStartOnTeam().toString());
		Date now = sdf.parse(LocalDate.now().toString());
		var diffInMiles = Math.abs(startWorking.getTime() - now.getTime());
		return TimeUnit.DAYS.convert(diffInMiles, TimeUnit.MILLISECONDS);
	}

	public double countDayOffRequest(UserModel user) throws ParseException {
		// This function returns the number of day offs that the employee has already
		// requested
		double countHalfDays = eventsRepository.countHalfDayOffs(user.getIdUser());
		double countDaysOffs = eventsRepository.countDayOff(user.getIdUser());
		if (user.getDaysOffWithdrawn() == null) {
			user.setDaysOffWithdrawn(0.0);
		}
		return (countHalfDays / 2) + countDaysOffs + user.getDaysOffWithdrawn();

	}

	public double dayOffsLeft(double allDaysOff, double daysOffRequested) throws ParseException {
		// This function returns the number of day offs that the employee has left
		double FORMULA = 0.08767123288;
		return Precision.round((allDaysOff * FORMULA) - daysOffRequested, 2);
	}

	public List<EventsModel> saveDayoff(EventsDto eventsDto) throws Exception {
		//This function saves the day off in the database
		EventsModel eventsModel = new EventsModel();
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		var ListDayoff = new ArrayList<EventsModel>();
		try {
			//This block of code is used to check if the date is valid
			List<LocalDate> dates = new ArrayList<>();
			String[] dateStrings = eventsDto.getDate().split("/");
			LocalDate startDate = LocalDate.parse(dateStrings[0], formatter);
			if (startDate.isBefore(now.toLocalDate())) {
				throw new Exception("Você não pode solicitar um dayoff no passado");
			}
			userServices.getLoggedUser();
			if (startDate.isBefore(ChronoLocalDate.from(now.plusDays(3))) && !userServices.isAdmin()) {
				throw new Exception("Você não pode solicitar um dayoff com menos de 72 horas de antecedência");
			}
			LocalDate endDate = startDate;
			if (eventsDto.getDate().contains("/")) {
				endDate = LocalDate.parse(dateStrings[1], formatter);
			}
			while (!startDate.isAfter(endDate)) {
				//This block of code is used to check if the day off is on a weekend
				DayOfWeek dayOfWeek = startDate.getDayOfWeek();
				if (dayOfWeek != dayOfWeek.SATURDAY && dayOfWeek != dayOfWeek.SUNDAY) {
					dates.add(startDate);
				}
				startDate = startDate.plusDays(1);
			}

			if (dates.size() == 0) {
				throw new Exception("Você não pode solicitar um dayoff no final de semana");
			}
			
			for (LocalDate date : dates) {
				//This block of code sets the values of the day off and saves it in the database
				eventsModel.setDate(date);
				BeanUtils.copyProperties(eventsDto, eventsModel);
				// Always when we save a dayoff it is marked as pending in first time
				eventsModel.setStatus(StatusDayOff.PENDING);
				var userName = userRepository.findByIdUser(eventsModel.getIdUser());
				var userNameString = (userName.get().getFullName().toString().substring(0, userName.get().getFullName().indexOf(" ") + 2));
				eventsModel.setCardTitle(userNameString + ".");
				validateDayoffDto(eventsDto, eventsModel);
				ListDayoff.add(new EventsModel(eventsModel));
			}
			
			
			// Save the list of day offs requested
			emailService.sendEmailDayOffRequest(eventsDto);
			eventsRepository.saveAll(ListDayoff);
			return ListDayoff;
		} catch (StringIndexOutOfBoundsException e) {
			throw new Exception("Data inválida , use o formato yyyy-MM-dd ou yyyy-MM-dd/yyyy-MM-dd");

		} catch (DateTimeParseException e) {
			throw new Exception("Data inválida , use o formato yyyy-MM-dd ou yyyy-MM-dd/yyyy-MM-dd");
		}
	}

	void validateDayoffDto(EventsDto eventsDto, EventsModel eventsModel) {
		if (eventsDto.getTypeDay() == DayoffType.HALF_DAYOFF) {
			Time startTime = eventsDto.getStartTime();
			checkStartTimeWasDefined(eventsModel, startTime);
		} else if (eventsDto.getTypeDay() == DayoffType.DAYOFF) {
			// DAYOFF does not use start time parameter, so we can remove it
			eventsModel.setStartTime(null);
		}
	}

	public EventsModel updateDayOff(int eventId, UpdateEventsDto updateEventsDto) throws Exception {
		EventsModel eventsModel = eventsRepository.findById(eventId);
		UserModel userModel = userRepository.findById(eventsModel.getIdUser()).get();
		LocalDateTime now = LocalDateTime.now();
		if (!eventsModel.getDate().equals(updateEventsDto.getDate())) {
			if (updateEventsDto.getDate().isBefore(ChronoLocalDate.from(now.plusDays(3))) && !userServices.isAdmin()) {
				throw new Exception("Você não pode alterar um dayoff com menos de 72 horas de antecedência");
			}
			// Check if the user is the owner of the event or if he is an admin
		}

		if (!userServices.isAdmin() && eventsModel.getStatus().equals(StatusDayOff.APPROVED)) {
			throw new Exception("Você não pode alterar um dayoff aprovado !");
		}

		if (userServices.getLoggedUser().equals(userModel.getEmail()) || userServices.isAdmin()) {
			validateUpdateDayoffDto(updateEventsDto, eventsModel);
			BeanUtils.copyProperties(updateEventsDto, eventsModel);
			return eventsRepository.save(eventsModel);
		} else {
			throw new RuntimeException("Você não pode atualizar esse dayoff");
		}

	}

	void validateUpdateDayoffDto(UpdateEventsDto updateEventsDto, EventsModel eventsModel) {
		if (updateEventsDto.getTypeDay() == DayoffType.HALF_DAYOFF) {
			Time startTime = updateEventsDto.getStartTime();
			checkStartTimeWasDefined(eventsModel, startTime);
		} else if (updateEventsDto.getTypeDay() == DayoffType.DAYOFF) {
			// DAYOFF does not use start time parameter, so we can remove it
			updateEventsDto.setStartTime(null);
			eventsModel.setStartTime(null);
			eventsModel.setEndTime(null);
		}

		// If we edit anything in dayoff(except status and document) we should set
		// status as PENDING
		var shouldChangeStatus = (updateEventsDto.getTypeDay() != null
				&& updateEventsDto.getTypeDay() != eventsModel.getTypeDay())
				|| (updateEventsDto.getStartTime() != null
						&& !updateEventsDto.getStartTime().equals(eventsModel.getStartTime()))
				|| (updateEventsDto.getDate() != null && !updateEventsDto.getDate().equals(eventsModel.getDate()));

		if (shouldChangeStatus) {
			updateEventsDto.setStatus(StatusDayOff.PENDING);
		}
	}

	private void checkStartTimeWasDefined(EventsModel eventsModel, Time time) {
		Time startTime = Objects.requireNonNull(time, "Hora de inicio deve ser definida para meio dayoffs");

		// Half dayoffs are defined to have 4 hours of duration according to the rule
		LocalTime endTime = startTime.toLocalTime().plusHours(4);

		eventsModel.setEndTime(Time.valueOf(endTime));
	}

	public EventsModel getEvents(int eventId) {

		EventsModel eventsModel = eventsRepository.getEvent(eventId);

		return eventsModel;
	}

	public List<EventsModel> findAll() {
		return eventsRepository.findAll();
	}

	public List<EventsDto> getEventsByUser(UUID idUser) {
		List<EventsModel> result = eventsRepository.findByIdUser(idUser);

		return result.stream().map(event -> new EventsDto(event)).collect(Collectors.toList());
	}

	public void deleteDayOff(int eventId) {
		eventsRepository.deleteById(eventId);
	}

	public Page<EventsModel> findAllByStatus(String idUser, int page, int size, String direction, String status) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), "date"));
		if (idUser.isEmpty()) {
			return eventsRepository.findByStatus(status, pageable);
		} else {
			return eventsRepository.findByIdUserAndStatus(UUID.fromString(idUser), status, pageable);
		}
	}

	public void approveEvent(EventsApprove eventsApprove) throws MessagingException {
		EventsModel eventsModel = eventsRepository.findById(eventsApprove.getEventId());
		eventsModel.setStatus(eventsApprove.getStatusDayOff());
		eventsModel.setReason(eventsApprove.getReason());
		emailService.sendEmailDayOffApproval(eventsModel.getIdUser(), eventsModel.getEventId());
		eventsRepository.save(eventsModel);
	}
}