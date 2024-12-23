package com.SafeCryptoStocks.repository;


import com.SafeCryptoStocks.model.DummyStocksList;
import com.SafeCryptoStocks.model.Stock;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DummyStocksRepository extends JpaRepository<DummyStocksList, Long> {

	
}
