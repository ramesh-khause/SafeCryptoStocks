package com.SafeCryptoStocks.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.mindrot.jbcrypt.BCrypt;

import com.SafeCryptoStocks.model.User;
import com.SafeCryptoStocks.repository.UserRepository;

@Service
public class UserServicesImp implements UserServices {

    
	@Autowired
	private JwtService jwtService;
	
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

	@Override
	public String verify(User user) {
		// TODO Auto-generated method stub
		
		Authentication authentication=authManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
		
		if(authentication.isAuthenticated())
		return jwtService.generateToken(user.getEmail());
		
		return "fail";
	}
}
