package com.medhir.rest.service;

import com.medhir.rest.config.JwtUtil;
import com.medhir.rest.dto.AuthRequest;
import com.medhir.rest.dto.AuthResponse;
import com.medhir.rest.dto.RegisterRequest;
import com.medhir.rest.exception.DuplicateResourceException;
import com.medhir.rest.model.Role;
import com.medhir.rest.model.UserAccount;
import com.medhir.rest.repository.UserAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.*;

import static com.medhir.rest.model.Role.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class AuthServiceTest {

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private AuthRequest authRequest;
    private UserAccount userAccount;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setRoles(Set.of(HRADMIN));

        authRequest = new AuthRequest();
        authRequest.setEmail("test@example.com");
        authRequest.setPassword("password123");

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        userAccount = UserAccount.builder()
                .email("test@example.com")
                .password(encoder.encode("password123"))
                .roles(Set.of(HRADMIN))
                .build();
    }

    @Test
    void Successwhen_do_Register_() {
        when(userAccountRepository.findByEmail(registerRequest.getEmail())).thenReturn(Optional.empty());

        String result = authService.register(registerRequest);

        assertThat(result).isEqualTo("User registered successfully!");
        verify(userAccountRepository, times(1)).save(any(UserAccount.class));
    }

    @Test
    void testRegister_DuplicateEmail_ThrowsException() {

        when(userAccountRepository.findByEmail(anyString())).thenReturn(Optional.of(userAccount));

        assertThrows(DuplicateResourceException.class, () -> authService.register(registerRequest));
    }

    @Test
    void testLogin_Success() {
        when(userAccountRepository.findByEmail(anyString())).thenReturn(Optional.of(userAccount));
        when(jwtUtil.generateToken(anyString(), anySet())).thenReturn("mock-token");

        AuthResponse response = authService.login(authRequest);

        assertThat(response.getToken()).isEqualTo("mock-token");
        assertThat(response.getRoles()).containsExactly("HRADMIN");
    }

    @Test
    void testLogin_InvalidEmail_ThrowsException() {
        when(userAccountRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class, () -> authService.login(authRequest));
    }

    @Test
    void testLogin_InvalidPassword_ThrowsException() {
        UserAccount wrongPasswordUser = UserAccount.builder()
                .email("test@example.com")
                .password(new BCryptPasswordEncoder().encode("wrongPassword"))
                .roles(Set.of(HRADMIN))
                .build();

        when(userAccountRepository.findByEmail(anyString())).thenReturn(Optional.of(wrongPasswordUser));

        assertThrows(BadCredentialsException.class, () -> authService.login(authRequest));
    }
}
