package com.SafeCryptoStocks.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.mindrot.jbcrypt.BCrypt;

import com.SafeCryptoStocks.model.User;
import com.SafeCryptoStocks.repository.UserRepository;

@Service
public class UserServicesImp implements UserServices {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authManager;  

    @Override
    public User registerUser(User user) {
        // Hash password before saving
        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        User savedUser = userRepository.save(user);
        System.out.println("Registered User: " + savedUser.toString());
        return savedUser;
    }

    @Override
    public boolean checkEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public User findByEmail(String email) {
        User user = userRepository.findByEmail(email);
        System.out.println("Found User: " + (user != null ? user.toString() : "No user found with email: " + email));
        return user;
    }

    @Override
    public boolean validateUserLogin(String email, String password) {
        User user = findByEmail(email);
        if (user != null && BCrypt.checkpw(password, user.getPassword())) {
            System.out.println("Login successful for user: " + user.toString());
            // Authentication is handled by Spring Security, no need for JWT.
            return true;
        }
        System.out.println("Invalid login attempt for email: " + email);
        return false;
    }

    @Override
    public void updatePassword(User user, String newPassword) {
        user.setPassword(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
        userRepository.save(user);
        System.out.println("Password updated for user: " + user.toString());
    }

    // Removed the jwtService-related code since we are using session-based authentication now.
    @Override
    public String verify(User user) {
        // Authentication using Spring Security's AuthenticationManager
        Authentication authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
        
        if (authentication.isAuthenticated()) {
            // Set the authentication in the SecurityContextHolder to create a session
            SecurityContextHolder.getContext().setAuthentication(authentication);
            System.out.println("User authenticated and session established for: " + user.getEmail());
            return "success";  // Return success message after session is established
        }
        
        return "fail";  // Return failure message if authentication fails
    }

    @Override
    public User findById(Long id) {
        // Fetch user by ID
        return userRepository.findById(id).orElse(null);
    }

	@Override
	public void updateUserProfile(User user) {
		// TODO Auto-generated method stub
		 // Fetch the current user entity from the database
	    User existingUser = userRepository.findById(user.getId()).orElseThrow(() -> new IllegalArgumentException("User not found"));

	    // Retain the password from the existing user object (avoid re-encoding)
	    user.setPassword(existingUser.getPassword());

	    // Save the updated user entity
	    userRepository.save(user);	
	}
}
