package com.example.demo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.Complaint;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    
    @Transactional
    @Modifying
    @Query("UPDATE Complaint c SET c.rating = :rating WHERE c.complaintid = :complaintId")
    void updateRatingById(Long complaintId, Double rating);
}
