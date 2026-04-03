package com.enzo.url.shortener.service;

import com.enzo.url.shortener.dto.LoginRequest;
import com.enzo.url.shortener.dto.LoginResponse;
import com.enzo.url.shortener.dto.RegisterRequest;
import com.enzo.url.shortener.repository.UserRepository;
import com.enzo.url.shortener.security.JwtService;
import com.enzo.url.shortener.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_shouldReturnLoginResponse_whenEmailNotExists() {
        RegisterRequest request = new RegisterRequest("test@email.com", "123456");

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.password())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        when(jwtService.generateToken(any(User.class))).thenReturn("token123");

        LoginResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("token123", response.token());
        assertEquals("test@email.com", response.email());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_shouldThrowException_whenEmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest("test@email.com", "123456");
        User existingUser = User.builder().email("test@email.com").password("hash").build();

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(existingUser));

        assertThrows(RuntimeException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_shouldReturnLoginResponse_whenCredentialsAreValid() {
        LoginRequest request = new LoginRequest("test@email.com", "123456");
        User user = User.builder().email("test@email.com").password("hash").build();

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("token123");

        LoginResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("token123", response.token());
        assertEquals("test@email.com", response.email());
    }
}
