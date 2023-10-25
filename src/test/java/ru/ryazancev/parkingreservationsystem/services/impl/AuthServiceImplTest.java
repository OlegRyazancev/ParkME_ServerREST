package ru.ryazancev.parkingreservationsystem.services.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import ru.ryazancev.parkingreservationsystem.models.user.User;
import ru.ryazancev.parkingreservationsystem.services.UserService;
import ru.ryazancev.parkingreservationsystem.util.exceptions.ResourceNotFoundException;
import ru.ryazancev.parkingreservationsystem.web.dto.auth.JwtRequest;
import ru.ryazancev.parkingreservationsystem.web.dto.auth.JwtResponse;
import ru.ryazancev.parkingreservationsystem.web.security.filter.jwt.JwtTokenProvider;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthServiceImpl authService;

    private User user;
    private JwtRequest request;

    private final String ACCESS_TOKEN = "accessToken";
    private final String REFRESH_TOKEN = "refreshToken";

    @BeforeEach
    public void setup() {
        user = User.builder()
                .id(1L)
                .email("username")
                .password("password")
                .roles(Collections.emptySet())
                .build();

        request = new JwtRequest();
        request.setUsername(user.getEmail());
        request.setPassword(user.getPassword());


    }

    @DisplayName("Login")
    @Test
    public void testLogin_whenCorrectDetails_returnsJwtResponse() {
        //Arrange
        when(userService.getByUsername(user.getEmail())).thenReturn(user);
        when(jwtTokenProvider.createAccessToken(user.getId(), user.getEmail(), user.getRoles())).thenReturn(ACCESS_TOKEN);
        when(jwtTokenProvider.createRefreshToken(user.getId(), user.getEmail())).thenReturn(REFRESH_TOKEN);

        //Act
        JwtResponse response = authService.login(request);

        //Assert
        assertEquals(response.getUsername(), user.getEmail());
        assertEquals(response.getId(), user.getId());
        assertNotNull(response.getAccessToken());
        assertNotNull(response.getRefreshToken());

        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword())
        );
    }

    @DisplayName("Login with incorrect username")
    @Test
    public void testLogin_whenIncorrectUsername_throwsResourceNotFoundException() {
        //Arrange
        when(userService.getByUsername(user.getEmail())).thenThrow(ResourceNotFoundException.class);

        //Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            authService.login(request);
        });
        verifyNoInteractions(jwtTokenProvider);
    }

    @DisplayName("Refresh")
    @Test
    public void testRefresh_returnsRefreshUserTokens() {
        //Arrange

        String newRefreshToken = "newRefreshToken";

        JwtResponse response = new JwtResponse();
        response.setAccessToken(ACCESS_TOKEN);
        response.setRefreshToken(newRefreshToken);

        when(jwtTokenProvider.refreshUserTokens(REFRESH_TOKEN)).thenReturn(response);

        //Act
        JwtResponse testResponse = authService.refresh(REFRESH_TOKEN);

        //Assert
        assertEquals(response, testResponse);
        verify(jwtTokenProvider).refreshUserTokens(REFRESH_TOKEN);
    }

}