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

import com.bankManagement.Barclays.Users.BankCustomers;
import com.bankManagement.Barclays.Users.Transaction;
import com.bankManagement.Barclays.Users.Users;

@RestController
@RequestMapping("/bank")
public class BankController {
	
	@Autowired
    Operations operations;
	
	@PostMapping(path="/login")
	public ResponseEntity<String> login(@RequestBody Users user){
		return null;
	}
	
	@GetMapping("/queryAccount")
	public ResponseEntity<String> queryAccount(@RequestParam String pancard){
		return null;
	}
	
	@PostMapping("/accountCreation")
	public ResponseEntity<String> accountCreation(@RequestBody BankCustomers customer){
	  String customerId=operations.accountCreation(customer);
	  ResponseEntity<String> response;
	  if(customerId!="False") {
		  response= new ResponseEntity<String>(customerId,HttpStatus.OK);
	  }else {
		  response= new ResponseEntity<String>("Error",HttpStatus.OK);
	  }
	  return response;
	  
	}
	
	@GetMapping("/viewAccounts")
	public ResponseEntity<String> viewAccounts(@RequestParam String pancard){
		return null;
	}
	
	@GetMapping("/lastTransaction")
	public ResponseEntity<List<Transaction>> fiveTransaction(@RequestParam String accountNumber){
		List<Transaction> transaction=operations.fiveTransaction(accountNumber);
		return new ResponseEntity<List<Transaction>>(transaction, HttpStatus.OK);
		
		
	}
	
	@GetMapping("/detailedTransaction")
	public ResponseEntity<String> detailedTransaction(@RequestParam String accountNumber){
		return null;
	}
	
	@PutMapping("/withdrawal")
	public ResponseEntity<String> cashWithdrawal(@RequestParam String accountNumber, @RequestParam int amount){
		return null;
	}
	
	@PutMapping("/deposit")
	public ResponseEntity<String> cashDeposit(@RequestParam String accountNumber, @RequestParam int amount){
		return null;
	}
	
	@PutMapping("/transfer")
	public ResponseEntity<String> transfer(@RequestParam String fromAccountNumber, @RequestParam String toAccountNumber, @RequestParam int amount){
		return null;
	}
	
}