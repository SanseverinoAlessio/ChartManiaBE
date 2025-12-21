package com.chartmania.dto.auth;

public class AccessTokenResponseDTO {
    private String tokenType;
    private String accessToken;
    private int success;
    private String message;

    public AccessTokenResponseDTO(int success, String message, String tokenType, String accessToken) {
        this.success = success;
        this.message = message;
        this.tokenType = tokenType;
        this.accessToken = accessToken;
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

}
