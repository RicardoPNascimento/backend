package com.simbioff.simbioff.services;

import com.simbioff.simbioff.dto.EventsDto;
import com.simbioff.simbioff.enums.DayoffType;
import com.simbioff.simbioff.enums.StatusEmail;
import com.simbioff.simbioff.helpers.EmailMessageBuilder;
import com.simbioff.simbioff.models.EmailModel;
import com.simbioff.simbioff.models.UserModel;
import com.simbioff.simbioff.repositories.EmailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class EmailService {

    @Autowired
    EmailRepository emailRepository;

    @Autowired
    JavaMailSender emailSender;

    @Autowired
    UserServices userServices;


    //emailFromServer this is the email that will be sent to the user
    public static final String emailFromServer = "rafael.santos@simbioseventures.com";

    //emailAdmin this is the email that will receive the emails from the users
    public static final String emailAdmin = "rafael.mg.bh@gmail.com";

    //Change this value if you need test email in your local machine
    public static Boolean sendEmail = false;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    public void sendEmail(String email, String linkResetPassword) throws MessagingException {
        EmailModel emailModel = new EmailModel();
        emailModel.setSendDateEmail(LocalDateTime.now());
        emailModel.setEmailFrom(emailFromServer);
        emailModel.setEmailTo(email);
        Optional<UserModel> user = userServices.findByEmail(email);

        try {
            MimeMessage mimeMessage = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");


            helper.setText(EmailMessageBuilder.buildRecoverPasswordMessage(user.get().getFullName(), linkResetPassword), true);
            helper.setTo(email);
            helper.setSubject("Simbioff - Reset Password");
            helper.setFrom(emailModel.getEmailFrom());
            emailSender.send(mimeMessage);
            emailModel.setStatusEmail(StatusEmail.SENT);
        } catch (MailException e) {
            emailModel.setStatusEmail(StatusEmail.ERROR);
        } finally {
            emailRepository.save(emailModel);
        }
    }

    public void sendEmailDayOffRequest(EventsDto eventsDto) throws MessagingException {

        if (sendEmail || activeProfile.equals("prod")) {
            String dayoffType = eventsDto.getTypeDay().equals(DayoffType.DAYOFF) ? "day off" : "meio dayoff";
            String link = activeProfile.equals("prod") ? "https://simbioff.netlify.app/dayoff-approval?id=" + eventsDto.getIdUser() : "http://localhost:3000/dayoff-approval?id=" + eventsDto.getIdUser();
            Optional<UserModel> userModel = userServices.findById(eventsDto.getIdUser());
            EmailModel emailModel = new EmailModel();
            emailModel.setSendDateEmail(LocalDateTime.now());
            emailModel.setEmailFrom(emailFromServer);
            emailModel.setEmailTo(emailAdmin);
            try {
                MimeMessage mimeMessage = emailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
                helper.setText(EmailMessageBuilder.buildRequestDayoffMessage(userModel.get().getFullName(), link, dayoffType), true);
                helper.setTo(emailAdmin);
                helper.setSubject("Simbioff - Requisição de " + dayoffType + " por " + userModel.get().getFullName());
                helper.setFrom(emailModel.getEmailFrom());
                emailSender.send(mimeMessage);
            } catch (MailException e) {
                throw new RuntimeException("Erro ao enviar o E-mail");
            }
        }

    }

    public void sendEmailDayOffApproval(UUID idUser, int eventId) throws MessagingException {
        if (sendEmail || activeProfile.equals("prod")) {
            Optional<UserModel> userModel = userServices.findById(idUser);
            EmailModel emailModel = new EmailModel();
            emailModel.setSendDateEmail(LocalDateTime.now());
            emailModel.setEmailFrom(emailFromServer);
            emailModel.setEmailTo(userModel.get().getEmail());

            String link = activeProfile.equals("prod") ? "https://simbioff.netlify.app/profile?id=" + eventId : "http://localhost:3000/profile?id=" + idUser;

            try {
                MimeMessage mimeMessage = emailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
                helper.setText(EmailMessageBuilder.buildDayoffStatusChanged(userModel.get().getFullName(), link), true);
                helper.setTo(userModel.get().getEmail());
                helper.setSubject("Simbioff - Houve uma atualização no seu pedido de day off");
                helper.setFrom(emailFromServer);
                emailSender.send(mimeMessage);
            } catch (MailException e) {
                throw new RuntimeException("Erro ao enviar o E-mail");
            }
        }
    }

    public void sendEmailNewUser(String email, String linkResetPassword) {
        EmailModel emailModel = new EmailModel();
        emailModel.setSendDateEmail(LocalDateTime.now());
        emailModel.setEmailFrom(emailFromServer);
        emailModel.setEmailTo(email);
        Optional<UserModel> user = userServices.findByEmail(email);

        try {
            MimeMessage mimeMessage = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");


            helper.setText(EmailMessageBuilder.buildNewUserMessage(user.get().getFullName(), linkResetPassword), true);
            helper.setTo(email);
            helper.setSubject("Simbioff - Criação novo usuario");
            helper.setFrom(emailModel.getEmailFrom());
            emailSender.send(mimeMessage);
            emailModel.setStatusEmail(StatusEmail.SENT);
        } catch (MailException | MessagingException e) {
            emailModel.setStatusEmail(StatusEmail.ERROR);
        } finally {
            emailRepository.save(emailModel);
        }
    }
}
