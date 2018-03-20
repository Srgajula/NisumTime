package com.nisum.mytime.model;

import java.util.Date;

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
public class EmployeeDashboardVO {
	private String employeeId;
	private String employeeName;
	private String emailId;
	private String baseTechnology;
	private String technologyKnown;
	private String alternateMobileNumber;
	private String personalEmailId;
	private Date createdOn;
	private Date lastModifiedOn;
	private String role;
	private String shift;
	private String projectId;
	private String projectName;
	private String account;
	private String managerId;
	private String managerName;
	private String experience;
	private String designation;
	private String billableStatus;
	private String mobileNumber;
	@DateTimeFormat(iso = ISO.DATE)
	private Date startDate;
	@DateTimeFormat(iso = ISO.DATE)
	private Date endDate;
	private boolean active;
	private boolean projectAssigned;
	private boolean hasB1Visa;
	private boolean hasH1Visa;
	private boolean hasPassport;
}
