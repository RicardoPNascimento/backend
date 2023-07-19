package com.simbioff.simbioff.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class UpdatePasswordDto {

    private static final long serialVersionUID = 5526944616460827387L;

    @NotNull
    private String currentPassword;

    @Size(min = 5, max = 30, message = "password must be between 5 and 30 characters")
    private String password;
    
    @Size(min = 5, max = 30, message = "password must be between 5 and 30 characters")
    private String passwordConfirm;

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

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
