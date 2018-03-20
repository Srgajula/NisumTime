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
@Document(collection = "TravelRequests")
public class TravelRequest {
	@Id
	private ObjectId id;
	private String employeeId;
	private String employeeName;
	private String visaNo;
	private String visaName;
	private String visaCountry;
	private Date visaExpiryDate;
	private String status;
	private String comments;
	private String fromLocation;
	private String toLocation;
	private String buddy;
	private String account;
	private String project;
	private Date travelDate;
	private Date returnDate;
}
