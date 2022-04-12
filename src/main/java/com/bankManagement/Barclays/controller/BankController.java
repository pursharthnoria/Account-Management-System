package com.bankManagement.Barclays.controller;

import com.bankManagement.Barclays.Services.Operations;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bankManagement.Barclays.Users.BankAccount;
import com.bankManagement.Barclays.Users.BankCustomers;
import com.bankManagement.Barclays.Users.Transaction;
import com.bankManagement.Barclays.Users.Login;

@RestController
@RequestMapping("/bank")
public class BankController {

	@Autowired
	Operations operations;

	@PostMapping(path = "/login")
	public ResponseEntity<String> login(@RequestBody Login user) {
		BankCustomers customer=operations.login(user);
		String result="";
		if(customer==null) {
			result="Wrong credentials. Please try again with correct ID and paaword";
			return new ResponseEntity<String>(result, HttpStatus.BAD_REQUEST);
		}
		result=customer.getRole();
		return new ResponseEntity<String>(result,HttpStatus.OK);
	}

	@GetMapping("/queryAccount")
	public ResponseEntity<Boolean> queryAccount(@RequestParam String pancard) {
		ResponseEntity<Boolean> response;
		List<BankAccount> Accounts = operations.viewAccount(pancard);
		if (Accounts.size() > 0) {
			response = new ResponseEntity<Boolean>(true, HttpStatus.OK);
		} else {
			response = new ResponseEntity<Boolean>(false, HttpStatus.NOT_FOUND);
		}
		return response;
	}

	@PostMapping("/accountCreation")
	public ResponseEntity<String> accountCreation(@RequestBody BankCustomers customer) {
		String customerId = operations.accountCreation(customer);
		ResponseEntity<String> response;
		if (customerId != "False") {
			response = new ResponseEntity<String>(customerId, HttpStatus.OK);
		} else {
			response = new ResponseEntity<String>("Error", HttpStatus.OK);
		}
		return response;

	}

	@GetMapping("/viewAccounts")
	public ResponseEntity<List<BankAccount>> viewAccounts(@RequestParam String pancard) {
		List<BankAccount> Accounts = operations.viewAccount(pancard);
		return new ResponseEntity<List<BankAccount>>(Accounts, HttpStatus.OK);
	}

	@GetMapping("/lastTransactions")
	public ResponseEntity<List<Transaction>> fiveTransaction(@RequestParam String accountNumber) {
		List<Transaction> transaction = operations.fiveTransaction(accountNumber);
		return new ResponseEntity<List<Transaction>>(transaction, HttpStatus.OK);

	}

	@GetMapping("/detailedTransactions")
	public ResponseEntity<List<Transaction>> detailedTransaction(@RequestParam String accountNumber, @RequestParam String fromDate, @RequestParam String toDate) {
		List<Transaction> transaction = operations.detailedTransaction(accountNumber, fromDate, toDate);
		return new ResponseEntity<List<Transaction>>(transaction, HttpStatus.OK);
	}

	@PutMapping("/withdrawal")
	public ResponseEntity<String> cashWithdrawal(@RequestParam String accountNumber, @RequestParam int amount) {
		String result;
		ResponseEntity<String> response;
		if(amount>10000) {
			result="Limit for withdraw in single day is 10000. More than 10000 cannot be withdraw in single day";
			response=new ResponseEntity<String>(result,HttpStatus.BAD_REQUEST);
		}
		if (operations.cashWithdrawal(accountNumber, amount)) {
			result = amount + "withdraw from account " + accountNumber;
			response = new ResponseEntity<String>(result, HttpStatus.OK);
		} else {
			result = "cashWithdrawal can not be done due to some error. Please try again";
			response = new ResponseEntity<String>(result, HttpStatus.BAD_REQUEST);
		}
		return response;

	}

	@PutMapping("/deposit")
	public ResponseEntity<String> cashDeposit(@RequestParam String accountNumber, @RequestParam int amount) {
		String result;
		ResponseEntity<String> response;
		if (operations.cashWithdrawal(accountNumber, amount)) {
			result = amount + " is successfully deposited to account " + accountNumber;
			response = new ResponseEntity<String>(result, HttpStatus.OK);
		} else {
			result = "Amount is not deposited due to some error please try again";
			response = new ResponseEntity<String>(result, HttpStatus.BAD_REQUEST);
		}
		return response;
	}

	@PutMapping("/transfer")
	public ResponseEntity<String> transfer(@RequestParam String fromAccountNumber, @RequestParam String toAccountNumber,
			@RequestParam int amount) {
		String result= operations.transfer(fromAccountNumber, toAccountNumber, amount);
		if(result.contains("successfull")) {
			return new ResponseEntity<String>(result,HttpStatus.OK);
		}else {
			return new ResponseEntity<String>(result,HttpStatus.BAD_REQUEST);
		}
	}

}