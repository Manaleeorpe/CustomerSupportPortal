package com.example.demo.Service;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.demo.Repository.AdminRepository;
import com.example.demo.Repository.ComplaintRepository;
import com.example.demo.Repository.FAQRepository;
import com.example.demo.Service.AdminService;
import com.example.demo.entity.Admin;
import com.example.demo.entity.Complaint;
import com.example.demo.entity.FAQ;

public class AdminServiceTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private ComplaintRepository complaintRepository;

    @Mock
    private FAQRepository faqRepository;

    @InjectMocks
    private AdminService adminService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testUpdateAdminDetails() {
        Long adminId = 1L;
        Admin existingAdmin = new Admin();
        existingAdmin.setAdminid(adminId);
        
        Admin updatedAdmin = new Admin();
        updatedAdmin.setName("New Name");
        updatedAdmin.setEmail("new@example.com");
        updatedAdmin.setPhone_number("1234567890");

        when(adminRepository.findById(adminId)).thenReturn(Optional.of(existingAdmin));
        when(adminRepository.save(any(Admin.class))).thenReturn(updatedAdmin);

        Admin result = adminService.updateAdminDetails(adminId, updatedAdmin);

        assertNotNull(result);
        assertEquals("New Name", result.getName());
        assertEquals("new@example.com", result.getEmail());
        assertEquals("1234567890", result.getPhone_number());
    }

    @Test
    public void testUpdateComplaintDetails() {
        Long complaintId = 1L;
        Complaint existingComplaint = new Complaint();
        existingComplaint.setAdminid(complaintId);
        
        Complaint updatedComplaint = new Complaint();
        updatedComplaint.setStatus("Resolved");

        when(complaintRepository.findById(complaintId)).thenReturn(Optional.of(existingComplaint));
        when(complaintRepository.save(any(Complaint.class))).thenReturn(updatedComplaint);

        Complaint result = adminService.updateComplaintDetails(complaintId, updatedComplaint);

        assertNotNull(result);
        assertEquals("Resolved", result.getStatus());
    }

    @Test
    public void testUpdateFaqDetails() {
        Long faqId = 1L;
        FAQ existingFAQ = new FAQ();
        existingFAQ.setFaqId(faqId);
        
        FAQ updatedFAQ = new FAQ();
        updatedFAQ.setFaqType("New Type");
        updatedFAQ.setQuestion("New Question");
        updatedFAQ.setAnswer("New Answer");

        when(faqRepository.findById(faqId)).thenReturn(Optional.of(existingFAQ));
        when(faqRepository.save(any(FAQ.class))).thenReturn(updatedFAQ);

        FAQ result = adminService.updateFaqDetails(faqId, updatedFAQ);

        assertNotNull(result);
        assertEquals("New Type", result.getFaqType());
        assertEquals("New Question", result.getQuestion());
        assertEquals("New Answer", result.getAnswer());
    }

    @Test
    public void testGetAdminDetailsById() {
        Long adminId = 1L;
        Admin admin = new Admin();
        admin.setAdminid(adminId);
        admin.setName("Admin Name");
        admin.setEmail("admin@example.com");

        when(adminRepository.findById(adminId)).thenReturn(Optional.of(admin));

        Admin result = adminService.getAdminDetailsById(adminId);

        assertNotNull(result);
        assertEquals(adminId, result.getAdminid());
        assertEquals("Admin Name", result.getName());
        assertEquals("admin@example.com", result.getEmail());
        assertNull(result.getPassword());
    }
}
