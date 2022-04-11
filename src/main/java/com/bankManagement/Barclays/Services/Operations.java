package com.bankManagement.Barclays.Services;

import java.lang.Math;

public class Operations {
    // import the classes from the package users here
    // Write the functions that are required by Prerna here in this class.

    public String generateCustomerId(){
        int min = 100000;
        int max = 999999;

        double customerId = Math.random()*(max-min+1)+min;

        return String.valueOf(customerId);
    }

    public String generatePassword(){
        int min = 10000000;
        int max = 99999999;

        double password = Math.random()*(max-min+1)+min;

        return String.valueOf(password);
    }

}
