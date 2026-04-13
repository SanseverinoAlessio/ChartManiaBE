package com.chartmania.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.chartmania.dto.GenericResponseDTO;
import com.chartmania.dto.user.GetUserInfoResponseDTO;
import com.chartmania.dto.user.UpdateUserInfoRequestDTO;
import com.chartmania.model.User;
import com.chartmania.repository.UserRepository;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    
    public GenericResponseDTO<GetUserInfoResponseDTO> getUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        GetUserInfoResponseDTO userInfo = GetUserInfoResponseDTO.fromEntity(user);
        return new GenericResponseDTO<>(true, null, userInfo);
    }

    public GenericResponseDTO<GetUserInfoResponseDTO> updateUserInfo(Long userId, UpdateUserInfoRequestDTO requestData) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        user.setUsername(requestData.getUsername());
        user.setEmail(requestData.getEmail());

        // Se viene fornita una nuova password, verifica la vecchia e aggiorna
        if (requestData.getPassword() != null && !requestData.getPassword().isEmpty()) {
            if (requestData.getOldPassword() == null || requestData.getOldPassword().isEmpty()) {
                return new GenericResponseDTO<>(false, "Old password is required to change password");
            }

            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            if (!passwordEncoder.matches(requestData.getOldPassword(), user.getPassword())) {
                return new GenericResponseDTO<>(false, "Old password is incorrect");
            }

            String hashedPassword = passwordEncoder.encode(requestData.getPassword());
            user.setPassword(hashedPassword);
        }

        try {
            userRepository.save(user);
            GetUserInfoResponseDTO userInfo = GetUserInfoResponseDTO.fromEntity(user);
            return new GenericResponseDTO<>(true, "User updated successfully", userInfo);
        } catch (Exception e) {
            return new GenericResponseDTO<>(false, "Could not update user");
        }
    }
}
