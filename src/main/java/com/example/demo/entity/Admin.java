package com.example.demo.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "admin")
public class Admin {
    @Id
    @Column(name = "adminid")
    private Long adminid;

    public Admin() {
        super();
    }
    @OneToMany(mappedBy = "admin" , cascade = CascadeType.ALL)
    private List<Complaint> Complaint;

    public Admin(Long adminid, List<com.example.demo.entity.Complaint> complaint,
			@NotNull(message = "First name is mandatory") String name,
			@NotNull(message = "Email is mandatory") @Email(message = "Require email format") String email,
			@Size(max = 10, min = 10, message = "Require only 10 digits") String phone_number,
			@NotNull(message = "Password is mandatory") String password, String adminType) {
		super();
		this.adminid = adminid;
		Complaint = complaint;
		this.name = name;
		this.email = email;
		Phone_number = phone_number;
		this.password = password;
		AdminType = adminType;
	}

	public List<Complaint> getComplaints() {
        return Complaint;
    }
	public void setComplaints(List<Complaint> complaints) {
	    Complaint = complaints;
	    for (Complaint complaint : complaints) {
	        complaint.setAdmin(this); // Set the admin for each complaint
	    }
	}


    public Admin(@NotNull(message = "First name is mandatory") String name,
			@NotNull(message = "Email is mandatory") @Email(message = "Require email format") String email,
			@Size(max = 10, min = 10, message = "Require only 10 digits") String Phone_number, String AdminType,
			@NotNull(message = "Password is mandatory") String password, Long adminid) {
		super();
		this.adminid = adminid;
		this.name = name;
		this.email = email;
		this.Phone_number = Phone_number;
		this.AdminType = AdminType;
		this.password = password;
	}

	public Admin(@NotNull(message = "First name is mandatory") String name,
			@NotNull(message = "Email is mandatory") @Email(message = "Require email format") String email,
			@Size(max = 10, min = 10, message = "Require only 10 digits") String Phone_number,
			@NotNull(message = "Password is mandatory") String password) {
		super();
		this.name = name;
		this.email = email;
		this.Phone_number = Phone_number;
		this.password = password;
	}

	public Admin(Long adminid) {
        super();
        this.adminid = adminid;
    }

    public Admin(Long adminid, @NotNull(message = "First name is mandatory") String name) {
        super();
        this.adminid = adminid;
        this.name = name;
    }

    public Admin(Long adminid, @NotNull(message = "First name is mandatory") String name,
            @NotNull(message = "Email is mandatory") @Email(message = "Require email format") String email) {
        super();
        this.adminid = adminid;
        this.name = name;
        this.email = email;
    }

    public Admin(Long adminid, @NotNull(message = "First name is mandatory") String name,
            @NotNull(message = "Email is mandatory") @Email(message = "Require email format") String email,
            @Size(max = 10, min = 10, message = "Require only 10 digits") String Phone_number) {
        super();
        this.adminid = adminid;
        this.name = name;
        this.email = email;
        this.Phone_number = Phone_number;
    }

	public Admin(Long adminid, @NotNull(message = "First name is mandatory") String name,
			@NotNull(message = "Email is mandatory") @Email(message = "Require email format") String email,
			@Size(max = 10, min = 10, message = "Require only 10 digits") String phone_number,String adminType,
			@NotNull(message = "Password is mandatory") String password) {
		super();
		this.adminid = adminid;
		this.name = name;
		this.email = email;
		Phone_number = phone_number;
		this.password = password;
		AdminType = adminType;
	}
	@NotNull(message = "First name is mandatory")
    private String name;

    @NotNull(message = "Email is mandatory")
    @Email(message = "Require email format")
    private String email;

    @Size(max = 10, min = 10, message = "Require only 10 digits")
    private String Phone_number;

    @NotNull(message = "Password is mandatory")
    private String password;

    // Additional adminType column for Admin
    @Column(name = "admin_type")
    private String AdminType;

    public Long getAdminid() {
        return adminid;
    }

    public void setAdminid(Long adminid) {
        this.adminid = adminid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone_number() {
        return Phone_number;
    }

    public void setPhone_number(String Phone_number) {
        this.Phone_number = Phone_number;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Getter and Setter for the adminType field
    public String getAdminType() {
        return AdminType;
    }

    public void setAdminType(String AdminType) {
        this.AdminType = AdminType;
    }
    
    @Column(name = "complaint_hours")
    public int complaintHours;

	public int getComplaintHours() {
		return complaintHours;
	}

	public void setComplaintHours(int complaintHours) {
		this.complaintHours = complaintHours;
	}

    
}
