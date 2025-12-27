package com.chartmania.dto.auth;
import java.time.Instant;

import jakarta.validation.constraints.NotBlank;

public class AccessTokenGeneratedDTO {
    private String token;
    private Instant expiresAt;

    public AccessTokenGeneratedDTO(String token, Instant expiresAt){
        this.token = token;
        this.expiresAt = expiresAt;
    }

    public String getToken(){
        return this.token;
    }

    public Instant getExpiresAt(){
        return this.expiresAt;
    }

   
}
