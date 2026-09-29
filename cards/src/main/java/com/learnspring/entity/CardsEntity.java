package com.learnspring.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity @Data
@Table(name="cards")
public class CardsEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cardId;

    private String mobileNumber;

    private String cardNumber;

    private String cardType;

    private int totalLimit;

    private int amountUsed;

    private int availableAmount;
}
