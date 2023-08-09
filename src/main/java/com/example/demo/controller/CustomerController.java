package com.example.demo.controller;

import java.util.Date;


import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.Repository.ComplaintRepository;
import com.example.demo.Repository.CustomerRepository;
import com.example.demo.Repository.FAQRepository;
import com.example.demo.Service.CustomerService;
import com.example.demo.entity.Complaint;
import com.example.demo.entity.Customer;
import com.example.demo.entity.FAQ;
import com.example.demo.payload.request.LoginRequest;
import com.example.demo.payload.request.SignupRequest;
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
  PasswordEncoder encoder;
  
  @Autowired
  private CustomerService customerService;


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
      if (customerRepository.existsByName(signUpRequest.getUsername())) {
          return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
      }

      if (customerRepository.existsByEmail(signUpRequest.getEmail())) {
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
  
 
  @PostMapping("/{customerId}/add-complaint")
  public ResponseEntity<?> addComplaint(@PathVariable Long customerId, @Valid @RequestBody Complaint complaint) {
      Customer customer = customerRepository.findById(customerId).orElse(null);

      if (customer != null) {
          // Create a new Complaint entity based on the request
          Complaint newComplaint = new Complaint();
          newComplaint.setComplaintType(complaint.getComplaintType());
          newComplaint.setCustomerid(customerId);
          newComplaint.setDate(new Date());
          newComplaint.setStatus("Pending");
          newComplaint.setDescription(complaint.getDescription());

          // Save the new complaint entity to the database
          complaintRepository.save(newComplaint);

          return ResponseEntity.ok(new MessageResponse("Complaint added successfully!"));
      } else {
          return ResponseEntity.badRequest().body(new MessageResponse("Customer not found for the provided ID."));
      }
  }
  
  @GetMapping("/{customerId}") // To get customer details
  public ResponseEntity<?> getCustomerById(@PathVariable Long customerId) {
      Customer customer = customerRepository.findById(customerId).orElse(null);

      if (customer != null) {
        
          customer.setPassword(null); // Remove password from the response
          
          return ResponseEntity.ok(customer);
      } else {
          return ResponseEntity.notFound().build();
      }
  }
  
  
  @PutMapping("UpdateCustomer/{customerId}")
  public ResponseEntity<?> updateCustomerDetails(@PathVariable Long customerId, @RequestBody Customer updatedCustomer) {
      Customer updated = customerService.updateCustomerDetails(customerId, updatedCustomer);

      if (updated != null) {
          return ResponseEntity.ok(new MessageResponse("Customer details updated successfully!"));
      } else {
          return ResponseEntity.notFound().build(); // Customer not found
      }
  }

  @PutMapping("/{customerId}/rate")
  public ResponseEntity<?> rateComplaint(@PathVariable Long customerId, @RequestBody Double rating) {
      Customer customer = customerRepository.findById(customerId).orElse(null);

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
  
  @GetMapping("/getFaq/{faqId}")
  public ResponseEntity<?> getFaqById(@PathVariable Long faqId) {
      FAQ faq = faqRepository.findById(faqId).orElse(null);

      if(faq != null) {
          return ResponseEntity.ok(faq);
      } else {
          return ResponseEntity.notFound().build();
      }

  }
  
  @GetMapping("/getAllFaqs/{faqType}")
  public ResponseEntity<?> getAllFaqsByType(@PathVariable String faqType) {
      List<FAQ> faqs = faqRepository.findAllByFaqType(faqType);

      if (!faqs.isEmpty()) {
          return ResponseEntity.ok(faqs);
      } else {
          return ResponseEntity.notFound().build();
      }


  }

  
  /*
   @PostMapping("/{customerId}/add-complaint")
   public ResponseEntity<?> addComplaint(@PathVariable Long customerId, @Valid @RequestBody Complaint complaint) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    Long userId = userDetails.getUserId(); // Get the customer ID from the authenticated user
    
   
    Complaint newComplaint = new Complaint();
    newComplaint.setComplaintType(complaint.getComplaintType());
    newComplaint.setCustomerid(customerId);
    newComplaint.setDate(new Date());
    newComplaint.setStatus("Pending");
    newComplaint.setDescription(complaint.getDescription());

    // Save the new complaint entity to the database
    complaintRepository.save(newComplaint);

    return ResponseEntity.ok(new MessageResponse("Complaint added successfully!"));
}
 */
  

}
