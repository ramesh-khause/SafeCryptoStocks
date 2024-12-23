package com.SafeCryptoStocks.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.SafeCryptoStocks.model.Budget;
import com.SafeCryptoStocks.model.Expense;
import com.SafeCryptoStocks.model.Portfolio;
import com.SafeCryptoStocks.model.Stock;
import com.SafeCryptoStocks.model.User;
import com.SafeCryptoStocks.services.BudgetService;
import com.SafeCryptoStocks.services.ExpenseService;
import com.SafeCryptoStocks.services.PortfolioService;
import com.SafeCryptoStocks.services.StockService;
import com.SafeCryptoStocks.services.EmailService;

@RestController
@RequestMapping("/stock")
public class StockController {

    private Portfolio portfolio;

    @Autowired
    private StockService stockService;

    @Autowired
    private PortfolioService portfolioService;

    @Autowired
    private BudgetService budgetService;

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private EmailService emailService;  // Injecting EmailService to send notifications after a purchase

    
    
    @PostMapping("/{portfolioId}")
    public ResponseEntity<String> addStockToPortfolio(
            @PathVariable Long portfolioId,
            @RequestBody Stock stock,
            @RequestParam double stockPrice,
            @RequestParam Long budgetId) {

        // Add stock to portfolio
        stockService.addStockToPortfolio(portfolioId, stock, stockPrice);

        // Add stock price as an expense to the budget
        try {
            Budget budget = budgetService.getBudgetById(budgetId);

            double totalExpenses = budget.getExpenses() + stockPrice;
            if (totalExpenses > budget.getAmount()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Out of Budget: Total expenses exceed the budget limit of ₹" + budget.getAmount());
            }

            Expense expense = new Expense();
            expense.setAmount(stockPrice);
            expense.setDescription(stock.getStockName());
            expense.setBudget(budget);
            expense.setTimestamp(LocalDateTime.now());
            expenseService.createExpense(expense);

            budget.setExpenses(totalExpenses);
            budgetService.updateBudget(budget);

            // Send email to user after purchase
           // emailService.sendPurchaseEmail(user.getEmail(),user.getFirstname(), List.of(stock));  // Replace with actual user data

            return ResponseEntity.ok("Stock added and budget updated successfully.");
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @GetMapping("/{portfolioId}")
    public ResponseEntity<List<Stock>> getStocksByPortfolio(@PathVariable Long portfolioId) {
        List<Stock> stocks = stockService.getStocksByPortfolio(portfolioId);
        return ResponseEntity.ok(stocks);
    }

    
    @PostMapping("/bulk-insert/{portfolioId}")
    public ResponseEntity<String> addMultipleStocksAndExpenses(
            @PathVariable Long portfolioId,
            @RequestBody List<Stock> stocks) {
        try {
            // Fetch portfolio by ID and validate its existence
            Portfolio portfolio = portfolioService.getPortfolioById(portfolioId);
            if (portfolio == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Portfolio not found.");
            }

            // Fetch the associated user
            User user = portfolio.getUser(); // Assuming Portfolio has a `getUser` method
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not associated with this portfolio.");
            }

            // Fetch and validate the associated budgets
            List<Budget> budgets = portfolio.getBudgets();
            if (budgets.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No budgets associated with the given portfolio.");
            }

            // Use the first budget for this example
            Budget budget = budgets.get(0);

            // Calculate the total cost of stocks
            double totalStockExpense = stocks.stream()
                    .mapToDouble(stock -> stock.getAvgBuyPrice() * stock.getHoldings())
                    .sum();

            // Validate if total expenses exceed the budget limit
            double newTotalExpenses = budget.getExpenses() + totalStockExpense;
            if (newTotalExpenses > budget.getAmount()) {
                String errorMessage = String.format(
                        "Out of Budget: Total expenses (₹%.2f) exceed the budget limit of ₹%.2f.",
                        newTotalExpenses, budget.getAmount()
                );
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
            }

            // Add stocks to portfolio
            List<Stock> createdStocks = stockService.addMultipleStocksToPortfolio(portfolioId, stocks);

            // Create and associate expenses with the budget
            List<Expense> expenses = createdStocks.stream()
                    .map(stock -> {
                        Expense expense = new Expense();
                        expense.setDescription("Stock Purchase: " + stock.getStockName());
                        expense.setAmount(stock.getAvgBuyPrice() * stock.getHoldings());
                        expense.setBudget(budget);
                        return expense;
                    })
                    .toList();

            // Bulk save expenses
            expenseService.addBulkExpenses(expenses);

            // Update the budget
            budget.setExpenses(newTotalExpenses);
            budgetService.updateBudget(budget);

            // Send email to the user
            emailService.sendPurchaseEmail(user.getEmail(), user.getFirstname(),user.getLastname(),createdStocks);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Stocks and expenses added successfully.");
        } catch (RuntimeException ex) {
            // Handle unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred: " + ex.getMessage());
        }
    }

    
    
    /////////////
    
    @PostMapping("/{portfolioId}/sell/{stockId}")
    public ResponseEntity<String> sellStock(
    		   @PathVariable Long portfolioId,
    	        @PathVariable Long stockId,
    	        @RequestBody Map<String, Double> payload) {
    	    double sellQuantity = payload.get("sellQuantity");
    	    double sellPrice = payload.get("sellPrice");
    	    double avgBuyPrice=payload.get("avgBuyPrice");
        try {
            Stock updatedStock = stockService.sellStock(portfolioId, stockId, sellQuantity, sellPrice,avgBuyPrice);
            return ResponseEntity.ok("Stock sold successfully. Remaining holdings: " + updatedStock.getHoldings());
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }
    
    
}
