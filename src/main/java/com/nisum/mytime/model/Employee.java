package com.nisum.mytime.model;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "Employee")
public class Employee implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Id
	private String empId;
	private String empName;
	private String empDesg;
	private String empDept;
	
}
