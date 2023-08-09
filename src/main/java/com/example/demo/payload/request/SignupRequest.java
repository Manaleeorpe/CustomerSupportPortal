package com.example.demo.payload.request;

import java.util.Set;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

 
public class SignupRequest {
    @NotBlank
    @Size(min = 3, max = 20)
    private String username;
    private String AdminType;
    private Long adminid;
 
   

	public Long getAdminid() {
		return adminid;
	}

	public void setAdminid(Long adminid) {
		this.adminid = adminid;
	}

	public String getAdminType() {
		return AdminType;
	}

	public void setAdminType(String adminType) {
		AdminType = adminType;
	}

	@NotBlank
    @Size(max = 50)
    @Email
    private String email;
    
    private String Phone_number;
    
    
    public String getPhone_number() {
		return Phone_number;
	}

	public void setPhone_number(String phone_number) {
		this.Phone_number = phone_number;
	}

	@NotBlank
    @Size(min = 6, max = 40)
    private String password;
  
    public String getUsername() {
        return username;
    }
 
    public void setUsername(String username) {
        this.username = username;
    }
 
    public String getEmail() {
        return email;
    }
 
    public void setEmail(String email) {
        this.email = email;
    }
 
    public String getPassword() {
        return password;
    }
 
    public void setPassword(String password) {
        this.password = password;
    }
    
    
}
