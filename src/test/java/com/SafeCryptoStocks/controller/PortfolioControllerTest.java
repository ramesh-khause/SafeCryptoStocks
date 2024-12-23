package com.SafeCryptoStocks.controller;

import com.SafeCryptoStocks.model.Portfolio;
import com.SafeCryptoStocks.services.PortfolioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PortfolioControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PortfolioService portfolioService;

    @InjectMocks
    private PortfolioController portfolioController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(portfolioController).build();
    }

    @Test
    void testCreatePortfolio() throws Exception {
        Portfolio portfolio = new Portfolio();
        portfolio.setPortfolioName("Tech Portfolio");
        portfolio.setInvestmentAgenda("Growth");

        double budgetAmount = 10000.0;  // Example budget amount

        // Mock the service method to return the portfolio
        when(portfolioService.addPortfolio(anyLong(), any(Portfolio.class), eq(budgetAmount))).thenReturn(portfolio);

        // Perform the test
        mockMvc.perform(post("/portfolios/1?budgetAmount=" + budgetAmount)  // Send budgetAmount in query params
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"portfolioName\":\"Tech Portfolio\",\"investmentAgenda\":\"Growth\"}"))
                .andExpect(status().isCreated())  // Assert response status 201 Created
                .andExpect(jsonPath("$.portfolioName").value("Tech Portfolio"));  // Check the portfolio name

        // Verify the service method is called with the correct parameters
        verify(portfolioService, times(1)).addPortfolio(anyLong(), any(Portfolio.class), eq(budgetAmount));
    }

    @Test
    void testGetPortfolioById() throws Exception {
        Portfolio portfolio = new Portfolio();
        portfolio.setPortfolioId(1L);
        portfolio.setPortfolioName("Tech Portfolio");

        when(portfolioService.getPortfolioById(1L)).thenReturn(portfolio);

        mockMvc.perform(get("/portfolios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.portfolioName").value("Tech Portfolio"));

        verify(portfolioService, times(1)).getPortfolioById(1L);
    }

    @Test
    void testDeletePortfolio() throws Exception {
        doNothing().when(portfolioService).deletePortfolio(1L);

        mockMvc.perform(delete("/portfolios/1"))
                .andExpect(status().isNoContent());

        verify(portfolioService, times(1)).deletePortfolio(1L);
    }


}
