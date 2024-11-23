package com.SafeCryptoStocks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


import com.SafeCryptoStocks.services.EmailService;


@SpringBootApplication
public class SafeCryptoStocksApplication {

//	@Autowired
//	private EmailService senderService;
	
	public static void main(String[] args) {
		SpringApplication.run(SafeCryptoStocksApplication.class, args);
	}
 
}
