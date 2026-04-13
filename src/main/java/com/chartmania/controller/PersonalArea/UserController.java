package com.chartmania.controller.PersonalArea;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chartmania.dto.GenericResponseDTO;
import com.chartmania.dto.user.UpdateUserInfoRequestDTO;
import com.chartmania.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/personal-area")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("users/me")
    public ResponseEntity<GenericResponseDTO> getUserInfo(@AuthenticationPrincipal Jwt jwt) {
        Long userId = jwt.getClaim("userId");
        GenericResponseDTO response = userService.getUserInfo(userId);
        return ResponseEntity.status(200).body(response);
    }

    @PutMapping("users/me")
    public ResponseEntity<GenericResponseDTO> updateUserInfo(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody UpdateUserInfoRequestDTO requestData) {
        Long userId = jwt.getClaim("userId");
        GenericResponseDTO response = userService.updateUserInfo(userId, requestData);
        return response.isSuccess()
                ? ResponseEntity.status(200).body(response)
                : ResponseEntity.badRequest().body(response);
    }

}
