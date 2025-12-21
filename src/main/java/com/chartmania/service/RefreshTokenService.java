package com.chartmania.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.keygen.Base64StringKeyGenerator;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.stereotype.Service;

import com.chartmania.model.RefreshToken;
import com.chartmania.model.User;
import com.chartmania.repository.RefreshTokenRepository;
import com.chartmania.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    @Value("${jwt.refresh.token.exp-minutes}")
    private long expMinutesRefreshToken;
    private final UserRepository userRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    /** Refresh Tokens */
    @Transactional 
    public String generateRefreshToken(Authentication auth) throws Exception {
        Instant expiry = Instant.now().plus(this.expMinutesRefreshToken, ChronoUnit.MINUTES);
        StringKeyGenerator generator = new Base64StringKeyGenerator(32);
        String refreshToken = generator.generateKey();
        String username = auth.getName();
        User user = userRepository.findByUsername(username);

        try {
            // Get the user id
            // long userId = user.getId();
            // Delete old tokens
            // deleteRefreshTokens(userId);
        } catch (Exception e) {
            throw new Exception("An error occurs during refresh token creation: " + e.getMessage());
        }

        try {
            RefreshToken newToken = new RefreshToken(refreshToken, expiry, user);
            this.refreshTokenRepository.save(newToken);
            return refreshToken;
        } catch (Exception e) {
            throw new Exception("An error occurs during refresh token creation: " + e.getMessage());
        }
    }

    public boolean isTokenValid(String refreshToken) throws Exception {
        try {
            RefreshToken refreshTokenRow = refreshTokenRepository.findByToken(refreshToken);
            if (refreshTokenRow == null) {
                return false;
            }

            // Verify if still valid
            Instant now = Instant.now();
            return !now.isAfter(refreshTokenRow.getExpiryDate());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public User getUserFromToken(String tokenString) throws Exception {
        RefreshToken token = refreshTokenRepository.findByToken(tokenString);
        if (token == null) {
            throw new Exception("Token not found");
        }
        return token.getUser();
    }

    @Transactional 
    public void deleteRefreshToken(String refreshToken) throws Exception {
        try {
            refreshTokenRepository.deleteByToken(refreshToken);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public void deleteRefreshTokens(long userId) throws Exception {
        try {
            refreshTokenRepository.deleteByUserId(userId);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

}
