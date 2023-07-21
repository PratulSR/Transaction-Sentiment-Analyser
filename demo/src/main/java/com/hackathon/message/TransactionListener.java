package com.hackathon.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackathon.model.Transaction;
import com.hackathon.sentiment.SentimentAnalysisService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class TransactionListener {

    private final ObjectMapper objectMapper;
    private final SentimentAnalysisService sentimentAnalysisService;

    @JmsListener(destination = "transactionListener")
    public void processMessage(String content) throws Exception {
        log.info("Processing" + content);
        var transaction = objectMapper.readValue(content, Transaction.class);

        sentimentAnalysisService.processTransaction(transaction);
    }
}
