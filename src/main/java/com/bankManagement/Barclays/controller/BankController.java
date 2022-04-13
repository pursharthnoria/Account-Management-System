package com.bankManagement.Barclays.controller;

import com.bankManagement.Barclays.Services.Operations;

import java.util.List;

import org.apache.catalina.startup.ClassLoaderFactory.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bankManagement.Barclays.Users.BankAccount;
import com.bankManagement.Barclays.Users.BankCustomers;
import com.bankManagement.Barclays.Users.ChangePassword;
import com.bankManagement.Barclays.Users.Transaction;
import com.bankManagement.Barclays.Users.Login;

@RestController
@RequestMapping("/bank")
public class BankController{

	@Autowired
	Operations operations;
	
	Logger logger=LoggerFactory.getLogger(BankController.class);

	@PostMapping(path = "/login")
	public ResponseEntity<String> login(@RequestBody Login user) {
		logger.info("Loging in user..........");
		BankCustomers customer=operations.login(user);
		String result="";
		if(customer==null) {
			logger.info("Loging failed as user provided wrong credentials");
			result="Wrong credentials. Please try again with correct ID and password";
			return new ResponseEntity<String>(result, HttpStatus.BAD_REQUEST);
		}
		result=customer.getRole();
		logger.info(user.getUserId()+" log in successful and user has a role: "+ result);
		return new ResponseEntity<String>(result,HttpStatus.OK);
	}

	@GetMapping("/queryAccount")
	public ResponseEntity<Boolean> queryAccount(@RequestParam String pancard) {
		ResponseEntity<Boolean> response;
		logger.info("Checking account for the pancard "+ pancard+"........................");
		List<BankAccount> Accounts = operations.viewAccount(pancard);
		if (Accounts.size() > 0) {
			logger.info("User already exist with the given pancard not need to create new user");
			response = new ResponseEntity<Boolean>(true, HttpStatus.OK);
		} else {
			logger.error("No user exists with the pancard "+ pancard+" Kindly register the user to generate customer id");
			response = new ResponseEntity<Boolean>(false, HttpStatus.NOT_FOUND);
		}
		return response;
	}

	@PostMapping("/accountCreation")
	public ResponseEntity<String> accountCreation(@RequestBody BankCustomers customer) {
		logger.info("Creating customer account..............");
		String customerId = operations.accountCreation(customer);
		ResponseEntity<String> response;
		if (customerId != "False") {
			logger.info("Customer is created with customer id: "+customerId);
			response = new ResponseEntity<String>(customerId, HttpStatus.OK);
		} else {
			
			response = new ResponseEntity<String>("Error", HttpStatus.OK);
		}
		return response;

	}

	@GetMapping("/viewAccounts")
	public ResponseEntity<List<BankAccount>> viewAccounts(@RequestParam String pancard) {
		logger.info("Showing linked accounts of user......................");
		List<BankAccount> Accounts = operations.viewAccount(pancard);
		return new ResponseEntity<List<BankAccount>>(Accounts, HttpStatus.OK);
	}

	@GetMapping("/lastTransactions")
	public ResponseEntity<List<Transaction>> fiveTransaction(@RequestParam String accountNumber) {
		logger.info("showing last 5 transaction of user with account number: "+ accountNumber);
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
		logger.info("Withdrawing the money.....................");
		if(amount>10000) {
			logger.info("Cannot withdraw more than 10000");
			result="Limit for withdraw in single day is 10000. More than 10000 cannot be withdraw in single day";
			response=new ResponseEntity<String>(result,HttpStatus.BAD_REQUEST);
		}
		if (operations.cashWithdrawal(accountNumber, amount)) {
			result = amount + " withdraw from account " + accountNumber;
			logger.info(amount+" has been withdraw from acoount: "+accountNumber);
			response = new ResponseEntity<String>(result, HttpStatus.OK);
		} else {
			logger.info("Error......cannot withdraw");
			result = "cash Withdrawal can not be done due to some error. Please try again";
			response = new ResponseEntity<String>(result, HttpStatus.BAD_REQUEST);
		}
		return response;

	}

	@PutMapping("/deposit")
	public ResponseEntity<String> cashDeposit(@RequestParam String accountNumber, @RequestParam int amount) {
		String result;
		ResponseEntity<String> response;
		logger.info("depositing.................");
		if (operations.deposit(accountNumber, amount)) {
			result = amount + " is successfully deposited to account " + accountNumber;
			logger.info(amount+" is deposited to account: "+accountNumber);
			response = new ResponseEntity<String>(result, HttpStatus.OK);
		} else {
			logger.info("Error.....................");
			result = "Amount is not deposited due to some error please try again";
			response = new ResponseEntity<String>(result, HttpStatus.BAD_REQUEST);
		}
		return response;
	}

	@PutMapping("/transfer")
	public ResponseEntity<String> transfer(@RequestParam String fromAccountNumber, @RequestParam String toAccountNumber,
			@RequestParam int amount) {
		logger.info("Transferring....................");
		String result= operations.transfer(fromAccountNumber, toAccountNumber, amount);
		if(result.contains("successfull")) {
			logger.info("Funds are transferred successfull");
			return new ResponseEntity<String>(result,HttpStatus.OK);
		}else {
			logger.info("Funds are not transfered successfully. Please try again");
			return new ResponseEntity<String>(result,HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping("/changePassword")
	public ResponseEntity<String> changePassword(@RequestBody ChangePassword changePassword){
		logger.info("Changing password.....................");
		  String result=operations.changePassword(changePassword);
		  return new ResponseEntity<String>(result,HttpStatus.OK);
	}
	
	@GetMapping("/checkBalance")
	public ResponseEntity<String> checkBalance(@RequestParam String accountNumber){
		if(operations.getBalance(accountNumber)>=0){
			return new ResponseEntity<String>(Integer.toString(operations.getBalance(accountNumber)), HttpStatus.OK);
		}else {
			return new ResponseEntity<String>("Account does not exist", HttpStatus.OK);
		}
		
	}
	
	@PostMapping("/createAccount")
	public ResponseEntity<String> createAccount(@PathVariable String customerId){
		return new ResponseEntity<String>(operations.createAccount(customerId), HttpStatus.OK);
	}

}