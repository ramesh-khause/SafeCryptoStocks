package com.SafeCryptoStocks.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.SafeCryptoStocks.model.Portfolio;
import com.SafeCryptoStocks.model.Stock;
import com.SafeCryptoStocks.services.PortfolioService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/portfolios")
public class PortfolioController {

    @Autowired
    private PortfolioService portfolioService;

    
 
       
    
   
    
    
    @PostMapping("/{userId}")
    public ResponseEntity<Portfolio> createPortfolio(
            @PathVariable Long userId,
            @RequestBody Portfolio portfolio,
            @RequestParam double budgetAmount) {
        Portfolio createdPortfolio = portfolioService.addPortfolio(userId, portfolio, budgetAmount);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPortfolio);
    }

   

    
    
    @GetMapping("/{portfolioId}")
    public ResponseEntity<Portfolio> getPortfolioById(@PathVariable Long portfolioId) {
        Portfolio portfolio = portfolioService.getPortfolioById(portfolioId);
        return ResponseEntity.ok(portfolio);
    }
    
    
    //get portfolio by userId
    @GetMapping("/user")
    public List<Portfolio> getUserPortfolios(HttpSession session, Model model) {
    	Long id = (Long) session.getAttribute("userId");
    	//session.setAttribute("userId", userId); // Ensure this is correctly set
    	System.out.println("userId = "+id);
    	model.addAttribute("userId", id);  // Add to the model for Thymeleaf
        return portfolioService.getPortfoliosByUser(id);
    }



    @PutMapping("/{portfolioId}")
    public ResponseEntity<Portfolio> updatePortfolio(@PathVariable Long portfolioId, @RequestBody Portfolio portfolio) {
        Portfolio updatedPortfolio = portfolioService.updatePortfolio(portfolioId, portfolio);
        return ResponseEntity.ok(updatedPortfolio);
    }
    


    @DeleteMapping("/{portfolioId}")
    public ResponseEntity<Void> deletePortfolio(@PathVariable Long portfolioId) {
        portfolioService.deletePortfolio(portfolioId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{portfolioId}/summary")
    public ResponseEntity<Map<String, Double>> getPortfolioSummary(@PathVariable Long portfolioId) {
        Map<String, Double> summary = portfolioService.calculatePortfolioSummary(portfolioId);
        return ResponseEntity.ok(summary);
    }
}

