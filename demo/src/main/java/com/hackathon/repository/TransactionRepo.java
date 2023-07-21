package com.hackathon.repository;

import com.hackathon.model.Transaction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

@Component
public interface TransactionRepo extends CrudRepository<Transaction, String> {
}
