package com.example.demo.Service;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import com.example.demo.Repository.CustomerRepository;
import com.example.demo.entity.Customer;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public Customer updateCustomerDetails(Long customerId, Customer updatedCustomer) {
        Customer existingCustomer = customerRepository.findById(customerId).orElse(null);

        if (existingCustomer != null) {
            existingCustomer.setName(updatedCustomer.getName());
            existingCustomer.setEmail(updatedCustomer.getEmail());
            existingCustomer.setPhoneNumber(updatedCustomer.getPhoneNumber());

            // Update other fields as needed

            return customerRepository.save(existingCustomer);
        } else {
            return null; // Customer not found
        }
    }
}

