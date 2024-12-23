package com.SafeCryptoStocks.controller;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class SecurityController {

	 @GetMapping("/security")
	    public String home(HttpServletRequest request) {
	        //model.addAttribute("user", new User());
	        return "login"+ request.getSession().getId();
	    }
 
	 @GetMapping("/csrf-token")
	 public CsrfToken getCsrfToken(HttpServletRequest request)
	 {
		return (CsrfToken) request.getAttribute("_csrf");
		 
	 }
	 
	 
}

