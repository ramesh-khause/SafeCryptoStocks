package com.SafeCryptoStocks.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.SafeCryptoStocks.model.User;
import com.SafeCryptoStocks.repository.UserRepository;
import com.SafeCryptoStocks.services.UserServices;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class UserProfileController {

    @Autowired
    UserServices userService;
    
    @Autowired
    private UserRepository userRepo;

    @Value("${upload.path}")
    private String uploadPath;

    @GetMapping("/profile")
    public String profile(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null) {
            return "redirect:/login"; // Redirect to login if no session
        }

        Long userId = (Long) session.getAttribute("userId"); // Assuming "userId" is stored in the session
        if (userId == null) {
            return "redirect:/login"; // Redirect to login if no user ID in session
        }

        // Fetch user data based on user ID
        User user = userService.findById(userId);

        if (user == null) {
            return "redirect:/login"; // Redirect to login if user is not found
        }

        // Add user object to the model
        model.addAttribute("user", user);

        return "profile"; // The profile.html view
    }

    @PostMapping("/uploadProfilePicture")
    public String uploadProfilePicture(@RequestParam("profilePicture") MultipartFile file,
                                       HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);

        if (session == null) {
            model.addAttribute("error", "Session expired. Please log in again.");
            return "redirect:/login";
        }

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            model.addAttribute("error", "User not logged in. Please log in again.");
            return "redirect:/login";
        }

        try {
            // Fetch the user from the database
            User user = userService.findById(userId);

            if (user == null) {
                model.addAttribute("error", "User not found.");
                return "redirect:/profile";
            }

            // Save the current password to prevent it from being overwritten
            String currentPassword = user.getPassword();

            // Define the directory to save the image
            String uploadDir = "src/main/resources/static/pic/";

            // Create a unique filename
            String fileName = userId + "_" + System.currentTimeMillis() + ".jpg";
            Path filePath = Paths.get(uploadDir + fileName);

            // Save the file to the defined path
            Files.write(filePath, file.getBytes());

            // Update the profilePicturePath in the user object
            String imagePath = "/pic/" + fileName;
            user.setProfilePicturePath(imagePath);

            // Re-set the password to its original value
            user.setPassword(currentPassword);

            // Save the updated user
            userService.updateUserProfile(user);

            // Update the session with the new profile picture URL
            session.setAttribute("profileUrl", imagePath);

            // Add success message
            model.addAttribute("message", "Profile picture uploaded successfully!");

            return "redirect:/profile";

        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("error", "Error uploading profile picture.");
            return "redirect:/profile";
        }
    }
}
