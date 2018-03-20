package com.nisum.mytime.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.nisum.mytime.model.EmployeeVisa;

public interface EmployeeVisaRepo extends MongoRepository<EmployeeVisa, String> {
	List<EmployeeVisa> findByVisaName(String visaName);
}