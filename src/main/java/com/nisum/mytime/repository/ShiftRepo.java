package com.nisum.mytime.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.nisum.mytime.model.Shift;

public interface ShiftRepo extends MongoRepository<Shift, String> {
	
} 