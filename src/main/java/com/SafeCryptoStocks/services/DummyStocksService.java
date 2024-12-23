package com.SafeCryptoStocks.services;
import com.SafeCryptoStocks.model.DummyStocksList;
import com.SafeCryptoStocks.model.Stock;
import com.SafeCryptoStocks.repository.DummyStocksRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DummyStocksService {

    @Autowired
    private DummyStocksRepository dummyStocksRepository;

    public List<DummyStocksList> getAllStocks() {
        return dummyStocksRepository.findAll(); // Fetch all stock data
    }

	
	
	public DummyStocksList saveStock(DummyStocksList stock) {
	    return dummyStocksRepository.save(stock);
	}

	public DummyStocksList getStockById(Long id) {
	    return dummyStocksRepository.findById(id).orElse(null);
	}

	
	
}
