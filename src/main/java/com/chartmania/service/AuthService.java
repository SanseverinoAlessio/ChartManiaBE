package com.chartmania.service;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.chartmania.dto.GenericResponseDTO;
import com.chartmania.dto.auth.AccessTokenGeneratedDTO;
import com.chartmania.dto.auth.RegisterRequestDTO;
import com.chartmania.model.User;
import com.chartmania.repository.UserRepository;
import com.chartmania.dto.auth.RefreshSessionResultDTO;


import jakarta.transaction.Transactional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,RefreshTokenService refreshTokenService,JwtService jwtService) {
        this.userRepository = userRepository;
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
    }


    public RefreshSessionResultDTO refreshSession(String refreshToken) throws Exception{
        // If the token is no longer valid
            Boolean isTokenValid = refreshTokenService.isTokenValid(refreshToken);
            if (!isTokenValid) {
                this.refreshTokenService.deleteRefreshToken(refreshToken);
                throw new Exception("Session expired");
            }

            User user = refreshTokenService.getUserFromToken(refreshToken);
            Authentication auth = new UsernamePasswordAuthenticationToken(
                    user.getUsername(),
                    null,
                    null);

            AccessTokenGeneratedDTO token = jwtService.generate(auth);
            refreshTokenService.deleteRefreshToken(refreshToken);

            // Regenerate refresh token
            String newRefreshToken = refreshTokenService.generateRefreshToken(auth);
        
            //Return qui
            return new RefreshSessionResultDTO(newRefreshToken,token.getToken(),token.getExpiresAt());
    }

    @Transactional 
    public GenericResponseDTO createUser(RegisterRequestDTO data) {
        //TODO: bisogna fare in modo che non si possa salvare uno username uguale
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(data.getPassword());
        User user = new User(
                data.getUsername(),
                data.getEmail(),
                hashedPassword);
        try {
            userRepository.save(user);
            return new GenericResponseDTO(true, "User created");
        } catch (Exception e) {
            return new GenericResponseDTO(true, "couldn't create user");
        }
    }


}
