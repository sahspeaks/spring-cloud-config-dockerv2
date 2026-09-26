package com.learnspring.service;

import com.learnspring.constant.CardsConstants;
import com.learnspring.dto.request.CardsDto;
import com.learnspring.entity.CardsEntity;
import com.learnspring.exception.ResourceNotFoundException;
import com.learnspring.mapper.CardsMapper;
import com.learnspring.repository.CardsRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service @AllArgsConstructor
public class CardServiceImpl implements ICardService{

    private CardsRepository cardsRepository;

    @Override
    public void createCard(String mobileNumber) {
        Optional<CardsEntity> cards = cardsRepository.findByMobileNumber(mobileNumber);
        if(cards.isPresent()){
            throw new RuntimeException("Card already exists for this mobile number");
        }
        CardsEntity cardsEntity = createNewCardForCustomer(mobileNumber);
        cardsRepository.save(cardsEntity);
    }
    private CardsEntity createNewCardForCustomer(String mobileNumber) {
        CardsEntity cardsEntity = new CardsEntity();
        cardsEntity.setMobileNumber(mobileNumber);
        long cardNumber = 1000000000000000L + new Random().nextInt(900000000);
        cardsEntity.setCardNumber(Long.toString(cardNumber));
        cardsEntity.setCardType(CardsConstants.CREDIT_CARD);
        cardsEntity.setTotalLimit(CardsConstants.NEW_CARD_LIMIT);
        cardsEntity.setAmountUsed(0);
        cardsEntity.setAvailableAmount(CardsConstants.NEW_CARD_LIMIT);
        return cardsEntity;
    }

    @Override
    public CardsDto fetchCard(String mobileNumber) {
        CardsEntity cards = cardsRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Card", "mobileNumber", mobileNumber)
        );
        return CardsMapper.mapToCardsDto(cards, new CardsDto());
    }

    @Override
    public boolean updateCard(CardsDto cardsDto) {
        CardsEntity cardsEntity = cardsRepository.findByCardNumber(cardsDto.getCardNumber()).orElseThrow(
                () -> new ResourceNotFoundException("Card", "cardNumber", cardsDto.getCardNumber())
        );
        cardsRepository.save(CardsMapper.mapToCardsEntity(cardsDto, cardsEntity));
        return true;
    }

    @Override
    public boolean deleteCard(String mobileNumber) {
        CardsEntity cardsEntity = cardsRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Card", "mobileNumber", mobileNumber)
        );
        cardsRepository.deleteById(cardsEntity.getCardId());
        return true;
    }
}
