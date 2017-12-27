package com.nisum.mytime.model;

import java.io.Serializable;

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
public class AttendenceData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7656364713943754178L;

	private String employeeId;
	private String employeeName;
	private String ifPresent;
	private int totalPresent;
	private int totalAbsent;

}
