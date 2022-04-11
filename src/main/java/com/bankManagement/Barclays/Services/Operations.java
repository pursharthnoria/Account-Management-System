package com.bankManagement.Barclays.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bankManagement.Barclays.Users.bankCustomers;
import com.bankManagement.Barclays.repository.BankRepository;

@Service
public class Operations {
    // import the classes from the package users here
    // Write the functions that are required by Prerna here in this class.
	@Autowired
	BankRepository repo;
	public String accountCreation(bankCustomers customer) {
		return repo.accountCreation(customer); 
	}
	
	

}
