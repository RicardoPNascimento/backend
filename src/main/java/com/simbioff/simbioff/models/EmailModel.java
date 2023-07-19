package com.simbioff.simbioff.models;

import com.simbioff.simbioff.enums.StatusEmail;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "email")
public class EmailModel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long emailId;

    private String emailFrom; // email do usuário
    private String emailTo; // email do destinatário

    private LocalDateTime sendDateEmail;
    @Enumerated(EnumType.STRING)
    private StatusEmail statusEmail;


}
