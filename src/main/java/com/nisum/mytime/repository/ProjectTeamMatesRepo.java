package com.nisum.mytime.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.nisum.mytime.model.EmployeeRoles;
import com.nisum.mytime.model.Project;
import com.nisum.mytime.model.ProjectTeamMate;

public interface ProjectTeamMatesRepo extends MongoRepository<ProjectTeamMate, String> {
	
	List<ProjectTeamMate> findByProjectId(String projectId);
	List<ProjectTeamMate> findByManagerId(String projectId);
	
}
