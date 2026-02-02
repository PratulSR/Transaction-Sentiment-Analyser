package com.hackathon.controller;

import com.hackathon.model.Transaction;
import com.hackathon.model.TransactionNegative;
import com.hackathon.processor.TransactionProcessor;
import com.hackathon.repository.TransactionNegativeRepo;
import com.hackathon.repository.TransactionRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Slf4j
public class TransactionController {
    private TransactionNegativeRepo transactionRepo;

    @GetMapping("transaction")
    public Iterable<TransactionNegative> getTransactions(){

        return transactionRepo.findAll();
    }

    @PostMapping("transaction/publish")
    public Transaction cleanTransaction(@RequestBody Transaction transaction) throws Exception {
        log.info("Processing Direct : " + transaction.getTransactionId());
        transaction.setDescription(TransactionProcessor.filterDescription(transaction.getDescription())[0]);
        return transaction;
    }


}
