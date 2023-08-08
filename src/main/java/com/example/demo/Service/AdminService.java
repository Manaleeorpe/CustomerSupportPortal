package com.example.demo.Service;

import com.example.demo.Repository.AdminRepository;
import com.example.demo.entity.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    public Admin updateAdminDetails(Long adminId, Admin updatedAdmin) {
        Admin existingAdmin = adminRepository.findById(adminId).orElse(null);

        if (existingAdmin != null) {
            existingAdmin.setName(updatedAdmin.getName());
            existingAdmin.setEmail(updatedAdmin.getEmail());
            existingAdmin.setPhone_number(updatedAdmin.getPhone_number());
            existingAdmin.setAdminType(updatedAdmin.getAdminType());

            // Update other fields as needed

            return adminRepository.save(existingAdmin);
        } else {
            return null; // Admin not found
        }
    }

}
