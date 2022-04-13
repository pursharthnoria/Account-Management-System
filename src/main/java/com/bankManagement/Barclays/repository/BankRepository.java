package com.bankManagement.Barclays.repository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.bankManagement.Barclays.Users.BankAccount;
import com.bankManagement.Barclays.Users.BankCustomers;
import com.bankManagement.Barclays.Users.ChangePassword;
import com.bankManagement.Barclays.Users.Login;
import com.bankManagement.Barclays.Users.Transaction;
import com.bankManagement.Barclays.rowmapper.BankCustomerMapper;

import org.springframework.jdbc.core.JdbcTemplate;

@Repository
public class BankRepository {

	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Autowired
	EmailSender mail;
	
	Logger logger=LoggerFactory.getLogger(BankRepository.class);

	public String accountCreation(BankCustomers customer, String customerId, String password, String accountNumber) {
		try {
			String result="";
			if (viewAccounts(customer.getPanCard()).size() == 0) {
				SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
				java.util.Date utilDate = format.parse(customer.getDob());

				java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
				
				logger.info("Creating customer id............");

				jdbcTemplate.update("insert into Customer values (?,?,?,?,?,?,?,?,?,?,?)",
						new Object[] { customerId, customer.getPostalAddress(), customer.getAdharNumber(),
								customer.getPanCard(), customer.getPhoneNumber(), sqlDate, customer.getEmail(),
								password, customer.getCity(), customer.getName(), customer.getRole() });
				
				logger.info("Creating account id.................");
				
				jdbcTemplate.update("insert into cust_account (Account_id, customer_id,balance) values(?,?,?)",
						new Object[] { accountNumber, customerId, 0 });
				
				result = customerId + " is generated with Account number " + accountNumber;
				logger.info(result);
				String subject="Customer Id creation and Account Id creation";
				String body=result+"\n"+"Your temporary password is "+password+"Kindly change it while login for the first time.";
				mail.sendEmail(customer.getEmail(), subject, body);
				
				logger.info("Mail sent to user");
			}else {
				logger.info("Cannot create new customer id as user with this pancard already exists");
				result="Customer already exists with the given Pancard";
			}
			
			return result;
			
		} catch (Exception e) {
			logger.error(e.getMessage());
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
			logger.info("User found with given id and password");
			return customer;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return customer;
		}
	}

	public List<BankAccount> viewAccounts(String pancard) {
		List<BankAccount> accounts = new ArrayList<>();
		try {
			logger.info("Fetching list of account linked with pancard "+pancard);
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

			logger.info("returning list of account linked with pancard");
			return accounts;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return accounts;
		}
	}

	public List<Transaction> fiveTransaction(String account) {
		List<Transaction> fiveTransactions = new ArrayList<>();
		try {
			logger.info("Fetching last five transactions of account:"+ account);
			List<Map<String, Object>> rows = jdbcTemplate.queryForList(
					"Select * from Transactions where trans_from = ? or trans_to= ? ORDER BY trans_Date DESC LIMIT 5",
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

			logger.info("returning last five transaction of given account");
			return fiveTransactions;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return fiveTransactions;
		}
	}

	public String deposit(String accountNumber, int amount, String transactionId) {
		try {
			logger.info("deposit started");
			int newBalance=getbalance(accountNumber)+ amount;
			
			String query="UPDATE cust_account SET balance=? where Account_id=?";
			jdbcTemplate.update(query, new Object[] {newBalance, accountNumber});

			logger.info("balance updated");
			SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
			Date date = new Date();
			java.sql.Date sqlDate = new java.sql.Date(date.getTime());
			
			logger.info("Updating transactions...............");
			
			jdbcTemplate.update("insert into Transactions (trans_id, trans_amount,transaction_type,trans_from,trans_to,trans_date) values (?,?,?,?,?,?)",
					new Object[] { transactionId, amount, "deposit", "Cash", accountNumber, sqlDate });

			String subject="Money deposit";
			String body=amount+" is deposited to your bank account "+accountNumber+" with transaction id: "+transactionId;
			
			String query1="Select customer_id from cust_account where Account_id="+accountNumber;
			String customerid=jdbcTemplate.queryForObject(query1, String.class);
			
			String query2="Select email from Customer where c_id="+customerid;
			String email=jdbcTemplate.queryForObject(query2, String.class);
			mail.sendEmail(email, subject, body);
			logger.info("Mail sent to user");
			return "True";
		} catch (Exception e) {
			logger.error(e.getMessage());
			return "False";
		}
	}

	public boolean cashWithdrawal(String accountNumber, int amount, String transactionId) {
		try {
			logger.info("checking current balance");
			int currentBalance = getbalance(accountNumber);
			if (currentBalance > amount) {
				jdbcTemplate.update("UPDATE cust_account SET balance=balance-? where Account_id=?",
						new Object[] { amount, accountNumber });

				logger.info("Withdrawal successfull......");
				
				SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
				Date date = new Date();
				java.sql.Date sqlDate = new java.sql.Date(date.getTime());
				
				jdbcTemplate.update("insert into Transactions (trans_id, trans_amount,transaction_type,trans_from,trans_to,trans_date) values (?,?,?,?,?,?)",
						new Object[] { transactionId, amount, "withdraw", accountNumber, "cash", sqlDate });

				logger.info("Updating Transactions.......");
				
				String subject="Cash Withdraw";
				String body=amount+" is withdraw from your bank account "+accountNumber+" with transaction id: "+transactionId;
				
				String query1="Select customer_id from cust_account where Account_id="+accountNumber;
				String customerid=jdbcTemplate.queryForObject(query1, String.class);
				
				String query2="Select email from Customer where c_id="+customerid;
				String email=jdbcTemplate.queryForObject(query2, String.class);
				
				mail.sendEmail(email, subject, body);
				logger.info("Mail sent to user");
				return true;
			} else {
				logger.info("Insucficcient balance......");
				return false;
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			return false;
		}
	}

	public int getbalance(String accountNumber) {
		try {
			logger.info("checking balance......");
			String query = "Select balance from cust_account where Account_id="+accountNumber;
			return jdbcTemplate.queryForObject(query, Integer.class);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return -1;
		}

	}

	public boolean transfer(String fromAccount, String toAccount, int amount, String transactionId) {
		try {
			logger.info("initiating transfer................");
			String queryFrom = "UPDATE cust_account SET balance=balance-? WHERE Account_id=?";
			jdbcTemplate.update(queryFrom, new Object[] { amount, fromAccount });

			logger.info("Amount debited from account "+ fromAccount);
			
			
			String queryTo = "UPDATE cust_account SET balance=balance+? WHERE Account_id=?";
			jdbcTemplate.update(queryTo, new Object[] { amount, toAccount });
			logger.info("Amount credited to account: "+toAccount );

			String subject="Amount debited";
			String body=amount+" is sent from your bank account "+fromAccount+" to bank account "+toAccount+" with transaction id: "+transactionId;
			
			String query1="Select customer_id from cust_account where Account_id="+fromAccount;
			String customerid=jdbcTemplate.queryForObject(query1, String.class);
			
			String query2="Select email from Customer where c_id="+customerid;
			String email=jdbcTemplate.queryForObject(query2, String.class);
			
			mail.sendEmail(email, subject, body);
			

			logger.info("Mail sent to users");
			
			SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
			Date date = new Date();
			java.sql.Date sqlDate = new java.sql.Date(date.getTime());
//			System.out.println(date);
			jdbcTemplate.update("insert into Transactions (trans_id, trans_amount, transaction_type, trans_from, trans_to, trans_date) values (?,?,?,?,?,?)",
					new Object[] { transactionId, amount, "transfer", fromAccount, toAccount, sqlDate });

			logger.info("updating transactions......");
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return false;
		}
	}

	public List<Transaction> detailedTransaction(String account, String fromDate, String toDate) {
		List<Transaction> allTransactions = new ArrayList<>();
		try {
			logger.info("Fetching detailed transaction..........");
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
				transaction.setTransactionDate(String.valueOf(m.get("trans_date")));
				return transaction;
			}).collect(Collectors.toList());

			logger.info("returning detailed transaction.......");
			return allTransactions;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return allTransactions;
		}
	}


	public boolean passwordChange(ChangePassword password) {
		String query="UPDATE Customer SET password=? where c_id=?";
		try {
			jdbcTemplate.update(query,new Object[] {password.getNewPassword(),password.getUserId()});
			logger.info("password changes successfully");
			return true;
			}catch (Exception e) {
				logger.error(e.getMessage());
				return false;
			}
		}
	
	public boolean createAccount(String customerId, String accountNumber) {
		try {

			jdbcTemplate.update("insert into cust_account (Account_id, customer_id,balance) values(?,?,?)",
					new Object[] { accountNumber, customerId, 0 });
			logger.info("New Account created for :"+ customerId+" with account number "+ accountNumber );
			return true;
			
			
		}catch (Exception e) {
			// TODO: handle exception
			logger.info(e.getMessage());
			return false;
		}
		
	}
}
