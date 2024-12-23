package com.SafeCryptoStocks.controller;

import com.SafeCryptoStocks.services.MarketService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MarketControllerTest {

    @Mock
    private MarketService marketService;

    @InjectMocks
    private MarketController.MarketRestController marketRestController;

    private JsonNode mockResponse;

    @BeforeEach
    public void setup() throws Exception {
        // Initialize the mock response
        ObjectMapper mapper = new ObjectMapper();
        String jsonResponse = "[ { \"name\": \"Bitcoin\", \"price\": 50000.0, \"symbol\": \"BTC\", \"percent_change_24h\": 2.0, \"percent_change_7d\": 5.0, \"market_cap\": 1000000000.0, \"total_volume\": 100000.0, \"circulating_supply\": 18000000.0, \"icon\": \"https://s2.coinmarketcap.com/static/img/coins/64x64/1.png\" } ]";
        mockResponse = mapper.readTree(jsonResponse);
    }

    @Test
    public void testGetCryptoData_Success() {
        // Mock the service call
        when(marketService.getTopCryptocurrencies()).thenReturn(mockResponse);

        // Call the controller method
        JsonNode response = marketRestController.getCryptoData();

        // Verify the result
        assertNotNull(response);
        assertTrue(response.isArray());
        assertEquals(1, response.size());
        assertEquals("Bitcoin", response.get(0).get("name").asText());
        assertEquals(50000.0, response.get(0).get("price").asDouble());

        // Verify the service method was called
        verify(marketService, times(1)).getTopCryptocurrencies();
    }

    @Test
    public void testGetCryptoData_Exception() {
        // Mock the service call to throw an exception
        when(marketService.getTopCryptocurrencies()).thenThrow(new RuntimeException("API call failed"));

        // Call the controller method and expect an exception
        Exception exception = assertThrows(RuntimeException.class, () -> {
            marketRestController.getCryptoData();
        });

        // Verify the exception message
        assertEquals("API call failed", exception.getMessage());

        // Verify the service method was called
        verify(marketService, times(1)).getTopCryptocurrencies();
    }
}
