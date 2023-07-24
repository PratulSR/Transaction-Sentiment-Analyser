package com.hackathon.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionMega {
    private String transactionId;
    private String fromAccount;
    private String toAccount;
    private BigDecimal amount;
    private String description;
    private String descriptionCensored;
    private String sentimentScore;

}
