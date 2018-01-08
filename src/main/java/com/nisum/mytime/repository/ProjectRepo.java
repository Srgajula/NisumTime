package com.nisum.mytime.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.nisum.mytime.model.Project;

public interface ProjectRepo extends MongoRepository<Project, String> {
	
	Project findByProjectId(String projectId);
	
	List<Project> findByManagerId(String managerId);
} 