package com.example.demo.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.example.demo.Repository.CustomerRepository;
import com.example.demo.Service.EmailService;
import com.example.demo.Service.TokenService;
import com.example.demo.entity.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Repository.AdminRepository;
import com.example.demo.Repository.ComplaintRepository;
import com.example.demo.Repository.FAQRepository;
import com.example.demo.Service.AdminService;
import com.example.demo.Service.ComplaintService;
import com.example.demo.entity.Admin;
import com.example.demo.entity.Complaint;
import com.example.demo.entity.FAQ;
import com.example.demo.payload.request.LoginRequest;
import com.example.demo.payload.request.ResetPasswordRequest;
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
	  ComplaintRepository complaintRepository;

	  @Autowired
	  PasswordEncoder encoder;
	  
	  @Autowired
	  JwtUtils jwtUtils;
	  
	  @Autowired
	  private FAQRepository faqRepository;

	  @Autowired
	  private CustomerRepository customerRepository;
	  
	  @Autowired
	  private AdminService adminService;

	  @Autowired
	  private ComplaintService complaintService;

	  @Autowired
	  private EmailService emailService;
	  
	  @Autowired
	  private TokenService tokenService;
	  

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
		  if (customerRepository.existsByName(signUpRequest.getUsername()) || adminRepository.existsByName(signUpRequest.getUsername())) {
	          return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
	      }

	      if (customerRepository.existsByEmail(signUpRequest.getEmail()) || adminRepository.existsByEmail(signUpRequest.getEmail())) {
	          return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
	      }

	      // Create new customer's account
	      Admin admin = new Admin(signUpRequest.getAdminid(),
	    		  							signUpRequest.getUsername(),
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
	  
	  
	  @PostMapping("/forgot-password")
	  public ResponseEntity<?> adminForgotPassword(@RequestParam String email) {
	      Admin admin = adminRepository.findByEmail(email);

	      if (admin == null) {
	          return ResponseEntity.badRequest().body(new MessageResponse("No admin found with the provided email."));
	      }

	      // Generate a password reset token and construct the reset link
	      String token = tokenService.generatePasswordResetToken(admin.getAdminid());
	      String resetLink = "http://localhost:8080/auth/admin/reset-password?token=" + token;

	      // Compose the email content
	      String subject = "Admin Password Reset Request";
	      String text = "To reset your password, please click the link below:\n" + resetLink;

	      // Send the password reset email
	      if (emailService.sendPasswordResetVerificationEmail(email, "your-email@example.com", subject, text)) {
	          return ResponseEntity.ok(new MessageResponse("Password reset instructions sent to your email."));
	      } else {
	          return ResponseEntity.badRequest().body(new MessageResponse("Failed to send password reset email."));
	      }
	  }

	  @GetMapping("/reset-password")
	  public ResponseEntity<?> adminResetPasswordPage(@RequestParam String token) {
	      Long adminId = tokenService.getUserIdFromToken(token);

	      if (adminId != null) {
	          // Return a JSON response containing the verified adminId
	    	  return ResponseEntity.ok().body("Token verified.");
	      } else {
	          return ResponseEntity.badRequest().body(new MessageResponse("Invalid token or token expired."));
	      }
	  }

	  @PostMapping("/reset-password")
	  public ResponseEntity<?> adminResetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
	      // Validate token and retrieve admin ID
	      Long adminId = tokenService.getUserIdFromToken(resetPasswordRequest.getToken());

	      if (adminId != null) {
	          // Fetch the admin by ID
	          Admin admin = adminRepository.findById(adminId).orElse(null);

	          if (admin != null) {
	              // Update the admin's password
	              admin.setPassword(encoder.encode(resetPasswordRequest.getPassword()));
	              adminRepository.save(admin);

	              return ResponseEntity.ok(new MessageResponse("Password reset successfully."));
	          } else {
	              return ResponseEntity.badRequest().body(new MessageResponse("Admin not found."));
	          }
	      } else {
	          return ResponseEntity.badRequest().body(new MessageResponse("Invalid token or token expired."));
	      }
	  }

	  
	  @GetMapping("/{adminId}") //admin details
	  public ResponseEntity<?> getAdminDetails(@PathVariable Long adminId) {
	      Optional<Admin> adminOptional = adminRepository.findById(adminId);

	      if (adminOptional.isPresent()) {
	          Admin admin = adminOptional.get();
	          admin.setPassword(null);
	          return ResponseEntity.ok(admin);
	      } else {
	          return ResponseEntity.notFound().build();
	      }
	  }
	  
	  @PutMapping("/update/{adminId}")
	  @PreAuthorize("hasRole('ADMIN')")
		public ResponseEntity<?> updateAdminDetails(@PathVariable Long adminId, @RequestBody Admin updatedAdmin) {
			Admin updated = adminService.updateAdminDetails(adminId, updatedAdmin);

			if (updated != null) {
				return ResponseEntity.ok(new MessageResponse("Admin details updated successfully!"));
			} else {
				return ResponseEntity.notFound().build(); // Admin not found
			}
		}

	 /* @PutMapping("/updateComplaint/{complaintid}")
	  @PreAuthorize("hasRole('ADMIN')")
		public ResponseEntity<MessageResponse> updateComplaint(@PathVariable Long complaintid, @RequestBody Complaint updatedComplaint) {
		  Complaint complaint = complaintRepository.findById(complaintid).orElse(null);
		  String previousStatus = complaint.getStatus();
		  
			if (previousStatus.equals("Pending") && updatedComplaint.getStatus().equals("Resolved")) {
				complaintService.unassignAdmin(complaintid);
				complaintService.CalculateHours();
	        }
		  Complaint existingComplaint = adminService.updateComplaintDetails(complaintid, updatedComplaint);
			if (existingComplaint != null) {
				return ResponseEntity.ok(new MessageResponse("Complaint details updated successfully"));
			} else {
				return ResponseEntity.notFound().build(); //Complaint not found
			}
			

		} */

	  @PutMapping("/updateComplaint/{complaintid}")
		@PreAuthorize("hasRole('ADMIN')")
		public ResponseEntity<MessageResponse> updateComplaint(@PathVariable Long complaintid, @RequestBody Complaint updatedComplaint) {
			Complaint complaint = complaintRepository.findById(complaintid).orElse(null);
			String previousStatus = complaint.getStatus();

			if (previousStatus.equals("Pending") && updatedComplaint.getStatus().equals("Resolved")) {
				complaintService.unassignAdmin(complaintid);
				complaintService.CalculateHours();
			}

			Complaint existingComplaint = adminService.updateComplaintDetails(complaintid, updatedComplaint);

			if (existingComplaint != null) {
				// Get the customer associated with the complaint
				Long customerId = existingComplaint.getCustomerid();
				Customer customer = customerRepository.findById(customerId).orElse(null);

				// Prepare email content based on the updated complaint status
				String to = customer.getEmail();
				String from = "customerportal45@gmail.com";
				String subject = "Your Complaint Has Been Resolved (ID: " + complaintid + ")";
				String text = "Dear " + customer.getName() + ",\n" +
							"\n" +
							"We are pleased to inform you that your complaint with Complaint ID: " + complaintid + " has been resolved.\n" +
							"\n" +
							"Our team led by has successfully addressed your concerns. We hope the resolution meets your satisfaction.\n" +
							"\n" +
							"Thank you for your patience and understanding throughout this process.\n" +
							"\n" +
							"If you have any further questions or feedback, please do not hesitate to reach out to us.\n" +
							"\n" +
							"Best regards,\n" +
							"Customer Support Team";
	            File file = new File("C:\\Users\\ADMIN\\Downloads\\Issue Resolved Pdf.pdf");

				// Send email to the customer
				boolean emailSent = emailService.sendEmailWithAttachment(to, from, subject, text, file);

				if (emailSent) {
					System.out.println("Email sent successfully");
				} else {
					System.out.println("There was an error sending the email");
				}

				return ResponseEntity.ok(new MessageResponse("Complaint details updated successfully"));
			} else {
				return ResponseEntity.notFound().build(); // Complaint not found
			}
		}

	@PostMapping("/addFaqs")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> addFaqs(@RequestBody List<FAQ> faqs) {

	    List<FAQ> addedFaqs = new ArrayList<>();

	    for (FAQ faq : faqs) {
	        // Create a new FAQ entity
	        FAQ newFaq = new FAQ();
	        newFaq.setFaqType(faq.getFaqType());
	        newFaq.setQuestion(faq.getQuestion());
	        newFaq.setAnswer(faq.getAnswer());

	        // Save the new FAQ entity to the database
	        faqRepository.save(newFaq);
	        addedFaqs.add(newFaq);
	    }

	    return ResponseEntity.ok(new MessageResponse("FAQs added successfully!"));

	}

		@GetMapping("/getFaq/{faqId}")
		@PreAuthorize("hasRole('ADMIN')")
		public ResponseEntity<?> getFaqById(@PathVariable Long faqId) {
			FAQ faq = faqRepository.findById(faqId).orElse(null);

			if(faq != null) {
				return ResponseEntity.ok(faq);
			} else {
				return ResponseEntity.notFound().build();
			}

		}

		@PutMapping("/updateFaq/{faqId}")
		@PreAuthorize("hasRole('ADMIN')")
		public ResponseEntity<MessageResponse> updateFaq(@PathVariable Long faqId, @RequestBody FAQ updatedFAQ) {
			FAQ existingFAQ = adminService.updateFaqDetails(faqId, updatedFAQ);

			if (existingFAQ != null) {
				return ResponseEntity.ok(new MessageResponse("FAQ details updated successfully"));
			} else {
				return ResponseEntity.notFound().build(); //FAQ not found
			}


		}
		
		
		



}
