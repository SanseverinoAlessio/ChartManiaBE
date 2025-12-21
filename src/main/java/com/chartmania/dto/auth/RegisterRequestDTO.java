package com.chartmania.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequestDTO {
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 30, message = "Username must be 3-30 chars")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 72, message = "Password must be 8-72 chars")
    private String password;

    @NotBlank(message = "Repeat password is required")
    @JsonProperty("repeat_password")
    private String repeatPassword;

    public RegisterRequestDTO(
            String username,
            String email,
            String password,
            @JsonProperty("repeat_password") String repeatPassword) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.repeatPassword = repeatPassword;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getRepeatPassword() {
        return repeatPassword;
    }

}
