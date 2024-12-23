package com.SafeCryptoStocks.controller;

import com.fasterxml.jackson.databind.JsonNode;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import com.SafeCryptoStocks.services.MarketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class MarketController {

    @Autowired
    private MarketService marketService;

//    @GetMapping("/market-page")
//    public String showMarketPage(Model model,HttpServletRequest request) {
//        JsonNode cryptoData = marketService.getTopCryptocurrencies();
//        model.addAttribute("cryptoData", cryptoData); // Pass all relevant data
//        HttpSession session = request.getSession(false);
//        if (session == null) {
//            return "redirect:/login";
//        }
//        String userName = (String) session.getAttribute("userName");
//        model.addAttribute("userName", userName);
//        return "market"; // Render the market.html page
//    }

    @RestController
    public static class MarketRestController {
        @Autowired
        private MarketService marketService;

        @GetMapping("/cryptocurrency")
        public JsonNode getCryptoData() {
            return marketService.getTopCryptocurrencies();
        }
    }
}
