package com.example.demo.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "AdminComplaintHistory")
public class AdminComplaintHistory {

	 @Id
	 @Column(name = "complaintid")
	 private Long complaintid;
	 private String complaintType;
	 private String status;
	 private Long adminid;
	 private Double rating;
	 
	public Double getRating() {
		return rating;
	}

	public void setRating(Double rating) {
		this.rating = rating;
	}

	public Long getComplaintid() {
		return complaintid;
	}

	public void setComplaintid(Long complaintid) {
		this.complaintid = complaintid;
	}

	public String getComplaintType() {
		return complaintType;
	}

	public void setComplaintType(String complaintType) {
		this.complaintType = complaintType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getAdminid() {
		return adminid;
	}

	public void setAdminid(Long adminid) {
		this.adminid = adminid;
	}

	public AdminComplaintHistory(Long complaintid, String complaintType, String status, Long adminid, Double rating) {
		super();
		this.complaintid = complaintid;
		this.complaintType = complaintType;
		this.status = status;
		this.adminid = adminid;
		this.rating = rating;
	}

	public AdminComplaintHistory() {
		super();
	}

	
	 
	 
}
