package com.chartmania.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.chartmania.dto.GenericResponseDTO;
import com.chartmania.dto.auth.LoginRequestDTO;
import com.chartmania.dto.auth.AccessTokenResponseDTO;
import com.chartmania.dto.auth.RegisterRequestDTO;
import com.chartmania.model.User;
import com.chartmania.service.AuthService;
import com.chartmania.service.JwtService;
import com.chartmania.util.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import com.chartmania.service.RefreshTokenService;

@RestController
@RequestMapping("/api/auth")
class AuthController {
    protected AuthService authService;
    protected CookieUtil cookieUtil;
    protected RefreshTokenService refreshTokenService;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;

    public AuthController(AuthService authService,
            CookieUtil cookieUtil,
            AuthenticationManager authManager, JwtService jwtService,
            RefreshTokenService refreshTokenService) {
        this.authManager = authManager;
        this.cookieUtil = cookieUtil;
        this.authService = authService;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    @ResponseBody
    @PostMapping("/register")
   

    public ResponseEntity<GenericResponseDTO> register(@Valid @RequestBody RegisterRequestDTO requestData) {
        GenericResponseDTO res = this.authService.createUser(requestData);
        return res.isSuccess()
                ? ResponseEntity.status(201).body(res)
                : ResponseEntity.badRequest().body(res);
    }

    @PostMapping("/login")
    public ResponseEntity<AccessTokenResponseDTO> loginAndGetToken(@Valid @RequestBody LoginRequestDTO requestData,
            HttpServletResponse response) {

        Authentication auth;
        try {
            auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestData.getUsername(), requestData.getPassword()));
        }

        catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.status(401).body(new AccessTokenResponseDTO(0, "Wrong credentials", "Bearer", ""));
        }

        try {
            // Access token
            String token = jwtService.generate(auth);

            // Refresh Token
            String refreshToken = refreshTokenService.generateRefreshToken(auth);
            cookieUtil.createRefreshCookie(response, refreshToken);

            return ResponseEntity.ok(new AccessTokenResponseDTO(1, "", "Bearer", token));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(new AccessTokenResponseDTO(0, "Server Error", "Bearer", ""));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<GenericResponseDTO> logout(@CookieValue(name = "refresh-token") String refreshToken, HttpServletResponse response) {
        if (refreshToken == null)
            return ResponseEntity.status(400).body(new GenericResponseDTO<>(false, ""));
            
        try {
            refreshTokenService.deleteRefreshToken(refreshToken);
            cookieUtil.clearRefreshCookie(response);
            return ResponseEntity.status(200).body(new GenericResponseDTO<>(true, ""));
        } catch (Exception e) {
            cookieUtil.clearRefreshCookie(response);
            return ResponseEntity.status(500).body(new GenericResponseDTO<>(false, "Internal Server Error"));
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AccessTokenResponseDTO> refreshToken(
            @CookieValue(name = "refresh-token") String refreshToken, HttpServletResponse response) {
        // Verify if refresh token is valid
        if(refreshToken == null)
            return ResponseEntity.status(400).body(new AccessTokenResponseDTO(0, "No refresh token found", "Bearer", ""));

        try {
            Boolean isTokenValid = refreshTokenService.isTokenValid(refreshToken);
            // If the token is no longer valid
            if (!isTokenValid) {
                refreshTokenService.deleteRefreshToken(refreshToken);
                cookieUtil.clearRefreshCookie(response);
                return ResponseEntity.status(401)
                        .body(new AccessTokenResponseDTO(0, "Refresh Token is not valid", "Bearer", ""));
            }

            User user = refreshTokenService.getUserFromToken(refreshToken);

            Authentication auth = new UsernamePasswordAuthenticationToken(
                    user.getUsername(),
                    null,
                    null);

            String token = jwtService.generate(auth);

            // Regenerate refresh token
            String newRefreshToken;

            refreshTokenService.deleteRefreshToken(refreshToken);
            newRefreshToken = refreshTokenService.generateRefreshToken(auth);

            cookieUtil.createRefreshCookie(response, newRefreshToken);

            return ResponseEntity.ok(new AccessTokenResponseDTO(1, "", "Bearer", token));
        } catch (Exception e) {
            cookieUtil.clearRefreshCookie(response);
            return ResponseEntity.status(500).body(new AccessTokenResponseDTO(0, "Server Error", "Bearer", ""));
        }
    }

}