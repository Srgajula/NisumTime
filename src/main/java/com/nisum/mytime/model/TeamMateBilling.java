package com.nisum.mytime.model;

import java.io.Serializable;
import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

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
@Document(collection = "TeamMateBilling")
public class TeamMateBilling implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private ObjectId id;
	private String employeeId;
	private String employeeName;
    private String projectId;
    private String projectName;
	@DateTimeFormat(iso = ISO.DATE)
	private Date billingStartDate;
	@DateTimeFormat(iso = ISO.DATE)
	private Date billingEndDate;
	private String comments;
	private boolean active;
	@DateTimeFormat(iso = ISO.DATE)
	private Date createDate;
	
}
