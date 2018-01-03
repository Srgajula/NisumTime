package com.nisum.mytime.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.nisum.mytime.model.Designation;

public interface DesignationRepo extends MongoRepository<Designation, String> {
	
} 