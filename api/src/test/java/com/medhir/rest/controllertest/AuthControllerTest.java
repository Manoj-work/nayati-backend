package com.medhir.rest.controllertest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medhir.rest.dto.AuthRequest;
import com.medhir.rest.dto.AuthResponse;
import com.medhir.rest.dto.RegisterRequest;
import com.medhir.rest.model.Role;
import com.medhir.rest.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.*;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testRegister() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("test@example.com");
        req.setPassword("pass123");
        req.setRoles(Set.of(Role.HRADMIN));

        when(authService.register(any(RegisterRequest.class)))
                .thenReturn("User registered successfully!");

        mockMvc.perform(post("/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully!"));
    }

    @Test
    void testLogin() throws Exception {
        AuthRequest req = new AuthRequest();
        req.setEmail("test@example.com");
        req.setPassword("pass123");

        AuthResponse res = new AuthResponse("mock-jwt-token", List.of("HRADMIN"));

        when(authService.login(any(AuthRequest.class))).thenReturn(res);

        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock-jwt-token"));
    }
}
