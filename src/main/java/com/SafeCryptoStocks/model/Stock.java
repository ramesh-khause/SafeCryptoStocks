package com.SafeCryptoStocks.model;

import jakarta.persistence.*;

@Entity
@Table(name = "stocks")
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String stockName;  // Name of the stock

    @Column(nullable = false)
    private double currentPrice;  // Current market price of the stock

    @Column(nullable = false)
    private double holdings;  // Quantity of stock held

    @Column(nullable = false)
    private double avgBuyPrice;  // Average buying price per stock

    @Transient
    private double initialInvestment;  // Total cost of buying all holdings (calculated dynamically)

    @Transient
    private double profitLoss;  // Calculated profit or loss (calculated dynamically)

    @Transient
    private double currentBalance;  // Current balance based on market price (calculated dynamically)

    @Column(nullable = false)
    private double percentChange24h;  // Percentage change in price in 24 hours

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_Id", referencedColumnName = "portfolio_Id")
    private Portfolio portfolio;

    // Getters and Setters

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

    // Calculates the initial investment
    public double getInitialInvestment() {
        initialInvestment = holdings * avgBuyPrice;
        return initialInvestment;
    }

    public double getProfitLoss() {
        profitLoss = (currentPrice - avgBuyPrice) * holdings;  // Profit or loss calculation
        return profitLoss;
    }

    public double getCurrentBalance() {
        currentBalance = holdings * currentPrice;  // Current balance calculation
        return currentBalance;
    }

    public double getPercentChange24h() {
        return percentChange24h;
    }

    public void setPercentChange24h(double percentChange24h) {
        this.percentChange24h = percentChange24h;
    }
    
    

    public Portfolio getPortfolio() {
       
    	return portfolio;
    }

    public void setPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
    }

    /**
     * Method to update the average buy price after a new purchase.
     * Formula: (Old Total Cost + New Purchase Cost) / (Old Holdings + New Holdings)
     */
    public void updateAvgBuyPrice(double newPurchasePrice, double newHoldings) {
        double oldTotalCost = getTotalCost();  // Calculate old total cost
        double newTotalCost = newPurchasePrice * newHoldings;  // New purchase cost
        this.avgBuyPrice = (oldTotalCost + newTotalCost) / (holdings + newHoldings);  // Updated average price
        this.holdings += newHoldings;  // Update total holdings
    }

    // Helper method to calculate the total cost (used for updating avg buy price)
    private double getTotalCost() {
        return holdings * avgBuyPrice;
    }

	
}
