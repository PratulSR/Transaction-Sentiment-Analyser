package com.hackathon.processor;

import com.hackathon.model.Transaction;

// Imports the Google Cloud client library
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.Document.Type;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ErrorHandler;

@Slf4j
public class TransactionProcessor {

    public static Transaction filterDescription(Transaction transaction) throws Exception {
        transaction.setSentiment("Sample Sentiment");

        // Instantiates a client
        try (LanguageServiceClient language = LanguageServiceClient.create()) {

            // The text to analyze
            String text = transaction.getDescription();
            Document doc = Document.newBuilder().setContent(text).setType(Type.PLAIN_TEXT).build();

            // Detects the sentiment of the text
            Sentiment sentiment = language.analyzeSentiment(doc).getDocumentSentiment();

            System.out.printf("Text: %s%n", text);
            System.out.printf("Sentiment: %s, %s%n", sentiment.getScore(), sentiment.getMagnitude());
        }

        return transaction;
    }

    @Service
    public class SomeHandler implements ErrorHandler {
        @Override
        public void handleError(Throwable t) {
            log.error("Error in listener", t);
        }
    }
}
