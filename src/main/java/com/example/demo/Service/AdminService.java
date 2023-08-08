package com.example.demo.Service;

import com.example.demo.Repository.AdminRepository;
import com.example.demo.Repository.ComplaintRepository;
import com.example.demo.Repository.FAQRepository;
import com.example.demo.entity.Admin;
import com.example.demo.entity.Complaint;
import com.example.demo.entity.FAQ;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private FAQRepository faqRepository;

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

    public Complaint updateComplaintDetails(Long complaintid, Complaint updatedComplaint) {
        Complaint existingComplaint = complaintRepository.findById(complaintid).orElse(null);

        if (existingComplaint != null) {
            existingComplaint.setDate(new Date());
            existingComplaint.setStatus(updatedComplaint.getStatus());

            // Update other fields as needed

            return complaintRepository.save(existingComplaint);
        } else {
            return null; // Complaint not found
        }
    }

    public FAQ updateFaqDetails(Long faqId, FAQ updatedFAQ) {
        FAQ existingFAQ = faqRepository.findById(faqId).orElse(null);

        if (existingFAQ != null) {
            existingFAQ.setFaqType(updatedFAQ.getFaqType());
            existingFAQ.setQuestion(updatedFAQ.getQuestion());
            existingFAQ.setAnswer(updatedFAQ.getAnswer());

            // Update other fields as needed

            return faqRepository.save(existingFAQ);
        } else {
            return null; // FAQ not found
        }
    }
    }

