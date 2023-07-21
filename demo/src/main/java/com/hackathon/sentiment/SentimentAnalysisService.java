//package com.hackathon.sentiment;
//
//import com.hackathon.model.Transaction;
//import com.hackathon.repository.TransactionRepo;
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//@Slf4j
//@AllArgsConstructor
//@Service
//public class SentimentAnalysisService {
//
//    private final TransactionRepo transactionRepo;
//
//    public String performSentimentAnalysis(String description) {
//        if (description.toLowerCase().contains("offensive")) {
//            return "Negative";
//        } else {
//            return "Positive";
//        }
//    }
//
//    public void processTransaction(Transaction transaction) {
//        log.info("Processing Transaction: {}", transaction.getTransactionId());
//
//        String sentiment = performSentimentAnalysis(transaction.getDescription());
//        transaction.setSentiment(sentiment);
//
//        transactionRepo.save(transaction);
//    }
//}




package com.hackathon.sentiment;

import com.hackathon.model.Transaction;
import com.hackathon.repository.TransactionRepo;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap; // Import CoreMap
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@AllArgsConstructor
@Service
public class SentimentAnalysisService {

    private final TransactionRepo transactionRepo;
    private final StanfordCoreNLP pipeline;
    private final List<String> censorWordList; // Predefined list of censor words
    private final Map<String, Integer> censorWordRatingMap;

    public String performSentimentAnalysis(String description) {
        if (description.toLowerCase().contains("censor") || description.toLowerCase().contains("offensive")) {
            return "Negative";
        } else {
            return "Positive";
        }
    }

    // Determine the sentiment rating based on the sentiment analysis result and censor word scoring
    public int determineSentimentRating(String sentiment, String description) {
        int rating = sentiment.equalsIgnoreCase("Negative") ? 1 : 5;

        // Calculate censor word rating based on Levenshtein distance or similarity
        int censorRating = calculateCensorWordRating(description);
        rating += censorRating;

        return rating;
    }

    // Method to calculate the rating of censor words based on Levenshtein distance or similarity
    private int calculateCensorWordRating(String description) {
        int totalRating = 0;

        // Perform POS tagging using Stanford NLP
        Annotation annotation = new Annotation(description);
        pipeline.annotate(annotation);

        // Retrieve the list of sentences from the annotation
        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);

        for (CoreMap sentence : sentences) {
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                String word = token.get(CoreAnnotations.TextAnnotation.class);
                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);

                // Check if the word is a noun, verb, or adjective (likely sensitive content)
                if (isCensorWordPOS(pos)) {
                    int wordRating = calculateWordRating(word);
                    totalRating += wordRating;
                }
            }
        }

        return totalRating;
    }

    // Method to check if a word matches POS associated with censor words
    private boolean isCensorWordPOS(String pos) {
        // Modify the POS as per your requirements to include words associated with sensitive content
        return pos.startsWith("NN") || pos.startsWith("VB") || pos.startsWith("JJ");
    }

    // Method to calculate the rating of a single word based on Levenshtein distance or similarity
    private int calculateWordRating(String word) {
        int wordRating = 0;

        // Check if the word is present in the censorWordRatingMap
        if (censorWordRatingMap.containsKey(word)) {
            wordRating = censorWordRatingMap.get(word);
        }

        return wordRating;
    }

    // Method to calculate Levenshtein distance between two words
    private int calculateLevenshteinDistance(String word1, String word2) {
        // Implement Levenshtein distance calculation here
        // ...

        return 0;
    }

    // Method to calculate similarity between two words
    private int calculateSimilarity(String word1, String word2) {
        // Implement similarity calculation here
        // ...

        return 0;
    }

    public void processTransaction(Transaction transaction) {
        log.info("Processing Transaction: {}", transaction.getTransactionId());

        // Perform sentiment analysis on the description
        String sentiment = performSentimentAnalysis(transaction.getDescription().toString());
        transaction.setSentiment(sentiment);

        // Determine the sentiment rating based on the sentiment analysis result and censor word scoring
        int sentimentRating = determineSentimentRating(sentiment, transaction.getDescription().toString());
        transaction.setSentimentRating(sentimentRating);

        transactionRepo.save(transaction); // Save the updated transaction to the repository
    }
}
