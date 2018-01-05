package com.nisum.mytime.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.nisum.mytime.model.Account;

public interface AccountRepo extends MongoRepository<Account, String> {
	
	Account findByAccountName(String accontName);
} 