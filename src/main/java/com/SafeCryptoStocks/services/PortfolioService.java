package com.SafeCryptoStocks.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.SafeCryptoStocks.exceptionHandlers.ResourceNotFoundException;
import com.SafeCryptoStocks.model.Budget;
import com.SafeCryptoStocks.model.Expense;
import com.SafeCryptoStocks.model.Portfolio;
import com.SafeCryptoStocks.model.Stock;
import com.SafeCryptoStocks.model.User;
import com.SafeCryptoStocks.repository.PortfolioRepository;
import com.SafeCryptoStocks.repository.StockRepository;
import com.SafeCryptoStocks.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PortfolioService {

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private StockRepository stockRepository;
   
    @Autowired
    UserServicesImp userServicesImp;
    

    @Autowired
    private BudgetService budgetService;
 
    
    public Portfolio addPortfolio(Long userId, Portfolio portfolio, double budgetAmount) {
        // Fetch the user from the database
        User user = userServicesImp.findById(userId);
        if (user == null) {
            throw new RuntimeException("User not found with ID: " + userId);
        }

        // Create and save a new budget for the portfolio
        Budget budget = new Budget();
        budget.setName(portfolio.getPortfolioName());
        budget.setDescription(portfolio.getInvestmentAgenda());
        budget.setAmount(budgetAmount);
        budget.setExpenses(0); // Initial expenses are zero
        budget = budgetService.createBudget(budget); // Save the budget

        // Set the bidirectional relationship
        portfolio.setUser(user); // Set the user to the portfolio
        portfolio.setBudgets(List.of(budget)); // Link the portfolio to the budget (assumes a one-to-many relation)
        budget.setPortfolio(portfolio); // Set the portfolio in the budget (many-to-one relation)

        // Save the portfolio (which will also save the budget if cascading is enabled)
        Portfolio savedPortfolio = portfolioRepository.save(portfolio);

        return savedPortfolio;
    }


    
    
    //get portfolio by portfolio id
   public Portfolio getPortfolioById(Long portfolioId) {
        return portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found"));
    }
  
    
    
    //update portfolio by portfolio id
    public Portfolio updatePortfolio(Long portfolioId, Portfolio portfolioDetails) {
        Portfolio portfolio = getPortfolioById(portfolioId);
        portfolio.setPortfolioName(portfolioDetails.getPortfolioName());
        portfolio.setInvestmentAgenda(portfolioDetails.getInvestmentAgenda());
        portfolio.setUpdatedAt(LocalDateTime.now());
        return portfolioRepository.save(portfolio);
    }

    //deleter portfolio by portfolio id
    public void deletePortfolio(Long portfolioId) {
        Portfolio portfolio = getPortfolioById(portfolioId);
        portfolioRepository.delete(portfolio);
    }

    public Map<String, Double> calculatePortfolioSummary(Long portfolioId) {
        Portfolio portfolio = getPortfolioById(portfolioId);
        List<Stock> stocks = stockRepository.findByPortfolio(portfolio);

        double totalValue = stocks.stream()
                .mapToDouble(stock -> stock.getCurrentPrice() * stock.getHoldings())
                .sum();

        double totalProfitLoss = stocks.stream()
                .mapToDouble(stock -> stock.getProfitLoss())
                .sum();

        return Map.of(
            "totalValue", totalValue,
            "totalProfitLoss", totalProfitLoss
        );
    }


    // Get portfolios by user ID
    public List<Portfolio> getPortfoliosByUser(Long userId) {
        List<Portfolio> portfolios = portfolioRepository.findByUserId(userId);
        portfolios.forEach(portfolio -> System.out.println("portfolio name: " + portfolio.getPortfolioName()));
        portfolios.forEach(portfolio -> System.out.println("Investment Agenda: " + portfolio.getInvestmentAgenda()));
        return portfolios;
    }
    
    
    // Get portfolios by portfolio ID
    public List<Portfolio> getPortfolioByPortfolioId(Long portfolioId) {
        List<Portfolio> portfolios = portfolioRepository.findByPortfolioId(portfolioId);
        portfolios.forEach(portfolio -> System.out.println("portfolio name: " + portfolio.getPortfolioName()));
        portfolios.forEach(portfolio -> System.out.println("Investment Agenda: " + portfolio.getInvestmentAgenda()));
        return portfolios;
    }
    
    
    
}

