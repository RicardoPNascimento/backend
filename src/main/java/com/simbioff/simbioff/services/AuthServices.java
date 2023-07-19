package com.simbioff.simbioff.services;

import com.simbioff.simbioff.models.UserModel;
import com.simbioff.simbioff.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthServices {

    private static final long EXPIRE_TOKEN_AFTER_MINUTES = 30;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthServices authServices;

    @Autowired
    private EmailService emailService;


    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Async
    public String forgotPassword(String email) {

        Optional<UserModel> userOptional = Optional
                .ofNullable(userRepository.findByEmail(email));

        if (!userOptional.isPresent()) {
            return "Invalid email id.";
        }

        UserModel user = userOptional.get();
        user.setToken(generateToken());
        user.setTokenCreationDate(LocalDateTime.now());

        user = userRepository.save(user);

        return user.getToken();
    }

    public ResponseEntity resetPassword(String token, String password) {

        Optional<UserModel> userOptional = Optional
                .ofNullable(userRepository.findByToken(token));

        if (!userOptional.isPresent()) {
            return ResponseEntity.badRequest().body("Token inválido");
        }

        LocalDateTime tokenCreationDate = userOptional.get().getTokenCreationDate();

        if (isTokenExpired(tokenCreationDate)) {
            return ResponseEntity.badRequest().body("Token expirado");

        }

        UserModel user = userOptional.get();

        user.setPassword(passwordEncoder.encode(password));
        user.setToken(null);
        user.setTokenCreationDate(null);

        userRepository.save(user);

        return ResponseEntity.ok("Senha alterada com sucesso");
    }

    /**
     * Generate unique token. You may add multiple parameters to create a strong
     * token.
     *
     * @return unique token
     */
    private String generateToken() {
        StringBuilder token = new StringBuilder();

        return token.append(UUID.randomUUID().toString()).toString();
    }

    /**
     * Check whether the created token expired or not.
     *
     * @param tokenCreationDate
     * @return true or false
     */
    private boolean isTokenExpired(final LocalDateTime tokenCreationDate) {

        LocalDateTime now = LocalDateTime.now();
        Duration diff = Duration.between(tokenCreationDate, now);

        return diff.toMinutes() >= EXPIRE_TOKEN_AFTER_MINUTES;
    }

    public void mailNewUser(String email) {

        String response = authServices.forgotPassword(email);
        String link = "";
        if (activeProfile.equals("dev")) {
            link = "http://localhost:3000/create-password-new-user?token=";
        } else if (activeProfile.equals("prod")) {
            link = " https://simbioff.netlify.app/create-password-new-user?token=";
        }
        if (!response.startsWith("Invalid") || userRepository.existsByEmail(email)) {
            var linkResetPassword = link + response;
            emailService.sendEmailNewUser(email, linkResetPassword);

        }
    }
}


