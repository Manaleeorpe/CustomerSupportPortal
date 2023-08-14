package com.example.demo.controller;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.example.demo.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
import com.example.demo.Repository.CustomerRepository;
import com.example.demo.Repository.FAQRepository;
import com.example.demo.entity.Admin;
import com.example.demo.entity.Complaint;
import com.example.demo.entity.Customer;
import com.example.demo.entity.FAQ;
import com.example.demo.payload.request.LoginRequest;
import com.example.demo.payload.request.ResetPasswordRequest;
import com.example.demo.payload.request.SignupRequest;
import com.example.demo.response.ComplaintResponse;
import com.example.demo.response.MessageResponse;
import com.example.demo.response.UserInfoResponse;
import com.example.demo.security.jwt.JwtUtils;
import com.example.demo.security.services.UserDetailsImpl;


@RestController
@RequestMapping("/auth/customer")
public class CustomerController {

  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  CustomerRepository customerRepository;
  
  @Autowired
  ComplaintRepository complaintRepository;
  
  @Autowired
  AdminRepository adminRepository;

  @Autowired
  PasswordEncoder encoder;
  
  @Autowired
  private CustomerService customerService;
  @Autowired
  private ComplaintService complaintService;
  @Autowired
  private ChatbotService chatbotService;
  @Autowired
  private EmailService emailService;

  @Autowired
  private AdminService adminService;

  @Autowired
  private ChatbotFAQsService chatbotFAQsService;

  @Autowired
  private ChatbotInteractService chatbotInteractService;
  
  @Autowired
  private TokenService tokenService;

    @Autowired
    public CustomerController(CustomerService customerService, ComplaintService complaintService, ChatbotService chatbotService, ChatbotFAQsService chatbotFAQsService, EmailService emailService, ChatbotInteractService chatbotInteractService) {
        super();
        this.chatbotFAQsService = chatbotFAQsService;
        this.customerService = customerService;
        this.complaintService = complaintService;
        this.chatbotService = chatbotService;
        this.emailService = emailService;
        this.tokenService = tokenService;
        this.chatbotInteractService = chatbotInteractService;
    }

@Autowired
  JwtUtils jwtUtils;
  

  @Autowired
  private FAQRepository faqRepository;

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
      Customer customer = new Customer(signUpRequest.getUsername(),
                                        signUpRequest.getEmail(),
                                        signUpRequest.getPhone_number(),
                                        encoder.encode(signUpRequest.getPassword()));
      customerRepository.save(customer);

      return ResponseEntity.ok(new MessageResponse("Customer registered successfully!"));
  }


  @PostMapping("/signout")
  public ResponseEntity<?> logoutUser() {
    ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
    return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
        .body(new MessageResponse("You've been signed out!"));
  }

  @PostMapping("/{customerid}/add-complaint")
  @PreAuthorize("hasRole('CUSTOMER')")
  public ResponseEntity<?> addComplaint(@PathVariable Long customerid, @Valid @RequestBody Complaint complaint) {
      Customer customer = customerRepository.findById(customerid).orElse(null);

      if (customer != null) {
          // Determine severity level and generate response using chatbot service
          String severityLevel = chatbotService.determineSeverity(complaint.getDescription());
          String response = chatbotService.processComplaint(complaint.getDescription());

          //get FAQ from chatbotFAQs service
          String FAQType = chatbotFAQsService.determineType(complaint.getDescription());
          List<FAQ> faqs = faqRepository.findAllByFaqType(FAQType);

          // Create a new Complaint entity based on the request
          Complaint newComplaint = new Complaint();
          newComplaint.setComplaintType(severityLevel); // Set the severity level as complaintType
          newComplaint.setCustomerid(customerid);
          newComplaint.setDate(new Date());
          newComplaint.setStatus("Pending");
          newComplaint.setDescription(complaint.getDescription());

          // Save the new complaint entity to the database
          complaintRepository.save(newComplaint);
          Admin addedAdmin = complaintService.AddComplaintToAdmin(newComplaint.getComplaintid());
          complaintService.CalculateHours();
          if (addedAdmin == null) {
              return ResponseEntity.badRequest().body(new MessageResponse("Failed to associate complaint with admin."));
          }

          // Send email to the customer
          String to = customer.getEmail();
          String from = "customerportal45@gmail.com";
          String subject = "Acknowledgement of Your Recent Complaint";
          String text = "Dear " + customer.getName() + ",\n" +
                  "\n" +
                  "We hope this email finds you well. We wanted to take a moment to acknowledge the complaint you recently submitted on our website with Complaint ID : " + newComplaint.getComplaintid() + ". Your feedback is important to us, and we're committed to addressing your concerns as swiftly as possible.\n" +
                  "\n" +
                  "We are reaching out to let you know that your complaint is currently under review by our dedicated team led by " + addedAdmin.getName() + ". We understand the importance of timely resolution, and please be assured that we are actively working on a solution.\n" +
                  "\n" +
                  "We take your concerns seriously and are committed to addressing them promptly. Our customer support team, led by " + addedAdmin.getName() + ", has been informed about your complaint and is already working on finding a solution. We will strive to resolve this matter to your satisfaction and ensure that such issues do not recur in the future.\n" +
                  "\n" +
                  "Thank you for bringing this matter to our attention. We look forward to resolving it to your satisfaction.";

          // Provide the correct path to the attachment file
          File file = new File("D:\\axis\\Capstone\\emailservice\\Customer Email Pdf.pdf");

          boolean emailSent = emailService.sendEmailWithAttachment(to, from, subject, text, file);

          if (emailSent) {
              System.out.println("Email sent successfully");
          } else {
              System.out.println("There was an error sending the email");
          }

          ComplaintResponse complaintResponse = new ComplaintResponse(response, addedAdmin.getName(), faqs, addedAdmin.getAdminid());

          return ResponseEntity.ok(complaintResponse);

      } else {
          return ResponseEntity.badRequest().body(new MessageResponse("Customer not found for the provided ID."));
      }
  }
    @GetMapping("/{customerid}") // To get customer details
  public ResponseEntity<?> getCustomerById(@PathVariable Long customerid) {
      Customer customer = customerRepository.findById(customerid).orElse(null);

      if (customer != null) {
        
          customer.setPassword(null); // Remove password from the response
          
          return ResponseEntity.ok(customer);
      } else {
          return ResponseEntity.notFound().build();
      }
  }
  
  
  @PutMapping("UpdateCustomer/{customerid}")
  @PreAuthorize("hasRole('CUSTOMER')")
  public ResponseEntity<?> updateCustomerDetails(@PathVariable Long customerid, @RequestBody Customer updatedCustomer) {
      Customer updated = customerService.updateCustomerDetails(customerid, updatedCustomer);

      if (updated != null) {
          return ResponseEntity.ok(new MessageResponse("Customer details updated successfully!"));
      } else {
          return ResponseEntity.notFound().build(); // Customer not found
      }
  }

  @PutMapping("/{customerid}/rate")
  @PreAuthorize("hasRole('CUSTOMER')")
  public ResponseEntity<?> rateComplaint(@PathVariable Long customerid, @RequestBody Double rating) {
      Customer customer = customerRepository.findById(customerid).orElse(null);

      if (customer != null) {
          List<Complaint> customerComplaints = customer.getComplaints(); // Use the new getter method

          for (Complaint complaint : customerComplaints) {
              if (complaint.getStatus().equals("Resolved")) {
                  complaint.setRating(rating);
                  complaintRepository.save(complaint);
              
          }
          }
          return ResponseEntity.ok(new MessageResponse("Complaints rated successfully!"));
      } else {
          return ResponseEntity.notFound().build(); // Customer not found
      }
  }

    @PutMapping("/{customerid}/cancel-complaint/{complaintid}")
    public ResponseEntity<?> cancelComplaint(@PathVariable Long customerid, @PathVariable Long complaintid) {
        Customer customer = customerRepository.findById(customerid).orElse(null);

        if (customer != null) {
            Optional<Complaint> optionalComplaint = complaintRepository.findById(complaintid);

            if (optionalComplaint.isPresent()) {
                Complaint complaint = optionalComplaint.get();

                if (complaint.getCustomerid().equals(customerid) && complaint.getStatus().equals("Pending")) {
                    complaint.setStatus("Cancelled");
                    complaintRepository.save(complaint);
                    complaintService.unassignAdmin(complaintid);
                    complaintService.CalculateHours();
                    return ResponseEntity.ok(new MessageResponse("Complaint cancelled successfully!"));
                } else {
                    return ResponseEntity.badRequest().body(new MessageResponse("Cannot cancel this complaint."));
                }
            } else {
                return ResponseEntity.notFound().build(); // Complaint not found
            }
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("Customer not found for the provided ID."));
        }
    }

    @GetMapping("/getAllFaqs")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> getAllFaqs() {
        List<FAQ> allFaqs = faqRepository.findAll();

        if (!allFaqs.isEmpty()) {
            return ResponseEntity.ok(allFaqs);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/getAllFaqs/{faqType}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> getAllFaqsByType(@RequestParam String faqType) {
        List<FAQ> faqs = faqRepository.findAllByFaqType(faqType);

        if (!faqs.isEmpty()) {
            return ResponseEntity.ok(faqs);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    //forgot password
  @PostMapping("/forgot-password")
  public ResponseEntity<?> forgotPassword(@RequestParam String email) {
      Customer customer = customerRepository.findByEmail(email);

      if (customer == null) {
          return ResponseEntity.badRequest().body(new MessageResponse("No user found with the provided email."));
      }

      // Generate a password reset token and construct the reset link
      String token = tokenService.generatePasswordResetToken(customer.getCustomerid());
      String resetLink = "http://localhost:8080/auth/customer/reset-password?token=" + token;

      // Compose the email content
      String subject = "Password Reset Request";
      String text = "To reset your password, please click the link below:\n" + resetLink;

      // Send the password reset email
      if (emailService.sendPasswordResetVerificationEmail(email, "your-email@example.com", subject, text)) {
          return ResponseEntity.ok(new MessageResponse("Password reset instructions sent to your email."));
      } else {
          return ResponseEntity.badRequest().body(new MessageResponse("Failed to send password reset email."));
      }
  }
  @GetMapping("/reset-password")
  public ResponseEntity<?> resetPasswordPage(@RequestParam String token) {
      Long customerId = tokenService.getUserIdFromToken(token);

      if (customerId != null) {
          // Return a JSON response containing the verified customerId
          return ResponseEntity.ok().body("Token verified.");
      } else {
          return ResponseEntity.badRequest().body(new MessageResponse("Invalid token or token expired."));
      }
  }

  @PostMapping("/reset-password")
  public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
      // Validate token and retrieve user ID
      Long customerId = tokenService.getUserIdFromToken(resetPasswordRequest.getToken());

      if (customerId != null) {
          // Fetch the customer by ID
          Customer customer = customerRepository.findById(customerId).orElse(null);

          if (customer != null) {
              // Update the customer's password
              customer.setPassword(encoder.encode(resetPasswordRequest.getPassword()));
              customerRepository.save(customer);

              return ResponseEntity.ok(new MessageResponse("Password reset successfully."));
          } else {
              return ResponseEntity.badRequest().body(new MessageResponse("User not found."));
          }
      } else {
          return ResponseEntity.badRequest().body(new MessageResponse("Invalid token or token expired."));
      }
  }

    @PostMapping("/chat")
    public ResponseEntity<String> chatWithCustomer(@RequestBody String userInput) {
        String response = chatbotInteractService.generateResponse(userInput);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }
}
