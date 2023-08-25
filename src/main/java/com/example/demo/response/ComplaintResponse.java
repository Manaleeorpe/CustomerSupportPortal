package com.example.demo.response;

import java.util.List;

import com.example.demo.entity.Admin;
import com.example.demo.entity.FAQ;

public class ComplaintResponse {
	private String message;
	private String adminName;
	private List<FAQ> faqs;
	private Long adminid;
	private Long complaintid;

	public ComplaintResponse(String message, String adminName, List<FAQ> faqs, Long adminid, Long complaintid) {
		super();
		this.message = message;
		this.adminName = adminName;
		this.faqs = faqs;
		this.adminid = adminid;
		this.complaintid = complaintid;
	}

	public Long getComplaintid() {
		return complaintid;
	}

	public void setComplaintid(Long complaintid) {
		this.complaintid = complaintid;
	}

	public Long getAdminid() {
		return adminid;
	}
	public void setAdminid(Long adminid) {
		this.adminid = adminid;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getAdminName() {
		return adminName;
	}
	public void setAdminName(String adminName) {
		this.adminName = adminName;
	}
	public List<FAQ> getFaqs() {
		return faqs;
	}
	public void setFaqs(List<FAQ> faqs) {
		this.faqs = faqs;
	}



	// Getters and setters for message and admin
}