package com.simbioff.simbioff.dto;

import javax.validation.constraints.Size;

public class ResetPasswordDto {

    private static final long serialVersionUID = 5526944616460827387L;
    
    @Size(min = 5, max = 30, message = "Senha deve ter entre 5 a 30 caracteres")
    private String password;
    
    @Size(min = 5, max = 30, message = "Confirmar senha deve ter entre 5 a 30 caracteres")
    private String passwordConfirm;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }
}
