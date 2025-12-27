package com.chartmania.dto.auth;
import java.time.Instant;

public class AccessTokenResponseDTO {
    private String tokenType;
    private String accessToken;
    private Instant expiresAt;
    private int success;
    private String message;

    public AccessTokenResponseDTO(int success, String message, String tokenType, String accessToken,Instant expiresAt) {
        this.success = success;
        this.message = message;
        this.tokenType = tokenType;
        this.accessToken = accessToken;
        this.expiresAt = expiresAt;
    }

    public String getTokenType() {
        return tokenType;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public int getSuccess() {
        return this.success;
    }

    public String getMessage(){
        return this.message;
    }
    
    public Instant getExpiresAt(){
        return this.expiresAt;
    }

}
