package com.simbioff.simbioff.controllers;

import com.simbioff.simbioff.dto.AuthResponseDTO;
import com.simbioff.simbioff.dto.LoginDto;
import com.simbioff.simbioff.dto.ResetPasswordDto;
import com.simbioff.simbioff.repositories.UserRepository;
import com.simbioff.simbioff.security.JWTGenerator;
import com.simbioff.simbioff.services.AuthServices;
import com.simbioff.simbioff.services.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.validation.Valid;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/auth")
public class AuthController {


    @Value("${spring.profiles.active}")
    private String activeProfile;
    @Autowired
    AuthServices authServices;

    @Autowired
    EmailService emailService;


    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private JWTGenerator jwtGenerator;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository,
                          PasswordEncoder passwordEncoder, JWTGenerator jwtGenerator) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtGenerator = jwtGenerator;
    }
    @Operation(summary = "Login", description = "Login", tags = {"Authentication"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação realizada com sucesso"
                    , content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Usuário ou senha inválido"
                    , content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @PostMapping(value = "/signup")
    public ResponseEntity login(@RequestBody @Valid LoginDto loginDto){

        try {
            loginDto.setEmail(loginDto.getEmail().toLowerCase());
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getEmail(),
                        loginDto.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtGenerator.generateToken(authentication);
            AuthResponseDTO authResponseDTO = new AuthResponseDTO(token);
            authResponseDTO.setIdUser(userRepository.findByEmail(loginDto.getEmail()).getIdUser());
            return new ResponseEntity<>(authResponseDTO, HttpStatus.OK);

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário ou senha inválido");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
        }

    }

    @PostMapping("/forgot-password")
    public ResponseEntity forgotPassword(@RequestParam String email) {

        String response = authServices.forgotPassword(email);
        String link = "";
        if (activeProfile.equals("dev")) {
            link = "http://localhost:3000/reset-password?token=";
        } else if (activeProfile.equals("prod")) {
            link = " https://simbioff.netlify.app/reset-password?token=";
        }
        if (!response.startsWith("Invalid") || userRepository.existsByEmail(email)) {
            var linkResetPassword = link + response;
            try {
				emailService.sendEmail(email, linkResetPassword);
			} catch (MessagingException e) {
				e.printStackTrace();
			}
            return ResponseEntity.ok("E-mail enviado");
        } else {
            return ResponseEntity.badRequest().body("E-mail não encontrado");
        }

    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestParam String token,
            @RequestBody @Valid ResetPasswordDto resetPassword) {

        if(resetPassword.getPassword().length() < 6) {
            return ResponseEntity.badRequest().body("A senha deve ter no mínimo 6 caracteres");
        }

        if (resetPassword.getPassword().equals(resetPassword.getPasswordConfirm())) {

            return authServices.resetPassword(token, resetPassword.getPassword());
        } else {
            return ResponseEntity.badRequest().body("As senhas não coincidem");
        }

    }

    @PostMapping("/resendEmailUser")
    public ResponseEntity<String> resendEmailUser(@RequestParam String email) {
        String link = "";

        if (userRepository.existsByEmail(email)) {
            if (activeProfile.equals("dev")) {
                link = "http://localhost:3000/create-password-new-user?token=";
            } else if (activeProfile.equals("prod")) {
                link = " https://simbioff.netlify.app/create-password-new-user?token=";
            }
            link += authServices.forgotPassword(email);
            emailService.sendEmailNewUser(email, link);
            return ResponseEntity.ok("E-mail enviado");
        } else {
            return ResponseEntity.badRequest().body("E-mail não encontrado");
        }
    }


}
