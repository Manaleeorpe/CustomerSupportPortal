package com.example.demo.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.example.demo.Repository.FAQRepository;
import com.example.demo.Service.AdminService;
import com.example.demo.entity.Complaint;
import com.example.demo.entity.FAQ;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.example.demo.Repository.AdminRepository;
import com.example.demo.entity.Admin;
import com.example.demo.payload.request.LoginRequest;
import com.example.demo.payload.request.SignupRequest;
import com.example.demo.response.MessageResponse;
import com.example.demo.response.UserInfoResponse;
import com.example.demo.security.jwt.JwtUtils;
import com.example.demo.security.services.UserDetailsImpl;

@RestController
@RequestMapping("/auth/admin")
public class AdminController {

	@Autowired
	  AuthenticationManager authenticationManager;

	  @Autowired
	  AdminRepository adminRepository;

	  @Autowired
	  private FAQRepository faqRepository;

	  @Autowired
	  private AdminService adminService;

	  @Autowired
	  PasswordEncoder encoder;
	  
	  @Autowired
	  JwtUtils jwtUtils;

	  @PostMapping("/signin")
	  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

	    Authentication authentication = authenticationManager
	        .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

	    SecurityContextHolder.getContext().setAuthentication(authentication);

	    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

	    ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

	    List<String> roles = userDetails.getAuthorities().stream()
	        .map(item -> item.getAuthority())
	        .collect(Collectors.toList());

	    return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
	        .body(new UserInfoResponse(userDetails.getUserId(),
	                                   userDetails.getUsername(),
	                                   userDetails.getEmail(),
	                                   roles));
	  }

	  @PostMapping("/signup")
	  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
	      if (adminRepository.existsByName(signUpRequest.getUsername())) {
	          return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
	      }

	      if (adminRepository.existsByEmail(signUpRequest.getEmail())) {
	          return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
	      }

	      // Create new customer's account
	      Admin admin = new Admin(signUpRequest.getUsername(),
	                                        signUpRequest.getEmail(),
	                                        signUpRequest.getPhone_number(),
	                                        signUpRequest.getAdminType(),
	                                        encoder.encode(signUpRequest.getPassword())
	                                        );
	      adminRepository.save(admin);
	      System.out.println("Admin Type: " + signUpRequest.getAdminType());

	      return ResponseEntity.ok(new MessageResponse("Admin registered successfully!"));
	  }
	  
	  @PostMapping("/signout")
	  public ResponseEntity<?> logoutUser() {
	    ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
	    return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
	        .body(new MessageResponse("You've been signed out!"));
	  }

	@GetMapping("/{adminId}") // To get admin details
	public ResponseEntity<?> getAdminById(@PathVariable Long adminId) {
		Admin admin = adminRepository.findById(adminId).orElse(null);

		if (admin != null) {

			admin.setPassword(null); // Remove password from the response

			return ResponseEntity.ok(admin);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@PutMapping("/update/{adminId}")
	public ResponseEntity<?> updateAdminDetails(@PathVariable Long adminId, @RequestBody Admin updatedAdmin) {
		Admin updated = adminService.updateAdminDetails(adminId, updatedAdmin);

		if (updated != null) {
			return ResponseEntity.ok(new MessageResponse("Admin details updated successfully!"));
		} else {
			return ResponseEntity.notFound().build(); // Admin not found
		}
	}

	@PutMapping("/updateComplaint/{complaintid}")
	public ResponseEntity<MessageResponse> updateComplaint(@PathVariable Long complaintid, @RequestBody Complaint updatedComplaint) {
		Complaint existingComplaint = adminService.updateComplaintDetails(complaintid, updatedComplaint);

		if (existingComplaint != null) {
			return ResponseEntity.ok(new MessageResponse("Complaint details updated successfully"));
		} else {
			return ResponseEntity.notFound().build(); //Complaint not found
		}


	}

	@PostMapping("/addFaq")
	public ResponseEntity<?> addFaq(@RequestBody FAQ faq) {

			// Create a new FAQ entity
			FAQ newFaq = new FAQ();
			newFaq.setFaqType(faq.getFaqType());
			newFaq.setQuestion(faq.getQuestion());
			newFaq.setAnswer(faq.getAnswer());

			// Save the new FAQ entity to the database
			faqRepository.save(newFaq);

			return ResponseEntity.ok(new MessageResponse("FAQ added successfully!"));

	}

	@GetMapping("/getFaq/{faqId}")
	public ResponseEntity<?> getFaqById(@PathVariable Long faqId) {
		FAQ faq = faqRepository.findById(faqId).orElse(null);

		if(faq != null) {
			return ResponseEntity.ok(faq);
		} else {
			return ResponseEntity.notFound().build();
		}

	}

	@PutMapping("/updateFaq/{faqId}")
	public ResponseEntity<MessageResponse> updateFaq(@PathVariable Long faqId, @RequestBody FAQ updatedFAQ) {
		FAQ existingFAQ = adminService.updateFaqDetails(faqId, updatedFAQ);

		if (existingFAQ != null) {
			return ResponseEntity.ok(new MessageResponse("FAQ details updated successfully"));
		} else {
			return ResponseEntity.notFound().build(); //FAQ not found
		}


	}
	}
