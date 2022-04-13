package com.bankManagement.Barclays;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class BankingSystemApplication extends SpringBootServletInitializer{
	
	@Override
	   protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
	      return application.sources(BankingSystemApplication.class);
	   }

	public static void main(String[] args) {
		SpringApplication.run(BankingSystemApplication.class, args);
	}

}
