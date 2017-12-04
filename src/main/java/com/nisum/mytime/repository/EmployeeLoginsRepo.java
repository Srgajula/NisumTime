package com.nisum.mytime.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.nisum.mytime.model.EmpLoginData;

public interface EmployeeLoginsRepo extends MongoRepository<EmpLoginData, Long> {

}
