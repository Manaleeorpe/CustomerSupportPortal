package com.example.demo.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "admin")
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "adminid")
    private Long adminid;

    public Admin() {
        super();
    }

    public Admin(@NotNull(message = "First name is mandatory") String name,
			@NotNull(message = "Email is mandatory") @Email(message = "Require email format") String email,
			@Size(max = 10, min = 10, message = "Require only 10 digits") String Phone_number, String AdminType,
			@NotNull(message = "Password is mandatory") String password) {
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
}
