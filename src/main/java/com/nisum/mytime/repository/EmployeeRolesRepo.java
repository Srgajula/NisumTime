package com.nisum.mytime.repository;

import java.io.Serializable;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.nisum.mytime.model.EmployeeRoles;

public interface EmployeeRolesRepo extends MongoRepository<EmployeeRoles, Serializable> {
}
