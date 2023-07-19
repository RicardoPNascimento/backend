package com.simbioff.simbioff.dto;

import lombok.Data;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
public class LoginDto implements java.io.Serializable {

    private static final long serialVersionUID = 1L;


    @Email(message = "E-mail inválido")
    private String email;
    @NotNull(message = "Senha é obrigatório")
    private String password;
}