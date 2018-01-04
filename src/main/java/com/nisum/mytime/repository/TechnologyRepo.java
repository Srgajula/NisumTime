package com.nisum.mytime.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.nisum.mytime.model.Skill;


public interface TechnologyRepo extends MongoRepository<Skill, String> {
	
} 