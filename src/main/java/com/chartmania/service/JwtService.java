package com.chartmania.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.chartmania.model.RefreshToken;
import com.chartmania.model.User;
import com.chartmania.repository.RefreshTokenRepository;
import com.chartmania.repository.UserRepository;
import org.springframework.security.crypto.keygen.Base64StringKeyGenerator;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import com.chartmania.dto.auth.AccessTokenGeneratedDTO;

@Service
public class JwtService {
    private final JwtEncoder encoder;
    private final String issuer;
    private final long expMinutes;

    public JwtService(
            JwtEncoder encoder,
            @Value("${jwt.issuer}") String issuer,
            @Value("${jwt.exp-minutes}") long expMinutes,
            UserRepository userRepository) {
        this.encoder = encoder;
        this.issuer = issuer;
        this.expMinutes = expMinutes;
    }

    /** Access Token */
    public AccessTokenGeneratedDTO generate(Authentication auth) throws Exception {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(this.expMinutes, ChronoUnit.MINUTES);
        try {
            JwtClaimsSet claims = JwtClaimsSet.builder().issuer(this.issuer).subject(auth.getName()).issuedAt(now)
                    .expiresAt(expiresAt).build();
            JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

            String token = encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
            return new AccessTokenGeneratedDTO(token,expiresAt);
        } catch (Throwable e) {
            System.out.println(e);
            throw new Exception("An error occurs during access token creation: " + e.getMessage());
        }
    }

}
