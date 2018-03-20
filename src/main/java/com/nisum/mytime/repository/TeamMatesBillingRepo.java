package com.nisum.mytime.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.nisum.mytime.model.TeamMateBilling;

public interface TeamMatesBillingRepo extends MongoRepository<TeamMateBilling, String> {

	List<TeamMateBilling> findByProjectId(String projectId);

	List<TeamMateBilling> findByEmployeeId(String employeeId);
	
	TeamMateBilling findById(ObjectId id);
	
	List<TeamMateBilling>  findByEmployeeIdAndProjectId(String employeeId, String projectId);

}
