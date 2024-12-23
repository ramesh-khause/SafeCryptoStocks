package com.SafeCryptoStocks.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class DummyStocksList {
	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;
	  
	    private String stockName;  // Name of the stock
	    private double currentPrice;  // 
	    private double holdings;  // Quantity of stock held
	    private double avgBuyPrice;  // Average buying price per stock
	    private double percentChange24h;  
		
	    
	    public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public String getStockName() {
			return stockName;
		}
		public void setStockName(String stockName) {
			this.stockName = stockName;
		}
		public double getCurrentPrice() {
			return currentPrice;
		}
		public void setCurrentPrice(double currentPrice) {
			this.currentPrice = currentPrice;
		}
		public double getHoldings() {
			return holdings;
		}
		public void setHoldings(double holdings) {
			this.holdings = holdings;
		}
		public double getAvgBuyPrice() {
			return avgBuyPrice;
		}
		public void setAvgBuyPrice(double avgBuyPrice) {
			this.avgBuyPrice = avgBuyPrice;
		}
		public double getPercentChange24h() {
			return percentChange24h;
		}
		public void setPercentChange24h(double percentChange24h) {
			this.percentChange24h = percentChange24h;
		}
		
	   
	 // Getters and setters
		
	    
	}
