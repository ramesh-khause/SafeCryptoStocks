package com.SafeCryptoStocks.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.SafeCryptoStocks.exceptionHandlers.ResourceNotFoundException;
import com.SafeCryptoStocks.model.Budget;
import com.SafeCryptoStocks.model.Expense;
import com.SafeCryptoStocks.model.Portfolio;
import com.SafeCryptoStocks.model.Stock;
import com.SafeCryptoStocks.repository.ExpenseRepository;
import com.SafeCryptoStocks.repository.PortfolioRepository;
import com.SafeCryptoStocks.repository.StockRepository;

import jakarta.transaction.Transactional;

@Service
public class StockService {

	 private static final Logger logger = LoggerFactory.getLogger(StockService.class);
	
    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private StockRepository stockRepository;
    
     @Autowired
     private ExpenseService expenseService;
    
    @Autowired
    private BudgetService budgetService;
    
    @Autowired
    private PortfolioService portfolioService;

    
    @Autowired
    private ExpenseRepository expenseRepository;
    
    @Autowired
    private EmailService emailService;
    
    @Transactional
    public void addStockToPortfolio(Long portfolioId, Stock stock, double stockPrice) {
        // Fetch the portfolio from the database
        Portfolio portfolio = portfolioService.getPortfolioById(portfolioId);

        // Ensure portfolio exists
        if (portfolio == null) {
            throw new RuntimeException("Portfolio not found.");
        }

        // Get the portfolio's budgets (one-to-many relation)
        List<Budget> portfolioBudgets = portfolio.getBudgets();
        if (portfolioBudgets.isEmpty()) {
            throw new RuntimeException("No budget associated with the portfolio.");
        }

        // Fetch the first budget (you can modify this to select a specific budget)
        Budget portfolioBudget = portfolioBudgets.get(0);  // Adjust if logic requires selecting a specific budget

        // Create an expense entry for the stock purchase
        Expense stockExpense = new Expense();
        stockExpense.setDescription("Stock Purchase: " + stock.getStockName());  // Adding description with stock name
        stockExpense.setAmount(stockPrice);  // The cost of the stock
        stockExpense.setTimestamp(LocalDateTime.now());  // Record the time of the expense
        stockExpense.setBudget(portfolioBudget);  // Link the expense to the selected budget

        // Save the expense associated with the budget
        budgetService.addExpense(portfolioBudget.getId(), stockExpense);

        // Save the stock under the portfolio
        stock.setPortfolio(portfolio);  // Link stock to the portfolio
        stockRepository.save(stock);  // Persist the stock

        System.out.println("Expense added to Budget with ID: " + portfolioBudget.getId());
    }

    public List<Stock> getStocksByPortfolio(Long portfolioId) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found"));
          return stockRepository.findByPortfolio(portfolio);
     
    }
    
     

	public Stock buyStock(Stock stock) {
		// TODO Auto-generated method stub
		// stock.setCurrentBalance(stock.getAvgBuyPrice().multiply(new BigDecimal(stock.getHoldings())));
	        return stockRepository.save(stock);
	}

//	 @Transactional
//	    public List<Stock> addMultipleStocksToPortfolio(Long portfolioId, List<Stock> stocks) {
//	        return stocks.stream()
//	                     .map(stock -> {
//	                         stock.setPortfolio(portfolioRepository.findById(portfolioId)
//	                             .orElseThrow(() -> new RuntimeException("Portfolio not found")));
//	                         logger.info("Saving stock: {}", stock.getStockName());
//	                         return stockRepository.save(stock);
//	                     })
//	                     .collect(Collectors.toList());
//	    }
	 
	 public List<Stock> addMultipleStocksToPortfolio(Long portfolioId, List<Stock> stocks) {
	        Portfolio portfolio = portfolioRepository.findById(portfolioId)
	                .orElseThrow(() -> new RuntimeException("Portfolio not found"));

	        stocks.forEach(stock -> stock.setPortfolio(portfolio));

	        return stockRepository.saveAll(stocks);
	    }
	 
	 
	 @Transactional
	 public void addMultipleStocksWithExpenses(Long portfolioId, List<Stock> stocks, Budget budget) {
	     Portfolio portfolio = portfolioRepository.findById(portfolioId)
	             .orElseThrow(() -> new RuntimeException("Portfolio not found"));

	     double currentExpenses = budget.getExpenses();

	     Logger log = null;
		for (Stock stock : stocks) {
	         double stockPrice = stock.getAvgBuyPrice();
	         log.debug("Processing stock: {} with price: {}", stock.getStockName(), stockPrice);

	         try {
	             // Create and save expense
	             Expense stockExpense = new Expense();
	             stockExpense.setDescription(stock.getStockName());
	             stockExpense.setAmount(stockPrice);
	             stockExpense.setTimestamp(LocalDateTime.now());
	             stockExpense.setBudget(budget);

	             log.debug("Saving expense: {}", stockExpense);
	             expenseRepository.save(stockExpense);

	             // Update and persist budget
	             currentExpenses += stockPrice;
	             budget.setExpenses(currentExpenses);
	             budgetService.updateBudget(budget);
	             log.debug("Updated budget expenses: {}", budget.getExpenses());

	             // Assign stock to portfolio
	             stock.setPortfolio(portfolio);
	         } catch (Exception e) {
	             log.error("Error saving expense for stock: {}", stock.getStockName(), e);
	             throw e; // Let @Transactional handle rollback
	         }
	     }

	     stockRepository.saveAll(stocks);
	     log.debug("All stocks saved successfully.");
	 }

	 ////////////////////
	 
	 
	 public Stock sellStock(Long portfolioId, Long stockId, double sellQuantity, double sellPrice,double avgBuyPrice) {
	        // Fetch portfolio
	        Portfolio portfolio = portfolioService.getPortfolioById(portfolioId);
	        if (portfolio == null) {
	            throw new RuntimeException("Portfolio not found.");
	        }

	        // Fetch stock by portfolioId and stockId
	        Stock stock = stockRepository.findByPortfolio_PortfolioIdAndId(portfolioId, stockId);
	        if (stock == null) {
	            throw new RuntimeException("Stock not found in the specified portfolio.");
	        }

	        // Check if there are enough holdings to sell
	        if (stock.getHoldings() < sellQuantity) {
	            throw new RuntimeException("Not enough holdings to sell.");
	        }

	        // Update stock holdings
	        stock.setHoldings(stock.getHoldings() - sellQuantity);

	        // If holdings become zero, you may delete the stock or just keep it
	        if (stock.getHoldings() == 0) {
	            stockRepository.delete(stock); // Optional: Delete stock if holdings are 0
	        } else {
	            stockRepository.save(stock);
	        }

	        // Calculate revenue
	        double revenue = sellQuantity * avgBuyPrice;

	        // Update budget
	        Budget budget = portfolio.getBudgets().get(0); // Assuming one budget per portfolio
	        budget.setExpenses(budget.getExpenses() - revenue);
	        budgetService.updateBudget(budget);

	        // Record the sale as an expense (negative for revenue)
	        Expense expense = new Expense();
	        expense.setDescription("Sold Stock: " + stock.getStockName());
	        expense.setAmount(-revenue); // Negative for revenue
	        expense.setTimestamp(LocalDateTime.now());
	        expense.setBudget(budget);
	        expenseService.createExpense(expense);

	        

        emailService.sendSellNotification(portfolio.getUser().getEmail(), 
                                  portfolio.getUser().getFirstname(), portfolio.getUser().getLastname(),  
                                  stock.getStockName(), 
                                  sellQuantity, 
                                  sellPrice);
	        
	        
	        return stock;
	    }
	
}
