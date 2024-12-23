package com.SafeCryptoStocks.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

@Service
public class DashboardService {

    @Value("${coinmarketcap.api.url}")
    private String apiUrl;

    @Value("${coinmarketcap.api.key}")
    private String apiKey;

    public JsonNode getCryptocurrencies() {
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
                        .put("percent_change_24h", crypto.get("quote").get("USD").get("percent_change_24h").asDouble()) // 24h change in percentage
                        .put("percent_change_7d", crypto.get("quote").get("USD").get("percent_change_7d").asDouble()) // 7d change in percentage
                        .put("symbol", crypto.get("symbol").asText()) // Symbol (short name) 
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

    // New method to fetch trending cryptocurrencies from CoinGecko
    @Value("${coingeko.api.url}")
    private String apiGekoUrl;

    public JsonNode getTrendingCryptocurrencies() {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            // Log the API URL and headers for debugging
            System.out.println("Request URL: " + apiGekoUrl);
            System.out.println("Request Headers: " + headers);

            // Sending the GET request to CoinGecko API
            ResponseEntity<String> response = restTemplate.exchange(
                    apiGekoUrl,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            // Log response status and body for debugging
            System.out.println("Response Status Code: " + response.getStatusCode());
            System.out.println("Response Body: " + response.getBody());

            // Handle potential 403 error explicitly (not needed here, but can be helpful)
            if (response.getStatusCode() == HttpStatus.FORBIDDEN) {
                System.err.println("403 Forbidden Error: Check API key and permissions.");
                throw new RuntimeException("403 Forbidden: Access denied by CoinGecko API. Check API key and permissions.");
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response.getBody());
            JsonNode coins = rootNode.get("coins"); // Fetch the "coins" node from the response

            // Create a JsonNode to store the trending cryptocurrencies
            JsonNode trendingCryptos = mapper.createArrayNode();

            // Loop through the trending coins and extract the relevant data
            for (JsonNode coinNode : coins) {
                JsonNode coin = coinNode.get("item");
                ((ArrayNode) trendingCryptos).add(mapper.createObjectNode()
                        .put("name", coin.get("name").asText()) // Crypto name
                        .put("symbol", coin.get("symbol").asText()) // Crypto symbol (short name)
                        .put("market_cap_rank", coin.get("market_cap_rank").asInt()) // Market rank
                        .put("logo", coin.get("large").asText()) // Logo URL
                );
            }

            // Return the trending cryptocurrencies data
            return trendingCryptos;

        } catch (Exception e) {
            e.printStackTrace(); // Print stack trace for better debugging
            throw new RuntimeException("Failed to fetch cryptocurrency data from CoinGecko API", e);
        }
    }

 // New method to fetch cryptocurrency-related news using NewsAPI

    @Value("${news.api.url}")
    private String newsApiUrl;

    @Value("${news.api.key}")
    private String newsApiKey;

    public JsonNode getCryptoNews() {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Api-Key", newsApiKey);  // Add the News API key header
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            // Create the full URL with query parameters
            String fullUrl = newsApiUrl + "?q=cryptocurrency&apiKey=" + newsApiKey;

            // Log the API URL and headers for debugging
            System.out.println("Request URL: " + fullUrl);
            System.out.println("Request Headers: " + headers);

            // Sending the GET request to News API
            ResponseEntity<String> response = restTemplate.exchange(
                    fullUrl,  // Use the full URL with query parameters
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            // Log response status and body for debugging
            System.out.println("Response Status Code: " + response.getStatusCode());
            System.out.println("Response Body: " + response.getBody());

            // Handle potential 403 error explicitly
            if (response.getStatusCode() == HttpStatus.FORBIDDEN) {
                System.err.println("403 Forbidden Error: Check API key and permissions.");
                throw new RuntimeException("403 Forbidden: Access denied by NewsAPI. Check API key and permissions.");
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response.getBody());
            JsonNode articles = rootNode.get("articles"); // Fetch the "articles" node from the response

            // Create a JsonNode to store the news articles
            JsonNode cryptoNews = mapper.createArrayNode();

            // Loop through the articles and extract the relevant data
            int count = 0;
            for (JsonNode articleNode : articles) {
                if (count >=7) break; // Limit to 5 articles
                
               

                ((ArrayNode) cryptoNews).add(mapper.createObjectNode()
                        .put("title", articleNode.has("title") ? articleNode.get("title").asText() : "No title available")
                        .put("description", articleNode.has("description") ? articleNode.get("description").asText() : "No description available")
                        .put("url", articleNode.has("url") ? articleNode.get("url").asText() : "")
                        .put("publishedAt", articleNode.has("publishedAt") ? articleNode.get("publishedAt").asText() : "")
                        .put("source", articleNode.has("source") && articleNode.get("source").has("name") ? 
                                articleNode.get("source").get("name").asText() : "Unknown source")
                );
                count++;
            }


            // Return the cryptocurrency news articles data, limited to 5 articles
            return cryptoNews;

        } catch (Exception e) {
            e.printStackTrace(); // Print stack trace for better debugging
            throw new RuntimeException("Failed to fetch cryptocurrency news from NewsAPI: " + e.getMessage(), e);
        }
    }

}
