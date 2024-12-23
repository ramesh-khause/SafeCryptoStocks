package com.SafeCryptoStocks.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.SafeCryptoStocks.model.Portfolio;
import com.SafeCryptoStocks.model.User;
import com.SafeCryptoStocks.services.DashboardService;
import com.SafeCryptoStocks.services.MarketService;
import com.SafeCryptoStocks.services.PortfolioService;
import com.fasterxml.jackson.databind.JsonNode;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@CrossOrigin(origins = "http://localhost:3000")
public class DashboardController {

	@Autowired
	private DashboardService dashboardService;
	
	PortfolioService portfolioService;
	
	User user;
	
	 @Autowired
	    private MarketService marketService;
	
	 @GetMapping("/market")
	    public String showMarket(Model model,HttpServletRequest request)
	    {
		 JsonNode cryptoData = marketService.getTopCryptocurrencies();
	        model.addAttribute("cryptoData", cryptoData); // Pass all relevant data
	        HttpSession session = request.getSession(false);
	        if (session == null) {
	            return "redirect:/home";
	        }
	        
	        Long userId = (Long) session.getAttribute("userId"); // Retrieve userId from session
	        if (userId == null) {
	            return "redirect:/home";  // Redirect to login if no userId found in the session
	        }
	        
	        String userName = (String) session.getAttribute("userName");
	        model.addAttribute("userName", userName);
	        
	        String  profileUrl=(String) session.getAttribute("profileUrl");
	        model.addAttribute("profileUrl",profileUrl);
              
	        System.out.println("pic url:  "+profileUrl);
	        return "market"; 
	    }
	
	 @GetMapping("/budget")
	    public String showBudget(Model model,HttpServletRequest request)
	    {
		 HttpSession session = request.getSession(false);
	        if (session == null) {
	            return "redirect:/home";
	        }
	        
	        Long userId = (Long) session.getAttribute("userId"); // Retrieve userId from session
	        if (userId == null) {
	            return "redirect:/home";  // Redirect to login if no userId found in the session
	        }
	        
	        String userName = (String) session.getAttribute("userName");
	        model.addAttribute("userName", userName);
	     
	        String firstName = (String) session.getAttribute("firstName");
	        model.addAttribute("firstName", firstName);
	        
	        String lastName = (String) session.getAttribute("lastName");
	        model.addAttribute("lastName", lastName);
	       
	        
	        String  profileUrl=(String) session.getAttribute("profileUrl");
	        model.addAttribute("profileUrl",profileUrl);
              
	        System.out.println("pic url:  "+profileUrl);
	        
	        System.out.println(userName);
	        System.out.println(firstName+"  "+lastName);
	        
	        return "budget"; 
	    }
	
	 @GetMapping("/learn")
	    public String learn(Model model,HttpServletRequest request)
	    {
		 HttpSession session = request.getSession(false);
	        if (session == null) {
	            return "redirect:/home";
	        }
	        
	        Long userId = (Long) session.getAttribute("userId"); // Retrieve userId from session
	        if (userId == null) {
	            return "redirect:/home";  // Redirect to login if no userId found in the session
	        }
	        
	        String userName = (String) session.getAttribute("userName");
	        model.addAttribute("userName", userName);
	        
	        String  profileUrl=(String) session.getAttribute("profileUrl");
	        model.addAttribute("profileUrl",profileUrl);
              
	        System.out.println("pic url:  "+profileUrl);
	        
	        return "learn"; 
	    }
	 
	
//	


	 @GetMapping("/create-portfolio/{userId}")
	 public String showPortfolioForm(@PathVariable String userId, Model model) {
	     model.addAttribute("portfolio", new Portfolio());
	     model.addAttribute("userId", userId); // Pass the userId to the model if needed
	     System.out.println("User ID on create-portfolio page: " + userId);

	     return "createPortfolio"; // Renders createPortfolio.html
	 }
	 
    @GetMapping("/dashboard/{userId}")
    public String showDashboard(@PathVariable Long userId, Model model) {
        // Logic for showing the dashboard for the given userId
        // You can add user-specific data here if needed
        model.addAttribute("userId", userId);
        return "dashboard"; // Thymeleaf template name
    }

    
  @GetMapping("/dashboard")
  public String dashboard(HttpServletRequest request, Model model) {
	  
	  JsonNode cryptoData = dashboardService.getCryptocurrencies();
      model.addAttribute("cryptoData", cryptoData); // Pass all relevant data
      
      JsonNode trendingData = dashboardService.getTrendingCryptocurrencies();
      model.addAttribute("trendingData", trendingData);
      
      JsonNode newsData = dashboardService.getCryptoNews();
      model.addAttribute("newsData", newsData);
      
	  HttpSession session = request.getSession(false);
      if (session == null) {
          return "redirect:/home";
      }

      Long userId = (Long) session.getAttribute("userId"); // Retrieve userId from session
      if (userId == null) {
          return "redirect:/home";  // Redirect to login if no userId found in the session
      }
      
      String userName = (String) session.getAttribute("userName");
      model.addAttribute("userName", userName);
      
      String firstName = (String) session.getAttribute("firstName");
      model.addAttribute("firstName", firstName);
      
      String lastName = (String) session.getAttribute("lastName");
      model.addAttribute("lastName", lastName);
      System.out.println(firstName+"  "+lastName);
      
      String  profileUrl=(String) session.getAttribute("profileUrl");
      model.addAttribute("profileUrl",profileUrl);
        
      System.out.println("pic url:  "+profileUrl);
      return "dashboard";
  }
  
  
  @GetMapping("/portfolio")
  public String portfolio(HttpServletRequest request, Model model) {
      HttpSession session = request.getSession(false); // Get the current session (or return null if no session exists)
      if (session == null) {
          return "redirect:/home";  // Redirect to login if the session does not exist
      }

      Long userId = (Long) session.getAttribute("userId"); // Retrieve userId from session
      if (userId == null) {
          return "redirect:/home";  // Redirect to login if no userId found in the session
      }
      System.out.println("userId = "+userId);
      model.addAttribute("userId", userId); // Add the userId to the model to pass it to Thymeleaf
      String userName = (String) session.getAttribute("userName");  // Retrieve userName from session
      model.addAttribute("userName", userName); // Add userName to the model as well
      
      String  profileUrl=(String) session.getAttribute("profileUrl");
      model.addAttribute("profileUrl",profileUrl);
        
      System.out.println("pic url:  "+profileUrl);

      return "portfolio"; // Return the Thymeleaf template to be rendered
  }



  
 


}
