package com.bankManagement.Barclays.Services;

import java.lang.Math;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bankManagement.Barclays.Users.BankAccount;
import com.bankManagement.Barclays.Users.BankCustomers;
import com.bankManagement.Barclays.Users.ChangePassword;
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
		return repo.accountCreation(customer, generateCustomerId(), generatePassword(), generateAccountNumber());
	}

	public List<Transaction> fiveTransaction(String account) {
		return repo.fiveTransaction(account);

	}

	public List<BankAccount> viewAccount(String pancard) {
		return repo.viewAccounts(pancard);
	}

	public boolean deposit(String accountNumber, int amount) {
		if (repo.deposit(accountNumber, amount, generateTransactionId()) == "True") {
			return true;
		} else {
			return false;
		}
	}

	public boolean cashWithdrawal(String accountNumber, int amount) {
		if (repo.cashWithdrawal(accountNumber, amount, generateTransactionId())) {
			return true;
		} else {
			return false;
		}
	}
	
	public List<Transaction> detailedTransaction(String account, String fromDate, String toDate){
		return repo.detailedTransaction(account, fromDate, toDate);
	}
	
	public String transfer(String fromAccount, String toAccount, int amount) {
		String transactionId=generateTransactionId();
		String result="";
		if(repo.transfer(fromAccount, toAccount, amount,transactionId )) {
			result="Transfering funds is successfull. TransactionId "+ transactionId;
		}else {
			result="Transfering fund is not done due to some error.Please try again";
		}
		return result;
		
	}
	
	public String changePassword(ChangePassword password) {
		if(repo.passwordChange(password)) {
			return "Password changed successfully";
		}else {
			return "Password is not changed due to some error please try again";
		}
	}

	public String generateCustomerId() {
		int min = 100000;
		int max = 999999;

		int customerId = (int) (Math.random() * (max - min + 1) + min);

		return String.valueOf(customerId);
	}

	public String generatePassword() {
		int min = 10000000;
		int max = 99999999;

		int password = (int) (Math.random() * (max - min + 1) + min);

		return String.valueOf(password);
	}

    public String generateTransactionId() {
        int min = 10000;
        int max = 99999;

        int partTransactionId = (int) (Math.random() * (max - min + 1) + min);
        int SecondPartTransactionId = (int) (Math.random() * (max - min + 1) + min);

        return String.valueOf(partTransactionId)+String.valueOf(SecondPartTransactionId);
    }

    public String generateAccountNumber() {
        int min = 10000;
        int max = 99999;

        int first = (int) (Math.random() * (max - min + 1) + min);
        int Second= (int) (Math.random() * (max - min + 1) + min);

        return String.valueOf(first)+String.valueOf(Second);
    }
    
    
}