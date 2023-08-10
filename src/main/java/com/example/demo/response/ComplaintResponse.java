package com.example.demo.response;

import com.example.demo.entity.Admin;

public class ComplaintResponse {
    private String message;
    private String adminName;
	public ComplaintResponse(String message, String adminName) {
		super();
		this.message = message;
		this.adminName = adminName;
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

  
    // Getters and setters for message and admin
}
