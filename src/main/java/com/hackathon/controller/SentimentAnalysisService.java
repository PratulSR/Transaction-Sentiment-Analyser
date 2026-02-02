package com.hackathon.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackathon.model.Transaction;
import com.hackathon.repository.TransactionRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@AllArgsConstructor
@Service
public class SentimentAnalysisService {

    private  TransactionRepo transactionRepo;
    private Set<String> badWords;

    public SentimentAnalysisService() {
        this.badWords = loadBadWords();
    }

    private Set<String> loadBadWords() {
        Set<String> badWordsSet = new HashSet<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            InputStream resource = getClass().getResourceAsStream("/sentiment/bad_words.json");
            List<Map<String, Object>> badWordsList = objectMapper.readValue(resource, new TypeReference<>() {});
            for (Map<String, Object> badWordEntry : badWordsList) {
                String badWord = (String) badWordEntry.get("id");
                badWordsSet.add(badWord.toLowerCase());
            }
        } catch (IOException e) {
            log.error("Failed to load bad words from JSON file.", e);
        }
        return badWordsSet;
    }

    public String performSentimentAnalysis(String description) {

        return "positive";
    }

    public void processTransaction(Transaction transaction) {
        log.info("Processing Transaction: {}", transaction.getTransactionId());
        log.info("Processing Transaction: {}", transaction.getDescription());


        String filteredDescription = filterBadWords(transaction.getDescription());
        transaction.setDescription(filteredDescription);
        log.info("Processing Transaction: {}", transaction.getDescription());

        String sentiment = performSentimentAnalysis(filteredDescription);
//        transaction.setSentiment("sentiment");

//        transactionRepo.save(transaction);
    }

    private String filterBadWords(String description) {
        String[] words = description.split("\\s+");
        StringBuilder filteredDescription = new StringBuilder();

        for (String word : words) {
            String lowercaseWord = word.toLowerCase();
            if (badWords.contains(lowercaseWord)) {
                String replacement = "*".repeat(word.length());
                filteredDescription.append(replacement).append(" ");
            } else {
                filteredDescription.append(word).append(" ");
            }
        }

        return filteredDescription.toString().trim();
    }
}
