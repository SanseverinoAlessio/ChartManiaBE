package com.chartmania.integration;

import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import com.chartmania.dto.user.UpdateUserInfoRequestDTO;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = "/tests/usercontrollerintegration/user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/tests/usercontrollerintegration/user-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class UserControllerIntegrationTest {

        private static final long USER_ID = 1;
        private static final String USER_USERNAME = "user_controller_test";
        private static final String USER_EMAIL = "user_controller_test@test.it";
        private static final String USER_PASSWORD = "password";

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        void testGetUserInfo() throws Exception {
                mockMvc.perform(get("/api/personal-area/users/me")
                                .with(jwt().jwt(jwt -> jwt.claim("userId", USER_ID))))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success", is(true)))
                                .andExpect(jsonPath("$.data.username", is(USER_USERNAME)))
                                .andExpect(jsonPath("$.data.email", is(USER_EMAIL)));
        }

        @Test
        void testGetUserInfoUnauthorized() throws Exception {
                mockMvc.perform(get("/api/personal-area/users/me"))
                                .andExpect(status().isUnauthorized());
        }

        @Test
        void testUpdateUserInfo() throws Exception {
                UpdateUserInfoRequestDTO request = new UpdateUserInfoRequestDTO(
                                "updated_username", "updated@test.it", null, null);

                mockMvc.perform(put("/api/personal-area/users/me")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .with(jwt().jwt(jwt -> jwt.claim("userId", USER_ID))))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success", is(true)))
                                .andExpect(jsonPath("$.data.username", is("updated_username")))
                                .andExpect(jsonPath("$.data.email", is("updated@test.it")));
        }

        @Test
        void testUpdateUserInfoWithPasswordChange() throws Exception {
                UpdateUserInfoRequestDTO request = new UpdateUserInfoRequestDTO(
                                USER_USERNAME, USER_EMAIL, USER_PASSWORD, "newpassword123");

                mockMvc.perform(put("/api/personal-area/users/me")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .with(jwt().jwt(jwt -> jwt.claim("userId", USER_ID))))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success", is(true)));
        }

        @Test
        void testUpdateUserInfoWithWrongOldPassword() throws Exception {
                UpdateUserInfoRequestDTO request = new UpdateUserInfoRequestDTO(
                                USER_USERNAME, USER_EMAIL, "wrongpassword", "newpassword123");

                mockMvc.perform(put("/api/personal-area/users/me")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .with(jwt().jwt(jwt -> jwt.claim("userId", USER_ID))))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.success", is(false)))
                                .andExpect(jsonPath("$.message", is("Old password is incorrect")));
        }

        @Test
        void testUpdateUserInfoValidationFail() throws Exception {
                UpdateUserInfoRequestDTO request = new UpdateUserInfoRequestDTO(
                                "", "not-a-valid-email", null, null);

                mockMvc.perform(put("/api/personal-area/users/me")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .with(jwt().jwt(jwt -> jwt.claim("userId", USER_ID))))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void testUpdateUserInfoUnauthorized() throws Exception {
                UpdateUserInfoRequestDTO request = new UpdateUserInfoRequestDTO(
                                USER_USERNAME, USER_EMAIL, null, null);

                mockMvc.perform(put("/api/personal-area/users/me")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isUnauthorized());
        }
}