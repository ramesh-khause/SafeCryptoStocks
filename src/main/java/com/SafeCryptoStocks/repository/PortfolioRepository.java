package com.SafeCryptoStocks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.SafeCryptoStocks.model.Portfolio;

import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

	List<Portfolio> findByUserId(Long userId);

	List<Portfolio> findByPortfolioId(Long portfolioId);

	
}
