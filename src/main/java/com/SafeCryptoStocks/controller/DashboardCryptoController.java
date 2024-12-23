package com.SafeCryptoStocks.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.SafeCryptoStocks.services.DashboardService;
import com.fasterxml.jackson.databind.JsonNode;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class DashboardCryptoController {
	
	
	@Autowired
	private DashboardService dashboardService;
	
 
	 
	 @RestController
	    public static class DashboardRestController {
	        @Autowired
	        private DashboardService dashboardService;

	        @GetMapping("/dash/cryptocurrency")
	        public JsonNode getCryptoData() {
	            return dashboardService.getCryptocurrencies();
	        }
	        
	        
	        @GetMapping("/dash/trending-cryptocurrency")
	        public JsonNode getTrendingCryptoData() {
	            return dashboardService.getTrendingCryptocurrencies(); // Endpoint for trending cryptocurrencies
	        }
	        
	        @GetMapping("/dash/crypto-news")
	        public JsonNode getCryptoNewsData() {
	            return dashboardService.getCryptoNews(); // Endpoint for cryptocurrency news
	        }
	    }
}


