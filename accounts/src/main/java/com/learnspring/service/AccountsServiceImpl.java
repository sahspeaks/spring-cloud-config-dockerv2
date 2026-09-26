package com.learnspring.service;

import com.learnspring.constant.AccountConstants;
import com.learnspring.dto.request.AccountsDto;
import com.learnspring.dto.request.CustomerDto;
import com.learnspring.entity.AccountsEntity;
import com.learnspring.entity.CustomerEntity;
import com.learnspring.exception.CustomerAlreadyExistsException;
import com.learnspring.exception.ResourceNotFoundException;
import com.learnspring.mapper.AccountsMapper;
import com.learnspring.mapper.CustomerMapper;
import com.learnspring.repository.AccountsRepository;
import com.learnspring.repository.CustomerRepository;
import com.learnspring.service.IAccountsService;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
public class AccountsServiceImpl  implements IAccountsService {
    private final AccountsRepository accountsRepository;
    private final CustomerRepository customerRepository;

    public AccountsServiceImpl(AccountsRepository accountsRepository, CustomerRepository customerRepository) {
        this.accountsRepository = accountsRepository;
        this.customerRepository = customerRepository;
    }
    @Override
    public void createAccount(CustomerDto customerDto) {
        CustomerEntity customerEntity = CustomerMapper.mapToCustomer(customerDto, new CustomerEntity());
        Optional <CustomerEntity> existingCustomer = customerRepository.findByMobileNumber(customerEntity.getMobileNumber());
        if (existingCustomer.isPresent()) {
            throw new CustomerAlreadyExistsException("Customer with mobile number " + customerEntity.getMobileNumber() + " already exists.");
        }
        CustomerEntity savedCustomer = customerRepository.save(customerEntity);
        accountsRepository.save(createNewAccountForCustomer(savedCustomer));

    }

    @Override
    public CustomerDto getCustomerDetails(String mobileNumber) {
        CustomerEntity customerEntity = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
            () -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber)
        );
        AccountsEntity accountsEntity = accountsRepository.findByCustomerId(customerEntity.getCustomerId()).orElseThrow(
            () -> new ResourceNotFoundException("Account", "customerId", customerEntity.getCustomerId().toString())
        );
        CustomerDto customerDto = CustomerMapper.mapToCustomerDto(customerEntity, new CustomerDto());
        customerDto.setAccountsDto(AccountsMapper.mapToAccountsDto(accountsEntity, new AccountsDto()));
        return customerDto;
    }


    private AccountsEntity createNewAccountForCustomer(CustomerEntity savedCustomer) {
        AccountsEntity accountsEntity = new AccountsEntity();
        accountsEntity.setCustomerId(savedCustomer.getCustomerId());
        long accountNumber = 1000000000L + new Random().nextInt(900000000);
        accountsEntity.setAccountNumber(accountNumber);
        accountsEntity.setAccountType(AccountConstants.SAVINGS);
        accountsEntity.setBranchAddress(AccountConstants.ADDRESS);
        return accountsEntity;
    }

    @Override
    public boolean updateAccountDetails(CustomerDto customerDto) {
        boolean result = false;
        AccountsDto accountsDto = customerDto.getAccountsDto();
        if(accountsDto!=null) {
            AccountsEntity accountsEntity = accountsRepository.findById(accountsDto.getAccountNumber()).orElseThrow(
                    () -> new ResourceNotFoundException("Account", "accountNumber", accountsDto.getAccountNumber().toString())
            );
            AccountsMapper.mapToAccounts(accountsDto, accountsEntity);
            accountsRepository.save(accountsEntity);
            Long customerId = accountsEntity.getCustomerId();

            CustomerEntity savedCustomer = customerRepository.findById(customerId).orElseThrow(
                    () -> new ResourceNotFoundException("Customer", "customerId", customerId.toString())
            );
            CustomerMapper.mapToCustomer(customerDto, savedCustomer);
            customerRepository.save(savedCustomer);
            result = true;
        }
        return result;
    }

    @Override
    public boolean deleteAccount(String mobileNumber) {
        CustomerEntity customerEntity = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber)
        );
        accountsRepository.deleteByCustomerId(customerEntity.getCustomerId());
        customerRepository.deleteById(customerEntity.getCustomerId());
        return true;
    }

}
