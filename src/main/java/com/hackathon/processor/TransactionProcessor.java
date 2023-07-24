package com.hackathon.processor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackathon.controller.SentimentAnalysisService;
import com.hackathon.model.Transaction;

// Imports the Google Cloud client library
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.Document.Type;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
import com.hackathon.model.TransactionMega;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ErrorHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
public class TransactionProcessor {

    private static Set<String> badWords;

    public static String[] filterDescription(String text) throws Exception {

        Double sentimentScore = 0.0;
        badWords = loadBadWords();

        // Instantiates a client
        try (LanguageServiceClient language = LanguageServiceClient.create()) {

            // The text to analyze
            Document doc = Document.newBuilder().setContent(text).setType(Type.PLAIN_TEXT).build();

            // Detects the sentiment of the text
            Sentiment sentiment = language.analyzeSentiment(doc).getDocumentSentiment();

            System.out.printf("Text: %s%n", text);
            System.out.printf("Sentiment: %s, %s%n", sentiment.getScore(), sentiment.getMagnitude());

            sentimentScore = (double) (sentiment.getScore() * sentiment.getMagnitude());

            if (sentimentScore < 0){
                // Censor the description
                String[] words = text.split("\\s+");
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

                text = filteredDescription.toString().trim();
            }
        }

        return new String[]{text, String.valueOf(sentimentScore)};
    }

    @Service
    public class SomeHandler implements ErrorHandler {
        @Override
        public void handleError(Throwable t) {
            log.error("Error in listener", t);
        }
    }

    private static Set<String> loadBadWords() {
        Set<String> badWordsSet = new HashSet<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            InputStream resource = TransactionProcessor.class.getResourceAsStream("/sentiment/bad_words.json");
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
}
