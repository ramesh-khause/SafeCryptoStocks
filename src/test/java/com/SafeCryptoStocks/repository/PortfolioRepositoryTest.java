package com.SafeCryptoStocks.repository;

import com.SafeCryptoStocks.model.Portfolio;
import com.SafeCryptoStocks.model.User;
import com.SafeCryptoStocks.services.PortfolioService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PortfolioRepositoryTest {

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PortfolioService portfolioService;
    
    
    
    @Test
    @Transactional
    void testFindByUserId() {
        // Create a user
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("Password@123");
        user.setEmail("testUser@example.com");
        user.setFirstname("Test");
        user.setLastname("User");
        user.setAddress("456 User Street");
        User savedUser = userRepository.save(user);

        // Create and save multiple portfolios for the user
        Portfolio portfolio1 = new Portfolio();
        portfolio1.setPortfolioName("Portfolio 1");
        portfolio1.setInvestmentAgenda("Agenda 1");
        portfolio1.setUser(savedUser);

        Portfolio portfolio2 = new Portfolio();
        portfolio2.setPortfolioName("Portfolio 2");
        portfolio2.setInvestmentAgenda("Agenda 2");
        portfolio2.setUser(savedUser);

        portfolioRepository.save(portfolio1);
        portfolioRepository.save(portfolio2);

        // Find portfolios by userId
        List<Portfolio> portfolios = portfolioRepository.findByUserId(savedUser.getId());

        // Assert the portfolios are retrieved correctly
        assertThat(portfolios).hasSize(2);
        assertThat(portfolios.get(0).getPortfolioName()).isEqualTo("Portfolio 1");
        assertThat(portfolios.get(1).getPortfolioName()).isEqualTo("Portfolio 2");
    }

    @Test
    @Transactional
    void testFindByPortfolioId() {
        // Create and save a portfolio
        Portfolio portfolio = new Portfolio();
        portfolio.setPortfolioName("Crypto Investments");
        portfolio.setInvestmentAgenda("Focus on cryptocurrency");
        Portfolio savedPortfolio = portfolioRepository.save(portfolio);

        // Retrieve the portfolio by portfolioId
        List<Portfolio> retrievedPortfolios = portfolioRepository.findByPortfolioId(savedPortfolio.getPortfolioId());

        // Assert the portfolio is retrieved correctly
        assertThat(retrievedPortfolios).isNotEmpty();
        assertThat(retrievedPortfolios.get(0).getPortfolioName()).isEqualTo("Crypto Investments");
    }

    @Test
    @Transactional
    void testDeletePortfolio() {
        // Create and save a portfolio
        Portfolio portfolio = new Portfolio();
        portfolio.setPortfolioName("Delete Portfolio");
        portfolio.setInvestmentAgenda("To be deleted");
        Portfolio savedPortfolio = portfolioRepository.save(portfolio);

        // Delete the portfolio
        portfolioRepository.delete(savedPortfolio);

        // Check that the portfolio no longer exists
        Optional<Portfolio> deletedPortfolio = portfolioRepository.findById(savedPortfolio.getPortfolioId());
        assertThat(deletedPortfolio).isEmpty();
    }
}
