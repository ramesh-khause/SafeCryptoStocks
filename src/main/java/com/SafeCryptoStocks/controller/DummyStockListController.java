package com.SafeCryptoStocks.controller;

import com.SafeCryptoStocks.model.DummyStocksList;
import com.SafeCryptoStocks.services.DummyStocksService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class DummyStockListController {

    @Autowired
    private DummyStocksService dummyStocksService;

    @GetMapping("/dummy-stock")
    public String getStocks(Model model) {
        List<DummyStocksList> stocks = dummyStocksService.getAllStocks();
        model.addAttribute("stocks", stocks);
        return "dummy-stocks"; 
    }

  
    @PostMapping("/submitSelection")
    public String submitSelection(@RequestBody List<Long> selectedStockIds) {
        // Logic to process selected stock IDs
        // For example, you could update the portfolio or store the selected stocks
        System.out.println("Selected Stocks: " + selectedStockIds);
        return "Stocks selected successfully!";
    }

    

    

}
