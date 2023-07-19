package com.simbioff.simbioff.dto;


import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class EmailDto {


    @NotBlank
    @Email
    public String emailFrom;
    @NotBlank
    @Email
    public String emailTo;


}
