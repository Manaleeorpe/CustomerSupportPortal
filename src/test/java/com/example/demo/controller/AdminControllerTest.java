package com.example.demo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.demo.Repository.AdminRepository;
import com.example.demo.Repository.CustomerRepository;
import com.example.demo.entity.Admin;
import com.example.demo.payload.request.LoginRequest;
import com.example.demo.payload.request.SignupRequest;
import com.example.demo.response.UserInfoResponse;
import com.example.demo.security.jwt.JwtUtils;
import com.example.demo.security.services.UserDetailsImpl;

public class AdminControllerTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private CustomerRepository customerRepository;
    
    @MockBean
    private AuthenticationManager authenticationManager;
    
    @MockBean
    private JwtUtils jwtUtils;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminController adminController;
    
    @Mock
    private UserDetailsService userDetailsService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegisterUser() {
        // Mocking behavior
        when(adminRepository.existsByName(any())).thenReturn(false);
        when(adminRepository.existsByEmail(any())).thenReturn(false);
        when(customerRepository.existsByName(any())).thenReturn(false);
        when(customerRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(adminRepository.save(any())).thenReturn(new Admin());

        // Prepare mock data
        SignupRequest signUpRequest = new SignupRequest();
        signUpRequest.setUsername("saloni");
        signUpRequest.setEmail("patilsaloni2705@gmail.com");
        signUpRequest.setPhone_number("1221263890");
        signUpRequest.setPassword("1234");

        // Perform the test
        ResponseEntity<?> response = adminController.registerUser(signUpRequest);
       
    }
    
    @Test
    public void testSignInSuccess() {
        // Mocking behavior of authenticationManager.authenticate
        Authentication authentication = Mockito.mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);

        // Prepare mock data
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("saloni");
        loginRequest.setPassword("1234");

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));

        UserDetailsImpl userDetails = new UserDetailsImpl(
            1L,
            "saloni",
            "patilsaloni2705@gmail.com",
            "encodedPassword",
            authorities
        );
        
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);

        // Mock jwtUtils behavior
        when(jwtUtils.generateJwtCookie(any(UserDetailsImpl.class))).thenReturn(null);

        // Perform the test
        ResponseEntity<?> response = adminController.authenticateUser(loginRequest);

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        UserInfoResponse userInfoResponse = (UserInfoResponse) response.getBody();
        assertEquals("saloni", userInfoResponse.getUsername());
        assertEquals("patilsaloni2705@gmail.com", userInfoResponse.getEmail());
    }

   
}