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
    
    public void CalculateHours() {     
         for (Long i = 1L; i <= 5L; i++) {
             Optional<Admin> adminOptional = adminRepository.findById(i);
             
             if (adminOptional.isPresent()) {
                 Admin admin = adminOptional.get();
                 List<Complaint> adminComplaints = admin.getComplaints();
                 int hours = 0;
                 for (Complaint complaint : adminComplaints) {
                     String complaintType = complaint.getComplaintType();
                     if (complaintType.equals("Level 1")) {
                         hours += 2;
                     } else if (complaintType.equals("Level 2")) {
                         hours += 24;
                     } else if (complaintType.equals("Level 3")) {
                         hours += 48;
                     }
                 }
                 
                 admin.setComplaintHours(hours);
                 adminRepository.save(admin);
             }
         }
 
    }
    
    public void unassignAdmin(Long complaintid) {
		Optional<Complaint> complaintOptional = complaintRepository.findById(complaintid);
		        if (complaintOptional.isPresent()) {
		            Complaint complaint = complaintOptional.get();
		            complaint.setAdminid(null);
		        }
    }
    
    public Long ChooseAdmin() {
        int minHours = Integer.MAX_VALUE; 
        Long chosenAdminId = null; 
        Random random = new Random();
        
        for (Long i = 1L; i <= 5L; i++) {
            Optional<Admin> adminOptional = adminRepository.findById(i);
            
            if (adminOptional.isPresent()) {
                Admin admin = adminOptional.get();
                int AdminHours = admin.getComplaintHours();

                if (AdminHours < minHours) {
                    minHours = AdminHours;
                    chosenAdminId = i;
                } else if (AdminHours == minHours && random.nextBoolean()) {
                    chosenAdminId = i;
                }

                adminRepository.save(admin); // Save the updated admin
            }
        }
        
        return chosenAdminId;
    }

    public Admin AddComplaintToAdmin(Long complaintid) {
        Optional<Complaint> complaintOptional = complaintRepository.findById(complaintid);
        
        if (complaintOptional.isPresent()) {
            Complaint complaint = complaintOptional.get();
            
            Long randomNumber = ChooseAdmin();
            System.out.println(randomNumber);
            
            Optional<Admin> adminOptional = adminRepository.findById(randomNumber);
            
            if (adminOptional.isPresent()) {
                Admin admin = adminOptional.get();
                List<Complaint> adminComplaints = admin.getComplaints();
                adminComplaints.add(complaint);
                admin.setComplaints(adminComplaints);

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
