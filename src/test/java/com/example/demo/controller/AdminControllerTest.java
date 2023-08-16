package com.example.demo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import com.example.demo.Repository.AdminRepository;
import com.example.demo.Repository.CustomerRepository;
import com.example.demo.entity.Admin;
import com.example.demo.payload.request.LoginRequest;
import com.example.demo.payload.request.SignupRequest;
import com.example.demo.response.MessageResponse;
import com.example.demo.response.UserInfoResponse;
import com.example.demo.security.jwt.JwtUtils;
import com.example.demo.security.services.UserDetailsImpl;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AdminControllerTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private AdminController adminController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(adminController).addFilter(new BasicAuthenticationFilter(authenticationManager)).build();
    }

    @Test
    public void testAuthenticateUser() throws Exception {
        // Mocking behavior for authentication
        Authentication authentication = new UsernamePasswordAuthenticationToken("saloni", "1234");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Mocking the user details
        UserDetails userDetails = new UserDetailsImpl(1L, "saloni", "patilsaloni2705@gmail.com", "password", new ArrayList<>());
        doReturn(authentication).when(authenticationManager).authenticate(any());
        doReturn(userDetails).when(userDetailsService).loadUserByUsername("saloni");

        // Mocking the authorities
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        doReturn(authorities).when(userDetails).getAuthorities();

        // Perform the test for signin
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("saloni");
        loginRequest.setPassword("1234");

        MvcResult result = mockMvc.perform(post("/auth/admin/signin")
                .contentType("application/json")
                .content("{\"username\": \"saloni\", \"password\": \"1234\"}"))
                .andExpect(status().isOk())
                .andReturn();
    }



    @Test
    public void testRegisterUser() throws Exception {
        // Mocking behavior for signup
        when(adminRepository.existsByName(any())).thenReturn(false);
        when(adminRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(adminRepository.save(any())).thenReturn(new Admin());

        // Perform the test for signup
        SignupRequest signUpRequest = new SignupRequest();
        signUpRequest.setUsername("saloni");
        signUpRequest.setEmail("patilsaloni2705@gmail.com");
        signUpRequest.setPhone_number("1221263890");
        signUpRequest.setPassword("1234");
        signUpRequest.setAdminType("ROLE_ADMIN");

        ResponseEntity<?> response = adminController.registerUser(signUpRequest);

        // Add assertions for the response
    }}