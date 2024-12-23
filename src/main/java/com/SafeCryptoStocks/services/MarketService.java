package com.SafeCryptoStocks.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MarketService {

    @Value("${coinmarketcap.api.url}")
    private String apiUrl;

    @Value("${coinmarketcap.api.key}")
    private String apiKey;

    public JsonNode getTopCryptocurrencies() {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-CMC_PRO_API_KEY", apiKey);
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            // Log the API URL and headers for debugging
            System.out.println("Request URL: " + apiUrl);
            System.out.println("Request Headers: " + headers);

            // Sending the GET request to CoinMarketCap API
            ResponseEntity<String> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            // Log response status and body for debugging
            System.out.println("Response Status Code: " + response.getStatusCode());
            System.out.println("Response Headers: " + response.getHeaders());
            System.out.println("Response Body: " + response.getBody());

            // Handle potential 403 error explicitly
            if (response.getStatusCode() == HttpStatus.FORBIDDEN) {
                System.err.println("403 Forbidden Error: Check API key and permissions.");
                throw new RuntimeException("403 Forbidden: Access denied by CoinMarketCap API. Check API key and permissions.");
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response.getBody());
            JsonNode data = rootNode.get("data"); // Fetch the "data" node from the response

            // Create a JsonNode to store the top 20 cryptocurrencies
            JsonNode topCryptos = mapper.createArrayNode();

            // Loop through the first 20 cryptocurrencies and extract the relevant data
            for (int i = 0; i < 100; i++) {
                JsonNode crypto = data.get(i);
                ((ArrayNode) topCryptos).add(mapper.createObjectNode()
                        .put("name", crypto.get("name").asText()) // Crypto name
                        .put("price", crypto.get("quote").get("USD").get("price").asDouble()) // Price in USD
                        .put("symbol", crypto.get("symbol").asText()) // Symbol (short name) 
                        .put("percent_change_24h", crypto.get("quote").get("USD").get("percent_change_24h").asDouble()) // 24h change in percentage
                        .put("percent_change_7d", crypto.get("quote").get("USD").get("percent_change_7d").asDouble()) // 7d change in percentage
                        .put("market_cap", crypto.get("quote").get("USD").get("market_cap").asDouble()) // Market cap
                        .put("total_volume", crypto.get("quote").get("USD").get("volume_24h").asDouble()) // 24h volume
                        .put("circulating_supply", crypto.get("circulating_supply").asDouble()) // Circulating supply
                        .put("icon", "https://s2.coinmarketcap.com/static/img/coins/64x64/" + crypto.get("id").asText() + ".png") // Icon URL based on CoinMarketCap ID
                );
            }

            // Return the top 20 cryptocurrencies data
            return topCryptos;

        } catch (Exception e) {
            e.printStackTrace(); // Print stack trace for better debugging
            throw new RuntimeException("Failed to fetch cryptocurrency data from CoinMarketCap API", e);
        }
    }
}
