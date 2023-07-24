package com.hackathon.model;

import lombok.Data;
@Data
public class TransactionNegative {
    private String transactionId;
    private String description;
    private String sentimentScore;

    public TransactionNegative(String transactionId, String description, String sentimentScore) {
        this.transactionId = transactionId;
        this.description = description;
        this.sentimentScore = sentimentScore;
    }
}
