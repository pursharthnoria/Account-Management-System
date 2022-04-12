package com.bankManagement.Barclays.repository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import com.bankManagement.Barclays.rowmapper.BankCustomerMapper;

import org.springframework.jdbc.core.JdbcTemplate;

@Repository
public class BankRepository {

	@Autowired
	JdbcTemplate jdbcTemplate;

	public String accountCreation(BankCustomers customer, String customerId, String password, String accountNumber) {
		try {
			String result="";
			if (viewAccounts(customer.getPanCard()).size() == 0) {
				SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
				java.util.Date utilDate = format.parse(customer.getDob());

				java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());

				jdbcTemplate.update("insert into Customer values (?,?,?,?,?,?,?,?,?,?,?)",
						new Object[] { customerId, customer.getPostalAddress(), customer.getAdharNumber(),
								customer.getPanCard(), customer.getPhoneNumber(), sqlDate, customer.getEmail(),
								password, customer.getCity(), customer.getName(), customer.getRole() });
				System.out.println(customerId);
				System.out.print(accountNumber);
				jdbcTemplate.update("insert into cust_account (Account_id, customer_id,balance) values(?,?,?)",
						new Object[] { accountNumber, customerId, 0 });
				result = customerId + " is generated with Account number " + accountNumber;
			}else {
				result="Customer already exists with the given Pancard";
			}
			
			return result;
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return "False";

		}
	}

	@SuppressWarnings("deprecation")
	public BankCustomers login(Login user) {
		BankCustomers customer = null;
		try {
			String query = "Select * from Customer where email=? and password=?";
			customer = jdbcTemplate.queryForObject(query, new Object[] { user.getUserId(), user.getPassword() },
					new BankCustomerMapper());
			return customer;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return customer;
		}
	}

	public List<BankAccount> viewAccounts(String pancard) {
		List<BankAccount> accounts = new ArrayList<>();
		try {
			List<Map<String, Object>> rows = jdbcTemplate.queryForList(
					"Select * from cust_account where customer_id in (Select c_id from Customer where pan=?)",
					new Object[] { pancard });

			for(Map<String, Object> row: rows) {
				BankAccount bankAccount=new BankAccount();
				bankAccount.setAccountNumber((String)row.get("Account_id"));
				bankAccount.setCustomerId((String)row.get("customer_id"));
				bankAccount.setBankBalance((Integer)row.get("balance"));
				accounts.add(bankAccount);
			}

			return accounts;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return accounts;
		}
	}

	public List<Transaction> fiveTransaction(String account) {
		List<Transaction> fiveTransactions = new ArrayList<>();
		try {
			List<Map<String, Object>> rows = jdbcTemplate.queryForList(
					"Select * from Transactions where trans_from = ? or trans_to= ? LIMIT 5",
					new Object[] { account, account });
			fiveTransactions = rows.stream().map(m -> {
				Transaction transaction = new Transaction();
				transaction.setTransactionReferenceNumber(String.valueOf(m.get("trans_id")));
				transaction.setFromAccountNumber(String.valueOf(m.get("trans_from")));
				transaction.setToAccountNumber(String.valueOf(m.get("trans_to")));
				transaction.setAmount(Float.parseFloat(String.valueOf(m.get("trans_amount"))));
				transaction.setType(String.valueOf(m.get("transaction_type")));
				transaction.setTransactionDate(String.valueOf(m.get("trans_date")));
				return transaction;
			}).collect(Collectors.toList());

			return fiveTransactions;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return fiveTransactions;
		}
	}

	public String deposit(String accountNumber, int amount, String transactionId) {
		try {
			System.out.println("deposit started");
			int newBalance=getbalance(accountNumber)+ amount;
			String query="UPDATE cust_account SET balance=? where Account_id=?";
			jdbcTemplate.update(query, new Object[] {newBalance, accountNumber});

			System.out.println("balance updated");
			SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
			Date date = new Date();
			java.sql.Date sqlDate = new java.sql.Date(date.getTime());
			jdbcTemplate.update("insert into Transactions (trans_id, trans_amount,transaction_type,trans_from,trans_to,trans_date) values (?,?,?,?,?,?)",
					new Object[] { transactionId, amount, "deposit", "Cash", accountNumber, sqlDate });

			return "True";
		} catch (Exception e) {
			e.printStackTrace();;
			return "False";
		}
	}

	public boolean cashWithdrawal(String accountNumber, int amount, String transactionId) {
		try {
			int currentBalance = getbalance(accountNumber);
			if (currentBalance > amount) {
				jdbcTemplate.update("UPDATE cust_account SET balance=balance-? where Account_id=?",
						new Object[] { amount, accountNumber });

				SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
				Date date = new Date();
				java.sql.Date sqlDate = new java.sql.Date(date.getTime());
				jdbcTemplate.update("insert into Transactions (trans_id, trans_amount,transaction_type,trans_from,trans_to,trans_date) values (?,?,?,?,?,?)",
						new Object[] { transactionId, amount, "withdraw", accountNumber, "cash", sqlDate });

				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
	}

	public int getbalance(String accountNumber) {
		try {
			String query = "Select balance from cust_account where Account_id=" + accountNumber;
			return jdbcTemplate.queryForObject(query, Integer.class);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return 0;
		}

	}

	public boolean transfer(String fromAccount, String toAccount, int amount, String transactionId) {
		try {
			String queryFrom = "UPDATE cust_account SET balance=balance-? WHERE Account_id=?";
			jdbcTemplate.update(queryFrom, new Object[] { amount, fromAccount });

			System.out.println("Amount debited.");
			String queryTo = "UPDATE cust_account SET balance=balance+? WHERE Account_id=?";
			jdbcTemplate.update(queryTo, new Object[] { amount, toAccount });
			System.out.println("Amount credited.");

			SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
			Date date = new Date();
			java.sql.Date sqlDate = new java.sql.Date(date.getTime());
			jdbcTemplate.update("insert into Transaction values (?,?,?,?,?,?)",
					new Object[] { transactionId, amount, "transfer", fromAccount, toAccount, sqlDate });

			return true;
		} catch (Exception e) {
			System.out.println("Could not transfer Funds.");
			return false;
		}
	}

	public List<Transaction> detailedTransaction(String account, String fromDate, String toDate) {
		List<Transaction> allTransactions = new ArrayList<>();
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
			java.util.Date utilDate = format.parse(fromDate);
			java.sql.Date fromDateSql = new java.sql.Date(utilDate.getTime());

			java.util.Date utilDate1 = format.parse(toDate);
			java.sql.Date toDateSql = new java.sql.Date(utilDate1.getTime());

			List<Map<String, Object>> rows = jdbcTemplate.queryForList(
					"Select * from Transactions where trans_from=? or trans_to=? AND trans_date>=? and trans_date<=?",
					new Object[] { account,account ,fromDateSql, toDateSql });
			allTransactions = rows.stream().map(m -> {
				Transaction transaction = new Transaction();
				transaction.setTransactionReferenceNumber(String.valueOf(m.get("trans_id")));
				transaction.setFromAccountNumber(String.valueOf(m.get("trans_from")));
				transaction.setToAccountNumber(String.valueOf(m.get("trans_to")));
				transaction.setAmount(Float.parseFloat(String.valueOf(m.get("trans_amount"))));
				transaction.setType(String.valueOf(m.get("transaction_type")));
				transaction.setTransactionDate(String.valueOf("trans_date"));
				return transaction;
			}).collect(Collectors.toList());

			return allTransactions;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return allTransactions;
		}
	}

}
