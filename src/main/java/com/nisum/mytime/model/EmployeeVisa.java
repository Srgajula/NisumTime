package com.nisum.mytime.model;

import java.util.Date;

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
@Document(collection = "EmployeeVisa")
public class EmployeeVisa {
	@Id
	private ObjectId id;
	private String employeeId;
	private String employeeName;
	private String visaNo;
	private String visaName;
	private String visaCountry;
	private Date visaIntiatedDate;
	private Date  approvedDate;
	private Date visaExpiryDate;
	private String status;
	private String comments;
}
