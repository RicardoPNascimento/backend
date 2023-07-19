package com.simbioff.simbioff.controllers;

import com.simbioff.simbioff.dto.DaysLeftDto;
import com.simbioff.simbioff.dto.EventsApprove;
import com.simbioff.simbioff.dto.EventsDto;
import com.simbioff.simbioff.dto.UpdateEventsDto;
import com.simbioff.simbioff.models.EventsModel;
import com.simbioff.simbioff.models.UserModel;
import com.simbioff.simbioff.services.BackBlazeServices;
import com.simbioff.simbioff.services.EventsServices;
import com.simbioff.simbioff.services.UserServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.simbioff.simbioff.enums.ResponseStatus.NOT_FOUND;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@Slf4j
@RequestMapping("/events")
public class EventController {

    @Autowired
    EventsServices eventsServices;

    DaysLeftDto daysLeftDto;

    @Autowired
    UserServices userServices;

    @Autowired
    BackBlazeServices backBlazeServices;

    @Operation(summary = "Registrar dayoff", description = "Registrar dayoff", tags = {"Events"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação realizada com sucesso"
                    , content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Usuário ou senha inválido"
                    , content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })

    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<Object> createEvent(@Valid @RequestPart EventsDto eventsDto, @RequestParam("arquivo") MultipartFile atestado) {

        Optional<UserModel> userModel = userServices.findById(eventsDto.getIdUser());
        Optional<UserModel> loggedUser = userServices.findByEmail(userServices.getLoggedUser());

        if (!userModel.get().getEmail().equals(loggedUser.get().getEmail()) && !userServices.isAdmin()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não autorizado");
        }


        try {
            if (!userServices.existsByID(eventsDto.getIdUser())) {
                return ResponseEntity.badRequest().body("Usuário não encontrado");
            }

            if (atestado.getContentType().equals("text-plain")) {
                eventsDto.setHasAtestado(false);
            } else {
                eventsDto.setHasAtestado(true);
            }

            List<EventsModel> events = eventsServices.saveDayoff(eventsDto);

            if (!atestado.getContentType().equals("text-plain")) {

                backBlazeServices.uploadFile(atestado, false, events);
            }

            return ResponseEntity.ok().body("Dayoff registrado");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Update a day off", description = "Update a day off", tags = {"Events"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação realizada com sucesso"
                    , content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Erro ao atualizar Dayoff"
                    , content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Dayoff não encontrado")
    })
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @PatchMapping(value = "/{event_id}")
    public ResponseEntity<Object> updateEvent(@PathVariable("event_id") int eventId,
                                              @Valid @RequestPart UpdateEventsDto updateEventsDto, @RequestParam("arquivo") MultipartFile atestado) {

        Optional<EventsModel> eventOptional = eventsServices.findEventById(eventId);
        Optional<UserModel> userOptional = userServices.findById(eventOptional.get().getIdUser());

        if (!userOptional.get().getEmail().equals(userServices.getLoggedUser()) && !userServices.isAdmin()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não autorizado");
        }


        if (eventOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Dayoff não encontrado");
        }
        try {
            if (atestado.getContentType().equals("text-plain") && !eventOptional.get().getHasAtestado()) {
                updateEventsDto.setHasAtestado(false);
            } else {
                updateEventsDto.setHasAtestado(true);
            }

            EventsModel dayoff = eventsServices.updateDayOff(eventId, updateEventsDto);
            if (!atestado.getContentType().equals("text-plain")) {
                List<EventsModel> list = new ArrayList<>();
                list.add(dayoff);
                backBlazeServices.uploadFile(atestado, false, list);
            }
            return ResponseEntity.ok().body("Dayoff atualizado com sucesso");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Excluir dayoff", description = "Excluir dayoff", tags = {"Events"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação realizada com sucesso"
                    , content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Erro ao excluir dayoff"
                    , content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Dayoff não encontrado")
    })
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @DeleteMapping(value = "/{eventId}")
    public ResponseEntity<Object> deleteEvent(@PathVariable("eventId") int eventId) {


        Optional<EventsModel> eventOptional = eventsServices.findEventById(eventId);
        Optional<UserModel> userOptional = userServices.findById(eventOptional.get().getIdUser());

        if (!userOptional.get().getEmail().equals(userServices.getLoggedUser()) && !userServices.isAdmin()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não autorizado");
        }

        if (eventOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Dayoff não encontrado");
        }

        try {
            eventsServices.deleteDayOff(eventId);
            return ResponseEntity.ok().body("Dayoff excluido com sucesso");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Listar dayoff por id", description = "Listar dayoff por id", tags = {"Events"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação realizada com sucesso"
                    , content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Erro ao buscar dayoff"
                    , content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Dayoff não encontrado")
    })
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/{eventId}")
    public ResponseEntity<Object> getEvents(@PathVariable("eventId") int eventId) {
        try {
            if (!eventsServices.existsByID(eventId)) {
                return ResponseEntity.status(NOT_FOUND.getStatusCode()).body("Dayoff não encontrado");
            }

            return ResponseEntity.ok().body(eventsServices.getEvents(eventId));
        } catch (Exception e) {
            log.warn(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Listar todos dayoffs", description = "Listar todos dayoffs", tags = {"Events"})
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<Object> getAllEvents(@RequestParam(value = "idUser", defaultValue = "") UUID idUser) {
        try {
            if (idUser != null && !idUser.toString().isEmpty()) {

                return ResponseEntity.ok().body(eventsServices.getEventsByUser(idUser));
            }
            var eventsDtolist = eventsServices.findAll();
            return ResponseEntity.ok().body(eventsDtolist);

        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body("Erro ao buscar dayoffs");

        }
    }

    @Operation(summary = "Listar todos dayoffs pendentes", description = "Listar todos dayoffs pendentes", tags = {"Events"})
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/approve")
    public ResponseEntity<Object> getPendingEvents(
            @RequestParam(value = "status", defaultValue = "PENDING") String status,
            @RequestParam(value = "idUser", defaultValue = "") String idUser,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "25") int size,
            @RequestParam(value = "sortDirection", defaultValue = "ASC") String sort) {
        try {
            Page<EventsModel> eventsModel = eventsServices.findAllByStatus(idUser, page, size, sort, status);
            return ResponseEntity.ok().body(eventsModel);

        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body("Erro ao buscar dayoffs");

        }
    }

    @Operation(summary = "Aprovar dayoff", description = "Aprovar dayoff", tags = {"Events"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação realizada com sucesso"
                    , content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Erro ao aprovar dayoff"
                    , content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Dayoff não encontrado")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(value = "/approve")
    public ResponseEntity<Object> approveEvent(@Valid @RequestBody EventsApprove eventsApprove) {
        try {
            if (!userServices.existsByID(eventsApprove.getIdUser())) {
                return ResponseEntity.badRequest().body("Usuário não encontrado");
            } else if (!eventsServices.existsByID(eventsApprove.getEventId())) {
                return ResponseEntity.badRequest().body("Dayoff não encontrado");
            } else {
                eventsServices.approveEvent(eventsApprove);
                return ResponseEntity.ok().body("Dayoff atualizado com sucesso");

            }


        } catch (Exception f) {
            log.error(f.getMessage());
            return ResponseEntity.badRequest().body("Erro ao aprovar dayoff");
        }
    }
}
