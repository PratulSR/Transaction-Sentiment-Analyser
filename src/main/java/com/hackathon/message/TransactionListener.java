package com.hackathon.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackathon.model.Transaction;
import com.hackathon.processor.TransactionProcessor;
import com.hackathon.repository.TransactionRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class TransactionListener {

    private TransactionRepo transactionRepo;
    private ObjectMapper objectMapper;

    @JmsListener(destination = "transactionListener")
    public void processMessage(String content) throws Exception{
        log.info("Processing " + content);
        var transaction = objectMapper.readValue(content, Transaction.class);

//        log.info("BEFORE : " + transaction.getSentiment());

        // Add a Service here

        transaction = TransactionProcessor.filterDescription(transaction);

        log.info("AFTER : " + transaction.getDescription().toLowerCase().contains("desc"));

        transactionRepo.save(transaction);
    }

}
