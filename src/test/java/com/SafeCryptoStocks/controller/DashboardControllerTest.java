package com.SafeCryptoStocks.controller;

import com.SafeCryptoStocks.services.DashboardService;
import com.SafeCryptoStocks.services.MarketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import com.fasterxml.jackson.databind.JsonNode;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class DashboardControllerTest {

    @Mock
    private DashboardService dashboardService;
    
    @Mock
    private MarketService marketService;
    
    @Mock
    private Model model;
    
    @InjectMocks
    private DashboardController dashboardController;

    private MockHttpServletRequest request;
    private MockHttpSession session;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        request = new MockHttpServletRequest();
        session = new MockHttpSession();
        request.setSession(session);
    }

    @Test
    public void testShowMarket_RedirectsToHomeIfNoSession() {
        String result = dashboardController.showMarket(model, request);
        assertEquals("redirect:/home", result);
    }

    @Test
    public void testShowMarket_RedirectsToHomeIfNoUserIdInSession() {
        session.setAttribute("userId", null);
        String result = dashboardController.showMarket(model, request);
        assertEquals("redirect:/home", result);
    }

    @Test
    public void testShowMarket_Success() {
        session.setAttribute("userId", 1L);
        session.setAttribute("userName", "John Doe");
        session.setAttribute("profileUrl", "http://example.com/profile.jpg");

        JsonNode cryptoData = mock(JsonNode.class);
        when(marketService.getTopCryptocurrencies()).thenReturn(cryptoData);
        
        String result = dashboardController.showMarket(model, request);

        assertEquals("market", result);
        verify(model).addAttribute("cryptoData", cryptoData);
        verify(model).addAttribute("userName", "John Doe");
        verify(model).addAttribute("profileUrl", "http://example.com/profile.jpg");
    }

    // Similar test cases can be written for other methods
    
    @Test
    public void testShowBudget_RedirectsToHomeIfNoSession() {
        String result = dashboardController.showBudget(model, request);
        assertEquals("redirect:/home", result);
    }

    @Test
    public void testShowBudget_RedirectsToHomeIfNoUserIdInSession() {
        session.setAttribute("userId", null);
        String result = dashboardController.showBudget(model, request);
        assertEquals("redirect:/home", result);
    }

    @Test
    public void testShowBudget_Success() {
        session.setAttribute("userId", 1L);
        session.setAttribute("userName", "John Doe");
        session.setAttribute("firstName", "John");
        session.setAttribute("lastName", "Doe");
        session.setAttribute("profileUrl", "http://example.com/profile.jpg");

        String result = dashboardController.showBudget(model, request);

        assertEquals("budget", result);
        verify(model).addAttribute("userName", "John Doe");
        verify(model).addAttribute("firstName", "John");
        verify(model).addAttribute("lastName", "Doe");
        verify(model).addAttribute("profileUrl", "http://example.com/profile.jpg");
    }

    
    @Test
    public void testLearn_RedirectsToHomeIfNoSession() {
        String result = dashboardController.learn(model, request);
        assertEquals("redirect:/home", result);
    }

    @Test
    public void testLearn_RedirectsToHomeIfNoUserIdInSession() {
        session.setAttribute("userId", null);
        String result = dashboardController.learn(model, request);
        assertEquals("redirect:/home", result);
    }

    @Test
    public void testLearn_Success() {
        session.setAttribute("userId", 1L);
        session.setAttribute("userName", "John Doe");
        session.setAttribute("profileUrl", "http://example.com/profile.jpg");

        String result = dashboardController.learn(model, request);

        assertEquals("learn", result);
        verify(model).addAttribute("userName", "John Doe");
        verify(model).addAttribute("profileUrl", "http://example.com/profile.jpg");
    }

    
    @Test
    public void testDashboard_RedirectsToHomeIfNoSession() {
        String result = dashboardController.dashboard(request, model);
        assertEquals("redirect:/home", result);
    }

    @Test
    public void testDashboard_RedirectsToHomeIfNoUserIdInSession() {
        session.setAttribute("userId", null);
        String result = dashboardController.dashboard(request, model);
        assertEquals("redirect:/home", result);
    }

    @Test
    public void testDashboard_Success() {
        session.setAttribute("userId", 1L);
        session.setAttribute("userName", "John Doe");
        session.setAttribute("firstName", "John");
        session.setAttribute("lastName", "Doe");
        session.setAttribute("profileUrl", "http://example.com/profile.jpg");

        JsonNode cryptoData = mock(JsonNode.class);
        JsonNode trendingData = mock(JsonNode.class);
        JsonNode newsData = mock(JsonNode.class);

        when(dashboardService.getCryptocurrencies()).thenReturn(cryptoData);
        when(dashboardService.getTrendingCryptocurrencies()).thenReturn(trendingData);
        when(dashboardService.getCryptoNews()).thenReturn(newsData);

        String result = dashboardController.dashboard(request, model);

        assertEquals("dashboard", result);
        verify(model).addAttribute("cryptoData", cryptoData);
        verify(model).addAttribute("trendingData", trendingData);
        verify(model).addAttribute("newsData", newsData);
        verify(model).addAttribute("userName", "John Doe");
        verify(model).addAttribute("firstName", "John");
        verify(model).addAttribute("lastName", "Doe");
        verify(model).addAttribute("profileUrl", "http://example.com/profile.jpg");
    }

    
    @Test
    public void testPortfolio_RedirectsToHomeIfNoSession() {
        String result = dashboardController.portfolio(request, model);
        assertEquals("redirect:/home", result);
    }

    @Test
    public void testPortfolio_RedirectsToHomeIfNoUserIdInSession() {
        session.setAttribute("userId", null);
        String result = dashboardController.portfolio(request, model);
        assertEquals("redirect:/home", result);
    }

    @Test
    public void testPortfolio_Success() {
        session.setAttribute("userId", 1L);
        session.setAttribute("userName", "John Doe");
        session.setAttribute("profileUrl", "http://example.com/profile.jpg");

        String result = dashboardController.portfolio(request, model);

        assertEquals("portfolio", result);
        verify(model).addAttribute("userId", 1L);
        verify(model).addAttribute("userName", "John Doe");
        verify(model).addAttribute("profileUrl", "http://example.com/profile.jpg");
    }
    
    @Test
    public void testShowDashboard() {
        Long userId = 1L;
        String result = dashboardController.showDashboard(userId, model);

        assertEquals("dashboard", result);
        verify(model).addAttribute("userId", userId);
    }


}
