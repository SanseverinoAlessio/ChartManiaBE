package com.chartmania.integration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import com.chartmania.dto.auth.LoginRequestDTO;
import com.chartmania.dto.auth.RegisterRequestDTO;
import com.chartmania.model.User;
import com.chartmania.repository.UserRepository;
import tools.jackson.databind.ObjectMapper;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = "/tests/authcontrollerintegration/user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/tests/authcontrollerintegration/user-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;
 
    @Autowired
    private org.springframework.core.env.Environment environment;


    //Registration
    private static final String REGISTER_USERNAME = "test";
    private static final String REGISTER_PASSWORD = "password";
    private static final String REGISTER_EMAIL = "test2@test.it";

    //Login
    private static final String LOGIN_USERNAME = "test_login";
    private static final String LOGIN_PASSWORD = "password";


    @Test
    public void testRegistration() throws Exception {
        RegisterRequestDTO registerDto = new RegisterRequestDTO(REGISTER_USERNAME, REGISTER_EMAIL, REGISTER_PASSWORD,
                REGISTER_PASSWORD);
        mockMvc.perform(
                post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isCreated());

        // Verifica se effettivamente l'utente esiste
        User user = userRepository.findByUsername("test");
        assertNotNull(user);
        assertEquals(user.getUsername(), "test");
    }

    
    @Test
    public void testLogin() throws Exception {
        LoginRequestDTO loginRequestDto = new LoginRequestDTO(LOGIN_USERNAME, LOGIN_PASSWORD);
        mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(cookie().exists("refresh-token"))
                .andExpect(cookie().httpOnly("refresh-token", true))
                .andExpect(jsonPath("$.accessToken", notNullValue()));
    }

    @Test
    public void testCheckUsernameExistsWhenUserExists() throws Exception {
        String username = "existing_user_for_username_check";

        mockMvc.perform(get("/api/auth/check-username-exists/{username}", username))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.exists", is(true)));
    }

    @Test
    public void testCheckUsernameExistsWhenUserDoesNotExist() throws Exception {
        String missingUsername = "missing_user_for_username_check";

        mockMvc.perform(get("/api/auth/check-username-exists/{username}", missingUsername))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.exists", is(false)));
    }

    @Test
    public void testCheckEmailExistsWhenUserExists() throws Exception {
        String email = "existing_user_for_email_check@test.it";

        mockMvc.perform(get("/api/auth/check-email-exists/{email}", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.exists", is(true)));
    }

    @Test
    public void testCheckEmailExistsWhenUserDoesNotExist() throws Exception {
        String missingEmail = "missing_user_for_email_check@test.it";

        mockMvc.perform(get("/api/auth/check-email-exists/{email}", missingEmail))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.exists", is(false)));
    }
}