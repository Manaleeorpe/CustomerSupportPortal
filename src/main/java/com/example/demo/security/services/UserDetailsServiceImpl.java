package com.example.demo.security.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.Repository.AdminRepository;
import com.example.demo.Repository.CustomerRepository;
import com.example.demo.entity.Admin;
import com.example.demo.entity.Customer;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
  @Autowired
  CustomerRepository customerRepository;

  @Autowired
  AdminRepository adminRepository;

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
    Customer customer = customerRepository.findByName(name).orElse(null);
    Admin admin = adminRepository.findByName(name).orElse(null);

    if (customer != null) {
      return UserDetailsImpl.buildCustomer(customer);
    } else if (admin != null) {
      return UserDetailsImpl.buildAdmin(admin);
    } else {
      throw new UsernameNotFoundException("User Not Found with username: " + name);
    }
  }
}
