package com.SafeCryptoStocks.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.SafeCryptoStocks.model.Portfolio;
import com.SafeCryptoStocks.model.Stock;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    List<Stock> findByPortfolio(Portfolio portfolio);

	//Stock findByPortfolio_IdAndId(Long portfolioId, Long stockId);

	public abstract Stock findByPortfolio_PortfolioIdAndId(Long portfolioId, Long id);



}

