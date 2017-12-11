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
@Document(collection = "EmployeeRoles")
public class EmployeeRoles implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2685951572250975416L;

	@Id
	private String id;
	private String employeeId;
	private String employeeName;
	private String emailId;
	private String role;

}
