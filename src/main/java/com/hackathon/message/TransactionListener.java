package com.hackathon.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackathon.model.Transaction;
import com.hackathon.model.TransactionMega;
import com.hackathon.model.TransactionNegative;
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

        var transactionMega = objectMapper.readValue(content, TransactionMega.class);
        var transactionNegative = objectMapper.readValue(content, TransactionNegative.class);

        String[] results = TransactionProcessor.filterDescription(transactionMega.getDescription());
        transactionMega.setDescriptionCensored(results[0]);
        transactionMega.setSentimentScore(results[1]);

        transactionNegative.setSentimentScore(transactionMega.getSentimentScore());

        if (Double.parseDouble(transactionNegative.getSentimentScore()) < 0){
            transactionRepo.save(transactionNegative);
        }

    }

}
