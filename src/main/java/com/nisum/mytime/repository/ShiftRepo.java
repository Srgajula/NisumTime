package com.nisum.mytime.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.nisum.mytime.model.Shift;

public interface ShiftRepo extends MongoRepository<Shift, String> {
	
} 