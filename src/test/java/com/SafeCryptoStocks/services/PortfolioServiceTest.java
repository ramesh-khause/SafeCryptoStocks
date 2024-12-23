package com.SafeCryptoStocks.services;

import com.SafeCryptoStocks.model.Budget;
import com.SafeCryptoStocks.model.Portfolio;
import com.SafeCryptoStocks.model.User;
import com.SafeCryptoStocks.repository.PortfolioRepository;
import com.SafeCryptoStocks.repository.StockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PortfolioServiceTest {

    @Mock
    private PortfolioRepository portfolioRepository;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private UserServicesImp userServicesImp;
    
    @Mock
    private BudgetService budgetService;

    @InjectMocks
    private PortfolioService portfolioService;

    private Portfolio portfolio;
    private User user;

    @BeforeEach
    public void setup() {
        portfolio = new Portfolio();
        portfolio.setPortfolioName("My Portfolio");
        portfolio.setInvestmentAgenda("Long-term investment");

        user = new User();
        user.setId(1L);
        user.setPortfolios(new ArrayList<>()); // Initialize the portfolio list
    }

    @Test
    public void testAddPortfolio() {
        // Sample data
        double budgetAmount = 10000.0;
        
        // Mocking dependent services
        when(userServicesImp.findById(any(Long.class))).thenReturn(user);
        when(budgetService.createBudget(any(Budget.class))).thenReturn(new Budget());  // Mocking budget creation
        when(portfolioRepository.save(any(Portfolio.class))).thenReturn(portfolio);

        // Calling the service method
        Portfolio result = portfolioService.addPortfolio(1L, portfolio, budgetAmount);

        // Validating the results
        assertNotNull(result);
        assertEquals("My Portfolio", result.getPortfolioName());

        // Verifying interactions
        verify(userServicesImp, times(1)).findById(any(Long.class));
        verify(budgetService, times(1)).createBudget(any(Budget.class));  // Ensure budget creation was called
        verify(portfolioRepository, times(1)).save(any(Portfolio.class));
    }

    @Test
    public void testGetPortfolioById() {
        when(portfolioRepository.findById(any(Long.class))).thenReturn(java.util.Optional.of(portfolio));

        Portfolio result = portfolioService.getPortfolioById(1L);

        assertNotNull(result);
        assertEquals("My Portfolio", result.getPortfolioName());
    }

    @Test
    public void testUpdatePortfolio() {
        when(portfolioRepository.findById(any(Long.class))).thenReturn(java.util.Optional.of(portfolio));
        when(portfolioRepository.save(any(Portfolio.class))).thenReturn(portfolio);

        portfolio.setInvestmentAgenda("Updated agenda");
        Portfolio result = portfolioService.updatePortfolio(1L, portfolio);

        assertNotNull(result);
        assertEquals("Updated agenda", result.getInvestmentAgenda());
        verify(portfolioRepository, times(1)).save(any(Portfolio.class));
    }

    @Test
    public void testDeletePortfolio() {
        when(portfolioRepository.findById(any(Long.class))).thenReturn(java.util.Optional.of(portfolio));

        portfolioService.deletePortfolio(1L);

        verify(portfolioRepository, times(1)).delete(any(Portfolio.class));
    }

    
}
