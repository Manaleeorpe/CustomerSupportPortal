package com.example.demo.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.AdminComplaintHistory;

public interface AdminComplaintHistoryRepository extends JpaRepository<AdminComplaintHistory,Long> {

	List<AdminComplaintHistory> findByAdminid(Long adminid);
	List<AdminComplaintHistory> findByStatus(String status);
	 List<AdminComplaintHistory> findByAdminidAndStatus(Long adminid, String status);

}
