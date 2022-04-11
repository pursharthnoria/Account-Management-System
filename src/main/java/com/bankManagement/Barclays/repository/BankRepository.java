package com.bankManagement.Barclays.repository;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.bankManagement.Barclays.Users.users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.bankManagement.Barclays.Services.Operations;

import com.bankManagement.Barclays.Users.bankCustomers;

import org.springframework.jdbc.core.JdbcTemplate;

@Repository
public class BankRepository {

	@Autowired
	JdbcTemplate jdbcTemplate;

	public String accountCreation(bankCustomers customer) {
		Operations operate = new Operations();
        String password = operate.generatePassword();
		customer.setCustomerId(operate.generateCustomerId());
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
			java.util.Date utilDate = format.parse(customer.getDob());

			java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());

			jdbcTemplate.update("insert into Customer values (?,?,?,?,?,?,?,?,?,?)",
					new Object[] { customer.getCustomerId(), customer.getPostalAddress(), customer.getAdharNumber(),
							customer.getPanCard(), customer.getPhoneNumber(), sqlDate, customer.getEmail(), password,
							customer.getCity(), customer.getName() });
			return customer.getCustomerId();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return "False";

		}
	}

    public List login(users user) {
        try {
            return jdbcTemplate.queryForList("Select * from Customer where email=? and password=?", new Object[] { user.getUserId(), user.getPassword()});
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Collections.emptyList();
        }
    }

    public List viewAccounts(String pancard) {
        try {
            return jdbcTemplate.queryForList("Select * from cust_account where customer_id in (Select c_id from Customer where pan=?)",
                    new Object[] { pancard });

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Collections.emptyList();
        }
    }

    public List fiveTransaction(String fromAccount) {
        try {
            return jdbcTemplate.queryForList("Select * from Transactions where fromAccount = ?) LIMIT 5", new Object[] { fromAccount });

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Collections.emptyList();
        }
    }

    public String deposit(String accountNumber, int amount){
        try{
            jdbcTemplate.update("UPDATE Account SET ac_balance=ac_balance+? where ac_num=?",new Object[] { accountNumber, amount});
            return "True";
        } catch (Exception e){
            System.out.println(e.getMessage());
            return "False";
        }
    }

}
