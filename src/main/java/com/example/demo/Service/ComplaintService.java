package com.example.demo.Service;

import java.util.Random;

import com.example.demo.Repository.AdminRepository;
import com.example.demo.Repository.ComplaintRepository;
import com.example.demo.entity.Admin;
import com.example.demo.entity.Complaint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

@Service
public class ComplaintService {

    private ComplaintRepository complaintRepository;
    private AdminRepository adminRepository;

    @Autowired
    public ComplaintService(ComplaintRepository complaintRepository, AdminRepository adminRepository) {
        this.complaintRepository = complaintRepository;
        this.adminRepository = adminRepository;
    }

    public Admin AddComplaintToAdmin(Long complaintid) {
        Optional<Complaint> complaintOptional = complaintRepository.findById(complaintid);
        
        if (complaintOptional.isPresent()) {
            Complaint complaint = complaintOptional.get();
            
            Random random = new Random();
            Long randomNumber = (long) (random.nextInt(5) + 1);
            System.out.println(randomNumber);
            
            Optional<Admin> adminOptional = adminRepository.findById(randomNumber);
            
            if (adminOptional.isPresent()) {
                Admin admin = adminOptional.get();
                List<Complaint> adminComplaints = admin.getComplaint();
                adminComplaints.add(complaint);
                admin.setComplaint(adminComplaints);

                return adminRepository.save(admin);
            } else {
                // Admin not found
                return null;
            }
        } else {
            // Complaint not found
            return null;
        }
    }
}
