package com.learnspring.service;

import com.learnspring.constant.LoansConstant;
import com.learnspring.dto.request.LoansDto;
import com.learnspring.entity.LoansEntity;
import com.learnspring.exception.LoansAlreadyExistsException;
import com.learnspring.exception.ResourceNotFoundException;
import com.learnspring.mapper.LoansMapper;
import com.learnspring.repository.LoansRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service @AllArgsConstructor
public class LoansServiceImpl implements ILoansService{
    private final LoansRepository loansRepository;


    @Override
    public void createLoan(String mobileNumber) {
        Optional<LoansEntity> loans= loansRepository.findByMobileNumber(mobileNumber);
        if(loans.isPresent()){
            throw new LoansAlreadyExistsException("Loan already registered with given mobileNumber "+mobileNumber);
        }
        loansRepository.save(createNewLoan(mobileNumber));
    }
    private LoansEntity createNewLoan(String mobileNumber) {
        LoansEntity loansEntity = new LoansEntity();
        loansEntity.setMobileNumber(mobileNumber);
        loansEntity.setLoanNumber("LN"+System.currentTimeMillis());
        loansEntity.setLoanType(LoansConstant.HOME_LOAN);
        loansEntity.setTotalLoan(LoansConstant.NEW_LOAN_LIMIT);
        loansEntity.setAmountPaid(0);
        loansEntity.setOutstandingAmount(LoansConstant.NEW_LOAN_LIMIT);
        return loansEntity;
    }

    @Override
    public LoansDto fetchLoan(String mobileNumber) {
        LoansEntity loans = loansRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Loan", "mobileNumber", mobileNumber)
        );
        return LoansMapper.mapToLoansDto(loans, new LoansDto());
    }

    @Override
    public boolean updateLoan(LoansDto loansDto) {
        LoansEntity loans = loansRepository.findByLoanNumber(loansDto.getLoanNumber()).orElseThrow(
                () -> new ResourceNotFoundException("Loan", "LoanNumber", loansDto.getLoanNumber()));
        loansRepository.save(LoansMapper.mapToLoans(loansDto, loans));
        return  true;
    }

    @Override
    public boolean deleteLoan(String mobileNumber) {
        LoansEntity loans = loansRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Loan", "mobileNumber", mobileNumber)
        );
        loansRepository.deleteById(loans.getLoanId());
        return true;
    }
}
