package com.hackathon.processor;

import com.hackathon.model.Transaction;

public class TransactionProcessor {

    public static Transaction filterDescription(Transaction transaction){
        transaction.setSentiment("Sample Sentiment");
        return transaction;
    }
}
