package com.example.demo.Repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.demo.Repository.AdminRepository;
import com.example.demo.entity.Admin;

public class AdminRepositoryTest {

    @Mock
    private AdminRepository adminRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testExistsByName() {
        when(adminRepository.existsByName("saloni")).thenReturn(true);

        boolean result = adminRepository.existsByName("saloni");

        assertTrue(result);
    }

    @Test
    public void testExistsByEmail() {
        when(adminRepository.existsByEmail("admin@example.com")).thenReturn(true);

        boolean result = adminRepository.existsByEmail("admin@example.com");

        assertTrue(result);
    }

    @Test
    public void testFindById() {
        Admin admin = new Admin();
        admin.setAdminid(1L);
        admin.setName("Admin Name");

        when(adminRepository.findById(1L)).thenReturn(Optional.of(admin));

        Optional<Admin> result = adminRepository.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("Admin Name", result.get().getName());
    }

    @Test
    public void testFindByEmail() {
        Admin admin = new Admin();
        admin.setAdminid(1L);
        admin.setEmail("admin@example.com");

        when(adminRepository.findByEmail("admin@example.com")).thenReturn(admin);

        Admin result = adminRepository.findByEmail("admin@example.com");

        assertNotNull(result);
        assertEquals("admin@example.com", result.getEmail());
    }
}
