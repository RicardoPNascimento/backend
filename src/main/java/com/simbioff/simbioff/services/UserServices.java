package com.simbioff.simbioff.services;

import com.simbioff.simbioff.dto.UserListDto;
import com.simbioff.simbioff.enums.UserActive;
import com.simbioff.simbioff.models.UserModel;
import com.simbioff.simbioff.repositories.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.text.ParseException;
import java.util.*;

@Service
public class UserServices {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventsServices eventsService;

    public List<UserModel> findAll() {
        return userRepository.findAll();
    }

    public Page<UserModel> findAllPage(int page, int size, String direction, Boolean enabled) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), "fullName"));
        return userRepository.findEnableUsers(pageable, enabled);
    }

    public Page<UserModel> findAllUserByName(int page, int size, String direction, String name, boolean status) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), "fullName"));
        if (status) {
            return userRepository.findByKeywordTrue(name, pageable);
        } else {
            return userRepository.findByKeywordFalse(name, pageable);
        }

    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    public boolean existsByCpf(String cpf) {
    	return userRepository.existsByCpf(cpf);
    }
    
    public boolean existsByPixKey(String pixKey) {
    	return userRepository.existsByPixKey(pixKey);
    }
    
    public boolean existsByPhone(String phone) {
		return userRepository.existsByPhone(phone);
	}

    public boolean existsByID(UUID id) {
        return userRepository.existsById(id);
    }

    public String encodePassword(String password) {
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put("pbkdf2", new Pbkdf2PasswordEncoder());
        DelegatingPasswordEncoder passwordEncoder = new DelegatingPasswordEncoder("pbkdf2", encoders);
        passwordEncoder.setDefaultPasswordEncoderForMatches(new Pbkdf2PasswordEncoder());

        String encrypt_password = passwordEncoder.encode(password);
        return encrypt_password;

    }
    @Transactional
    public UserModel save(UserModel userModel) {
        return userRepository.save(userModel);
    }


    public void deleteByEmail(String email) {
        userRepository.deleteByEmail(email);
    }

    @Transactional
    public Optional<UserModel> findByEmail(String email) {
        return Optional.ofNullable(userRepository.findByEmail(email));
    }

    public Optional<UserModel> findById(UUID id) {
        return userRepository.findById(id);
    }

    

    public List<UserListDto> getUserLIst() {
        List<UserModel> users = userRepository.findAll();
        List<UserListDto> listDto = new ArrayList<>();

        for (UserModel user : users) {
            UserListDto dto = new UserListDto();
            if (user.getEnabled() == true) {
                dto.setEnabled(UserActive.ACTIVE);
            } else {
                dto.setEnabled(UserActive.INACTIVE);
            }
            BeanUtils.copyProperties(user, dto);
            listDto.add(dto);
        }
        return listDto;
    }
    
    public String getLoggedUser() {
    	
    	UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return user.getUsername();
    }
    
    public boolean isAdmin() {
    	
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    	
    	boolean isAdmin = auth.getAuthorities().stream().anyMatch(user -> user.getAuthority().equals("ROLE_ADMIN"));
    	return isAdmin;
    }

	
    
//    @Scheduled(cron = "0 20 10 * * *")
//    public void updateDayOffs() throws ParseException, Exception {
//    	List<UserModel> users = userRepository.findAll();
//    	
//    	for(UserModel user : users) {
//    		user.setDayOffsAvailable(eventsService.dayOffsLeft(eventsService.daysWorking(user.getIdUser()), eventsService.countDayOffRequest(user.getIdUser())));
//    		userRepository.save(user);
//    	}
//    }
}