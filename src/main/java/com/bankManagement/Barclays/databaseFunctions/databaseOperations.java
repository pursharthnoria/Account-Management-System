package com.bankManagement.Barclays.databaseFunctions;

import java.sql.*;
import com.bankManagement.Barclays.Users.bankCustomers;
import com.bankManagement.Barclays.Users.users;
import com.bankManagement.Barclays.Users.bankAccount;
import com.bankManagement.Barclays.Users.role;
import com.bankManagement.Barclays.Users.transfer;

public class databaseOperations {

    public Connection connect() {
        try {
            Class.forName("com.mysql.jdbc.Driver");

            String url = "";
            String username = "";
            String password = "";

            Connection con = DriverManager.getConnection(url, username, password);

            if (con.isClosed()) {
                System.out.println("Connection is closed");
            } else {
                System.out.println("Connection Created..");
                return con;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void insertCustomer(bankCustomers customer) throws SQLException {
        Connection con = this.connect();
        String query = "";
        Statement stmt = con.createStatement();
        stmt.executeUpdate(query);
        System.out.println("Inserted the user.");
        con.close();
    }

    public void fetchCustomer(users user) {
        Connection con = this.connect();
        String query = "";
        Statement stmt = con.createStatement();
        stmt.executeUpdate(query);
        System.out.println("Inserted the user.");
        con.close();
    }

}