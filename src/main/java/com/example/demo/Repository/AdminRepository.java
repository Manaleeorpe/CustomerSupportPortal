package com.example.demo.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Admin;
import com.example.demo.entity.Customer;

public interface AdminRepository extends JpaRepository<Admin,Long> {

	Optional<Admin> findByName(String name);

	boolean existsByName(String username);

	boolean existsByEmail(String email);

	Optional<Admin> findById(Long randomNumber);

	Admin findByEmail(String email);

}
