package com.nisum.mytime.model;

import java.io.Serializable;
import java.util.List;

import org.bson.types.ObjectId;
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
@Document(collection = "Projects")
public class Project implements Serializable {


	private static final long serialVersionUID = 1L;

	@Id
	private ObjectId id;
	private String projectId;
	private String projectName;
	private String managerId;
	private String managerName;
	private List<String> employeeIds;

}
