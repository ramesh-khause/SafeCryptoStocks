package com.SafeCryptoStocks.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.SafeCryptoStocks.model.User;
import com.SafeCryptoStocks.services.EmailService;
import com.SafeCryptoStocks.services.JwtService;
import com.SafeCryptoStocks.services.UserServices;
import com.SafeCryptoStocks.utils.OtpUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@CrossOrigin(origins = "http://localhost:3000")
public class HomeController {

    @Autowired
    JwtService jwtService;

    @Autowired
    private UserServices userServices;

    @Autowired
    private EmailService emailService;

    private Map<String, String> otpStorage = new HashMap<>();

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }

    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return "redirect:/login";
        }
        String userName = (String) session.getAttribute("userName");
        model.addAttribute("userName", userName);
        return "dashboard";
    }
    
    
//    @GetMapping("/forgotPwd")
//    public String forgetPwd(Model model) {
//        model.addAttribute("user", new User());
//        return "forgotPwd";
//    }
//    
//    
//    @GetMapping("/verifyOtp")
//    public String verifyOtp(Model model) {
//        model.addAttribute("user", new User());
//        return "verifyOtp";
//    }
//    
//    
    
    
    
    
    
    
    

    @PostMapping("/registerUser")
    public ResponseEntity<Map<String, Object>> registerUser(@Valid @RequestBody User user, BindingResult result) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (result.hasErrors()) {
                response.put("success", false);
                response.put("message", "Validation errors: " + result.toString());
                return ResponseEntity.badRequest().body(response);
            }
            userServices.registerUser(user);
            response.put("success", true);
            response.put("message", "Registration successful!");
            return ResponseEntity.ok(response);
        } catch (DataIntegrityViolationException e) {
            response.put("success", false);
            response.put("message", "Email or username already exists!");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "An error occurred. Please try again.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    
    @PostMapping("/loginUser")
    public ResponseEntity<Map<String, Object>> loginUser(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();
        
        // Validate user credentials
        boolean isValid = userServices.validateUserLogin(user.getEmail(), user.getPassword());
        
        if (isValid) {
            // Find the user from the database using email (ensure user exists)
            User authenticatedUser = userServices.findByEmail(user.getEmail());
            
            if (authenticatedUser == null) {
                response.put("success", false);
                response.put("message", "User not found.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // Generate OTP
            String otp = OtpUtil.generateOtp();
            otpStorage.put(user.getEmail(), otp);
            
            // Send OTP email
            try {
                emailService.sendOtpEmail(user.getEmail(), "Your OTP Code", "Your OTP code is " + otp);
            } catch (Exception e) {
                response.put("success", false);
                response.put("message", "Failed to send OTP email: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }

            // Generate JWT token (before OTP verification)
            String token = jwtService.generateToken(user.getEmail());

            // Inform user that OTP has been sent and JWT token has been generated
            response.put("success", true);
            response.put("message", "OTP sent to your email. You can verify it to complete the login.");
            response.put("token", token);  // Include the JWT token for immediate use (optional)

            return ResponseEntity.ok(response);
        } else {
            // Prepare failure response for invalid credentials
            response.put("success", false);
            response.put("message", "Invalid credentials!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
 
    
    @PostMapping("/verifyOtp")
    public ResponseEntity<Map<String, Object>> verifyOtp(@RequestBody Map<String, String> otpData, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        String email = otpData.get("email");
        String otp = otpData.get("otp");
        
        // Check if email or OTP is missing
        if (email == null || otp == null) {
            response.put("success", false);
            response.put("message", "Email and OTP are required.");
            return ResponseEntity.badRequest().body(response);
        }

        // Retrieve session if it exists
        HttpSession session = request.getSession(true); // Do not create a new session
        if (session == null) {
            response.put("success", false);
            response.put("message", "Session expired. Please login again.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        // Verify the OTP against the stored value
        if (otpStorage.containsKey(email) && otpStorage.get(email).equals(otp)) {
            // OTP is valid; proceed to user verification
            User user = userServices.findByEmail(email);
            if (user != null) {
                // Store user information in session for continuity
                session.setAttribute("userName", user.getUsername());
                otpStorage.remove(email); // Clear OTP after verification
                
                // Generate JWT token
                String token = jwtService.generateToken(user.getEmail());

                response.put("success", true);
                response.put("message", "OTP verified. Login successful!");
                response.put("token", token); // Include JWT token for further authentication
                response.put("user", user); // Include user details if required
            } else {
                // User not found for the provided email
                response.put("success", false);
                response.put("message", "User not found!");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } else {
            // Invalid OTP or email
            response.put("success", false);
            response.put("message", "Invalid OTP or email!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        return ResponseEntity.ok(response);
    }


    @PostMapping("/forgotPassword")
    public ResponseEntity<Map<String, Object>> forgotPassword(@RequestBody Map<String, String> requestData) {
        Map<String, Object> response = new HashMap<>();
        String email = requestData.get("email");

        if (userServices.checkEmail(email)) {
            String resetToken = OtpUtil.generateOtp();
            otpStorage.put(email, resetToken);
            emailService.sendOtpEmail(email, "Password Reset Request", "Your reset OTP is: " + resetToken);
            response.put("success", true);
            response.put("message", "Password reset OTP sent to your email.");
            System.out.println("OTP for email " + email + " is " + resetToken); // Log OTP for debugging
        } else {
            response.put("success", false);
            response.put("message", "No user found with the provided email.");
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/verifyPasswordResetOtp")
    public ResponseEntity<Map<String, Object>> verifyPasswordResetOtp(@RequestBody Map<String, String> otpData, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        String email = otpData.get("email");
        String otp = otpData.get("otp");

        if (otpStorage.containsKey(email) && otpStorage.get(email).equals(otp)) {
            otpStorage.remove(email);

            HttpSession session = request.getSession(true); // Ensure new session if needed
            session.setAttribute("userEmail", email); // Save the user's email to the session for password reset

            response.put("success", true);
            response.put("message", "OTP verified. You can now reset your password.");
            System.out.println("OTP verified for email " + email); // Log OTP verification success
        } else {
            response.put("success", false);
            response.put("message", "Invalid OTP!");
            System.out.println("Failed OTP verification for email " + email + ". Provided OTP: " + otp + ", Expected OTP: " + otpStorage.get(email)); // Log OTP verification failure
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<Map<String, Object>> resetPassword(@RequestBody Map<String, String> resetData, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        HttpSession session = request.getSession(false);
        if (session == null) {
            response.put("success", false);
            response.put("message", "Session expired. Please start over.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        String email = (String) session.getAttribute("userEmail");

        if (email == null) {
            response.put("success", false);
            response.put("message", "No user found for this session.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        String newPassword = resetData.get("newPassword");
        User user = userServices.findByEmail(email);
        if (user != null) {
            userServices.updatePassword(user, newPassword); 
            response.put("success", true);
            response.put("message", "Password reset successful.");
        } else {
            response.put("success", false);
            response.put("message", "No user found with the provided email.");
        }

        return ResponseEntity.ok(response);
    }
}
