package com.chartmania.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.chartmania.dto.GenericResponseDTO;
import com.chartmania.dto.auth.RegisterRequestDTO;
import com.chartmania.model.User;
import com.chartmania.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class AuthService {
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
