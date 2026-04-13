package com.chartmania.dto.user;

import com.chartmania.model.User;

public class GetUserInfoResponseDTO {
    public String username;
    public String email;


    public GetUserInfoResponseDTO(String username, String email){
        this.username = username;
        this.email = email;   
    }

    public static GetUserInfoResponseDTO fromEntity(User user) {
        return new GetUserInfoResponseDTO(user.getUsername(),user.getEmail());
    }
}
