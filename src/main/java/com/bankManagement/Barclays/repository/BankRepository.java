package com.bankManagement.Barclays.repository;

import java.text.SimpleDateFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.bankManagement.Barclays.Users.bankCustomers;

import org.springframework.jdbc.core.JdbcTemplate;

@Repository
public class BankRepository {

	@Autowired
	JdbcTemplate jdbcTemplate;

	public String accountCreation(bankCustomers customer) {
		String password="abc";
		customer.setCustomerId("123456");
		try{
			 SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
			 java.util.Date utilDate = format.parse(customer.getDob());
		        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());

			jdbcTemplate.update("insert into Customer values (?,?,?,?,?,?,?,?,?,?)",
					new Object[] {customer.getCustomerId(),customer.getPostalAddress(),customer.getAdharNumber(),customer.getPanCard(),customer.getPhoneNumber(),sqlDate,customer.getEmail(),password,customer.getCity(),customer.getName()});
			return customer.getCustomerId();
			
		}catch(Exception e) {
			System.out.println(e.getMessage());
			return "False";
			
		}
		
		
	}

}
