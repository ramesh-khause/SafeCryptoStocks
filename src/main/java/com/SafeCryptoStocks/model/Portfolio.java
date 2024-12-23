package com.SafeCryptoStocks.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name="portfolios")
public class Portfolio {
	
	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "portfolio_Id")
	    private Long portfolioId;

	   

		@Column(nullable = false)
	    private String portfolioName;

	    @Column(nullable = false)
	    private String investmentAgenda;

	    @Transient
	    private  LocalDateTime createdAt;

	    @Transient
	    private LocalDateTime updatedAt;

	    public Portfolio() {
	        this.createdAt = LocalDateTime.now();
	        this.updatedAt = LocalDateTime.now();
	    }

	    
	    
//
//	    @JsonIgnore
//	    @ManyToOne
//	    @JoinColumn(name = "budget_id")
//	    private Budget budget;

	    
	    @JsonIgnore
	    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true)
	    private List<Budget> budgets; // One Portfolio can have multiple Budgets

	   

	    @JsonIgnore
	    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	    private List<Stock> stocks;

	    
//	    @Column(columnDefinition = "JSON")
//	    private String transaction;
	    

		@ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "userId")
	    @JsonBackReference
		private User user;

	
	    // Getters and Setters
	    
		 public Long getPortfolioId() {
				return portfolioId;
			}

			public List<Budget> getBudgets() {
			return budgets;
		}

		public void setBudgets(List<Budget> budgets) {
			this.budgets = budgets;
		}

			public void setPortfolioId(Long portfolioId) {
				this.portfolioId = portfolioId;
			}
		
		
		
		
//	    public String getTransaction() {
//			return transaction;
//		}
//
//		public void setTransaction(String transaction) {
//			this.transaction = transaction;
//		}

		public User getUser() {
			return user;
		}

		public void setUser(User user) {
			this.user = user;
		}


		public String getPortfolioName() {
			return portfolioName;
		}

		public void setPortfolioName(String portfolioName) {
			this.portfolioName = portfolioName;
		}

		public String getInvestmentAgenda() {
			return investmentAgenda;
		}

		public void setInvestmentAgenda(String investmentAgenda) {
			this.investmentAgenda = investmentAgenda;
		}

		public LocalDateTime getCreatedAt() {
			return createdAt;
		}

		public void setCreatedAt(LocalDateTime createdAt) {
			this.createdAt = createdAt;
		}

		public LocalDateTime getUpdatedAt() {
			return updatedAt;
		}

		public void setUpdatedAt(LocalDateTime updatedAt) {
			this.updatedAt = updatedAt;
		}


		public List<Stock> getStocks() {
			return stocks;
		}

		public void setStocks(List<Stock> stocks) {
			this.stocks = stocks;
		}

	   

}


