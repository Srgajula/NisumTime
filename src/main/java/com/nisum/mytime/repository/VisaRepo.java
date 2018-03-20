package com.nisum.mytime.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.nisum.mytime.model.Visa;

public interface VisaRepo extends MongoRepository<Visa, String> {
	
} 