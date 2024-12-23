package com.SafeCryptoStocks.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class MarketServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private MarketService marketService;

    @Mock
    private ResponseEntity<String> responseEntity;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    public void testGetTopCryptocurrencies_Forbidden() throws Exception {
        // Simulate a 403 Forbidden response from the API
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.FORBIDDEN));

        // Call the method and expect it to throw a RuntimeException
        assertThrows(RuntimeException.class, () -> marketService.getTopCryptocurrencies());
    }

    @Test
    public void testGetTopCryptocurrencies_MalformedResponse() throws Exception {
        // Simulate a malformed response (not valid JSON)
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>("Malformed Response", HttpStatus.OK));

        // Call the method and expect it to throw a RuntimeException
        assertThrows(RuntimeException.class, () -> marketService.getTopCryptocurrencies());
    }
}
