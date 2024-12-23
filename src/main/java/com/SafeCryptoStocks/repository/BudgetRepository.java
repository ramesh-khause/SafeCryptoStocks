package com.SafeCryptoStocks.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.SafeCryptoStocks.model.Budget;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

	
	
	@Query("SELECT b FROM Budget b WHERE b.portfolio.user.id = :userId")
	List<Budget> findByUserId(@Param("userId") Long userId);

}
