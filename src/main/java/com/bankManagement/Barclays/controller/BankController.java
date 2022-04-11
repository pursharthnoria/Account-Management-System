package com.bankManagement.Barclays.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bankManagement.Barclays.Users.bankCustomers;
import com.bankManagement.Barclays.Users.users;

@RestController
@RequestMapping("/bank")
public class BankController {
	
	@PostMapping(path="/login")
	public ResponseEntity<String> login(@RequestBody users user){
		return null;
	}
	
	@GetMapping("/queryAccount")
	public ResponseEntity<String> queryAccount(@RequestParam String pancard){
		return null;
	}
	
	@PostMapping("/accountCreation")
	public ResponseEntity<String> accountCreation(@RequestBody bankCustomers customer){
		return null;
	}
	
	@GetMapping("/viewAccounts")
	public ResponseEntity<String> viewAccounts(@RequestParam String pancard){
		return null;
	}
	
	@GetMapping("/lastTransaction")
	public ResponseEntity<String> fiveTransaction(@RequestParam String accountNumber){
		return null;
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