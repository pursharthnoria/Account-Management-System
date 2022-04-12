package com.bankManagement.Barclays.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.bankManagement.Barclays.Users.BankCustomers;

public class BankCustomerMapper implements RowMapper<BankCustomers> {

	@Override
	public BankCustomers mapRow(ResultSet rs, int rowNum) throws SQLException {
		// TODO Auto-generated method stub
		BankCustomers customer=new BankCustomers();
		customer.setRole(rs.getString("role"));
		return customer;
	}

}
