package com.bankManagement.Barclays.repository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import com.bankManagement.Barclays.Users.BankAccount;
import com.bankManagement.Barclays.Users.BankCustomers;
import com.bankManagement.Barclays.Users.Login;
import com.bankManagement.Barclays.Users.Transaction;

import org.springframework.jdbc.core.JdbcTemplate;

@Repository
public class BankRepository {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	Transaction transaction;

	@Autowired
	BankAccount bankAccounts;

	public String accountCreation(BankCustomers customer, String customerId, String password) {
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
			java.util.Date utilDate = format.parse(customer.getDob());

			java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());

			jdbcTemplate.update("insert into Customer values (?,?,?,?,?,?,?,?,?,?)",
					new Object[] { customerId, customer.getPostalAddress(), customer.getAdharNumber(),
							customer.getPanCard(), customer.getPhoneNumber(), sqlDate, customer.getEmail(), password,
							customer.getCity(), customer.getName() });
			return customerId;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return "False";

		}
	}

	public BankCustomers login(Login user) {
		BankCustomers customer=null;
		try {
			String query="Select * from Customer where email="+ user.getUserId() + " and password=" + user.getPassword();
			customer=jdbcTemplate.queryForObject(query, BankCustomers.class);
			return customer;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return customer;
		}
	}

	public List<BankAccount> viewAccounts(String pancard) {
		List<BankAccount> Accounts = new ArrayList<>();
		try {
			List<Map<String, Object>> rows = jdbcTemplate.queryForList(
					"Select * from cust_account where customer_id in (Select c_id from Customer where pan=?)",
					new Object[] { pancard });
			Accounts = rows.stream().map(m -> {
				bankAccounts.setAccountNumber(String.valueOf("Account_id"));
				bankAccounts.setCustomerId(String.valueOf("customer_id"));
				bankAccounts.setBankBalance(Integer.parseInt(String.valueOf("init_deposit")));
				return bankAccounts;
			}).collect(Collectors.toList());

			return Accounts;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return Accounts;
		}
	}

	public List<Transaction> fiveTransaction(String fromAccount) {
		List<Transaction> fiveTransactions = new ArrayList<>();
		try {
			List<Map<String, Object>> rows = jdbcTemplate.queryForList(
					"Select * from Transactions where fromAccount = ? LIMIT 5", new Object[] { fromAccount });
			fiveTransactions = rows.stream().map(m -> {
				transaction.setTransactionReferenceNumber(String.valueOf(m.get("trans_id")));
				transaction.setFromAccountNumber(String.valueOf(m.get("trans_from")));
				transaction.setToAccountNumber(String.valueOf(m.get("trans_to")));
				transaction.setAmount(Float.parseFloat(String.valueOf(m.get("trans_amount"))));
				transaction.setType(String.valueOf(m.get("transaction_type")));
				return transaction;
			}).collect(Collectors.toList());

			return fiveTransactions;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return fiveTransactions;
		}
	}

	public String deposit(String accountNumber, int amount) {
		try {
			jdbcTemplate.update("UPDATE Account SET ac_balance=ac_balance+? where ac_num=?",
					new Object[] { amount, accountNumber });
			return "True";
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return "False";
		}
	}

	public boolean cashWithdrawal(String accountNumber,int amount){
    	try {
    		int currentBalance= getbalance(accountNumber);
        	if(currentBalance>amount) {
        		jdbcTemplate.update("UPDATE Account SET ac_balance=ac_balance-? where ac_num=?",new Object[] {amount, accountNumber});
        		return true;
        	}else {
        		return false;
        	}
        }catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
    }

	public int getbalance(String accountNumber) {
		try {
			String query = "Select init_deposit from cust_account where Account_id=" + accountNumber;
			return jdbcTemplate.queryForObject(query, Integer.class);
		}catch (Exception e) {
			System.out.println(e.getMessage());
			return 0;
		}
		
	}

}
