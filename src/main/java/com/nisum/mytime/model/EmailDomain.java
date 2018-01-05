package com.nisum.mytime.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class EmailDomain {
	private String empId;
	private String fromDate;
	private String toDate;
	private String[] toEmail;
	private String[] ccEmail;
	private String[] bccEmail;
}
