package com.hackathon.controller;

import com.hackathon.model.Transaction;
import com.hackathon.repository.TransactionRepo;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@AllArgsConstructor
public class TransactionController {
    private TransactionRepo transactionRepo;

    @GetMapping("transaction")
    public Iterable<Transaction> getTransaction() {
        return transactionRepo.findAll();
    }
}

