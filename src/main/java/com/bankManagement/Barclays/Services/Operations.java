package com.bankManagement.Barclays.Services;

import java.lang.Math;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bankManagement.Barclays.Users.BankAccount;
import com.bankManagement.Barclays.Users.BankCustomers;
import com.bankManagement.Barclays.Users.Login;
import com.bankManagement.Barclays.Users.Transaction;
import com.bankManagement.Barclays.repository.BankRepository;

@Service
public class Operations {
	// import the classes from the package users here
	// Write the functions that are required by Prerna here in this class.

	@Autowired
	BankRepository repo;

	public BankCustomers login(Login user) {
		return repo.login(user);
	}

	public String accountCreation(BankCustomers customer) {
		return repo.accountCreation(customer, generateCustomerId(), generatePassword());
	}

	public List<Transaction> fiveTransaction(String fromAccount) {
		return repo.fiveTransaction(fromAccount);

	}

	public List<BankAccount> viewAccount(String pancard) {
		return repo.viewAccounts(pancard);
	}

	public boolean deposit(String accountNumber, int amount) {
		if (repo.deposit(accountNumber, amount) == "True") {
			return true;
		} else {
			return false;
		}
	}

	public boolean cashWithdrawal(String accountNumber, int amount) {
		if (repo.cashWithdrawal(accountNumber, amount)) {
			return true;
		} else {
			return false;
		}
	}

	public String generateCustomerId() {
		int min = 100000;
		int max = 999999;

		double customerId = Math.random() * (max - min + 1) + min;

		return String.valueOf(customerId);
	}

	public String generatePassword() {
		int min = 10000000;
		int max = 99999999;

		double password = Math.random() * (max - min + 1) + min;

		return String.valueOf(password);
	}

}