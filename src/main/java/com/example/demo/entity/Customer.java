package com.example.demo.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "customer")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "customerid")
    private Long customerid;

    public Customer() {
        super();
    }

    public Customer(Long customerid) {
        super();
        this.customerid = customerid;
    }

    public Customer(Long customerid, @NotNull(message = "First name is mandatory") String name) {
        super();
        this.customerid = customerid;
        this.name = name;
    }

    public Customer(Long customerid, @NotNull(message = "First name is mandatory") String name,
            @NotNull(message = "Email is mandatory") @Email(message = "Require email format") String email) {
        super();
        this.customerid = customerid;
        this.name = name;
        this.email = email;
    }

    public Customer(Long customerid, @NotNull(message = "First name is mandatory") String name,
			@NotNull(message = "Email is mandatory") @Email(message = "Require email format") String email,
			@Size(max = 10, min = 10, message = "Require only 10 digits") String phoneNumber,
			@NotNull(message = "Password is mandatory") String password,
			List<com.example.demo.entity.Complaint> complaint) {
		super();
		this.customerid = customerid;
		this.name = name;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.password = password;
		Complaint = complaint;
	}

	public Customer(@NotNull(message = "First name is mandatory") String name,
			@NotNull(message = "Email is mandatory") @Email(message = "Require email format") String email,
			@Size(max = 10, min = 10, message = "Require only 10 digits") String phoneNumber,
			@NotNull(message = "Password is mandatory") String password) {
		super();
		this.name = name;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.password = password;
	}

	public Customer(Long customerid, @NotNull(message = "First name is mandatory") String name,
            @NotNull(message = "Email is mandatory") @Email(message = "Require email format") String email,
            @Size(max = 10, min = 10, message = "Require only 10 digits") String phoneNumber) {
        super();
        this.customerid = customerid;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    @NotNull(message = "First name is mandatory")
    private String name;

    @NotNull(message = "Email is mandatory")
    @Email(message = "Require email format")
    private String email;

    @Size(max = 10, min = 10, message = "Require only 10 digits")
    private String phoneNumber;

    @NotNull(message = "Password is mandatory")
    private String password;

    public Long getCustomerid() {
        return customerid;
    }

    public void setCustomerid(Long customerid) {
        this.customerid = customerid;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    @OneToMany(mappedBy = "customer" , cascade = CascadeType.ALL)
    private List<Complaint> Complaint;

    public List<Complaint> getComplaints() {
        return Complaint;
    }
}
