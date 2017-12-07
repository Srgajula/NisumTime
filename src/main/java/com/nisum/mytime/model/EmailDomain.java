package com.nisum.mytime.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EmailDomain {
	private String[] toEmail;
	private String[] ccEmail;
	private String[] bccEmail;
	private String empId;
	private String fromDate;
	private String toDate;
}
