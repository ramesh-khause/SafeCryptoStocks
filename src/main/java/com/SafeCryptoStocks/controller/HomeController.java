package com.SafeCryptoStocks.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.SafeCryptoStocks.model.User;
import com.SafeCryptoStocks.services.EmailService;
import com.SafeCryptoStocks.services.UserServices;
import com.SafeCryptoStocks.utils.OtpUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@Validated
public class HomeController {

    @Autowired
    private UserServices userServices;

    @Autowired
    private EmailService emailService;

    private Map<String, String> otpStorage = new HashMap<>();


    @GetMapping("/")
    public String index() {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String home() {
        return "home";
    }
    
    
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

    @GetMapping("/me")
    public ResponseEntity<User> getLoggedInUser(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId"); // Session attribute
        User user = userServices.findById(userId);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userServices.findById(id); // Fetch user by ID
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
   
    // Handle form submission
    @PostMapping("/registerUser")
    public ResponseEntity<Map<String, Object>> registerUser(
            @Valid @ModelAttribute("user") User user,
            BindingResult bindingResult) {

        Map<String, Object> response = new HashMap<>();
        
        if (bindingResult.hasErrors()) {
            response.put("message", "Please correct the errors in the form!");
            response.put("messageType", "error");
            return ResponseEntity.badRequest().body(response); // Return 400 Bad Request with error message
        }

        try {
            userServices.registerUser(user);
            String emailBody = emailService.createRegistrationEmailBody(user.getFirstname(), user.getLastname());
            emailService.sendHtmlEmail(user.getEmail(), "Registration Successful - Welcome to SafeCryptoStocks", emailBody);

            response.put("message", "Registration successful...");
            response.put("messageType", "success");
            return ResponseEntity.ok(response); // Return 200 OK with success message
        } catch (DataIntegrityViolationException e) {
            response.put("message", "Email or username already exists!");
            response.put("messageType", "error");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response); // Return 400 Bad Request
        } catch (Exception e) {
            response.put("message", "An error occurred. Please try again.");
            response.put("messageType", "error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // Return 500 Internal Server Error
        }
    }



    @PostMapping("/loginUser")
    public ResponseEntity<Map<String, Object>> loginUser(@RequestBody User user, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        // Validate request body for empty email or password
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "Email is required.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "Password is required.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        boolean isValid = userServices.validateUserLogin(user.getEmail(), user.getPassword());

        if (isValid) {
            User authenticatedUser = userServices.findByEmail(user.getEmail());

            if (authenticatedUser == null) {
                response.put("success", false);
                response.put("message", "User not found.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            String otp = OtpUtil.generateOtp();
            otpStorage.put(user.getEmail(), otp);

            try {
                emailService.sendOtpEmail(user.getEmail(), "Your OTP Code", "Your OTP code is " + otp);
            } catch (Exception e) {
                response.put("success", false);
                response.put("message", "Failed to send OTP email: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }

            response.put("success", true);
            response.put("message", "OTP sent to your email...");
            return ResponseEntity.ok(response);
        } else {
            emailService.sendLoginFailureAlert(user.getEmail());
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

        if (email == null || otp == null) {
            response.put("success", false);
            response.put("message", "Email and OTP are required.");
            return ResponseEntity.badRequest().body(response);
        }

        if (otpStorage.containsKey(email) && otpStorage.get(email).equals(otp)) {
            User user = userServices.findByEmail(email);
            if (user != null) {
                HttpSession session = request.getSession(true);
                session.setAttribute("userName", user.getUsername());
                session.setAttribute("userId", user.getId());
                session.setAttribute("firstName", user.getFirstname());
                session.setAttribute("lastName", user.getLastname());
                session.setAttribute("profileUrl", user.getProfilePicturePath());
                otpStorage.remove(email); 

                emailService.sendLoginNotification(user.getEmail(), user.getFirstname(), user.getLastname());

                response.put("success", true);
                response.put("message", "OTP verified. Login successful!");
                response.put("user", user); 
            } else {
                response.put("success", false);
                response.put("message", "User not found!");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } else {
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

            HttpSession session = request.getSession(true);
            session.setAttribute("userEmail", email); 

            response.put("success", true);
            response.put("message", "OTP verified. You can now reset your password.");
        } else {
            response.put("success", false);
            response.put("message", "Invalid OTP!");
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
            response.put("message", "User not found.");
        }

        return ResponseEntity.ok(response);
    }
}
