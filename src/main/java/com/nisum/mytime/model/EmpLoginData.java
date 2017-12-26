package com.nisum.mytime.model;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Document(collection = "EmployeesLoginData")
public class EmpLoginData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8448056441155775579L;

	@Id
	private String id;
	private String employeeId;
	private String employeeName;
	private String dateOfLogin;
	private String firstLogin;
	private String lastLogout;
	private String totalLoginTime;
	private String direction;
	private String totalAvgTime;

}
