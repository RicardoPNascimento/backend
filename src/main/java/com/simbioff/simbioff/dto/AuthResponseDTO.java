package com.simbioff.simbioff.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class AuthResponseDTO {
    private String accessToken;
    private UUID idUser;

    public AuthResponseDTO(String accessToken) {
        this.accessToken = accessToken;
    }
}
