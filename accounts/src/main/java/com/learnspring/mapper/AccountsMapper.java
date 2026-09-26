package com.learnspring.mapper;

import com.learnspring.dto.request.AccountsDto;
import com.learnspring.entity.AccountsEntity;

public class AccountsMapper {
    public static AccountsDto mapToAccountsDto(AccountsEntity accounts, AccountsDto accountsDto) {
        accountsDto.setAccountNumber(accounts.getAccountNumber());
        accountsDto.setAccountType(accounts.getAccountType());
        accountsDto.setBranchAddress(accounts.getBranchAddress());
        return accountsDto;
    }

    public static AccountsEntity mapToAccounts(AccountsDto accountsDto, AccountsEntity accounts) {
        accounts.setAccountNumber(accountsDto.getAccountNumber());
        accounts.setAccountType(accountsDto.getAccountType());
        accounts.setBranchAddress(accountsDto.getBranchAddress());
        return accounts;
    }
}
