package com.chartmania.dto.auth;
import java.time.Instant;

public class RefreshSessionResultDTO {
    private String refreshToken;
    private String accessToken;
    private Instant expiresAt;

    public RefreshSessionResultDTO() {

    }

    public RefreshSessionResultDTO(String refreshToken, String accessToken, Instant expiresAt) {
        this.refreshToken = refreshToken;
        this.accessToken = accessToken;
        this.expiresAt = expiresAt;
    }

    public void setRefreshToken(String refreshToken){
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return this.refreshToken;
    }

    public void setAccessToken(String accessToken){
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public void setExpiresAt(Instant expiresAt){
        this.expiresAt = expiresAt;
    }


    public Instant getExpiresAt() {
        return this.expiresAt;
    }

}
