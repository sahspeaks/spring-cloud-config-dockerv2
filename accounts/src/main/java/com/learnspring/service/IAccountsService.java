package com.learnspring.service;

import com.learnspring.dto.request.CustomerDto;

public interface IAccountsService {
    void createAccount(CustomerDto customerDto);

    CustomerDto getCustomerDetails(String mobileNumber);

    boolean updateAccountDetails(CustomerDto customerDto);
    boolean deleteAccount(String mobileNumber);
}
