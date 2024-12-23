package com.SafeCryptoStocks.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DashboardServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private DashboardService dashboardService;

    @Mock
    private ResponseEntity<String> responseEntity;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetCryptocurrencies_Forbidden() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.FORBIDDEN));

        assertThrows(RuntimeException.class, () -> dashboardService.getCryptocurrencies());
    }

    @Test
    public void testGetCryptocurrencies_MalformedResponse() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>("Malformed Response", HttpStatus.OK));

        assertThrows(RuntimeException.class, () -> dashboardService.getCryptocurrencies());
    }

    @Test
    public void testGetTrendingCryptocurrencies_Forbidden() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.FORBIDDEN));

        assertThrows(RuntimeException.class, () -> dashboardService.getTrendingCryptocurrencies());
    }

    @Test
    public void testGetTrendingCryptocurrencies_MalformedResponse() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>("Malformed Response", HttpStatus.OK));

        assertThrows(RuntimeException.class, () -> dashboardService.getTrendingCryptocurrencies());
    }

    @Test
    public void testGetCryptoNews_Forbidden() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.FORBIDDEN));

        assertThrows(RuntimeException.class, () -> dashboardService.getCryptoNews());
    }

    @Test
    public void testGetCryptoNews_MalformedResponse() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>("Malformed Response", HttpStatus.OK));

        assertThrows(RuntimeException.class, () -> dashboardService.getCryptoNews());
    }
}
