package com.example.demo.entity;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "complaint")
public class Complaint {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "complaintid")
    private Long complaintid;

    @NotNull(message = "Complaint type is mandatory")
    private String complaintType;

    @NotNull(message = "Customer ID is mandatory")
    private Long customerid;

    @NotNull(message = "Date is mandatory")
    private Date date;

    @NotNull(message = "Status is mandatory")
    private String status;

    private String description; // New field for complaint description
    
    private Double rating; // New field for complaint rating

    public Complaint() {
    }

    public Complaint(Long complaintid, String complaintType, Long customerid, Date date, String status, String description, Double rating) {
        this.complaintid = complaintid;
        this.complaintType = complaintType;
        this.customerid = customerid;
        this.date = date;
        this.status = status;
        this.description = description;
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

    public Long getCustomerid() {
        return customerid;
    }

    public void setCustomerid(Long customerid) {
        this.customerid = customerid;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }


    @ManyToOne
    @JoinColumn(name = "customerid", insertable = false, updatable = false)
    private Customer customer;

}
