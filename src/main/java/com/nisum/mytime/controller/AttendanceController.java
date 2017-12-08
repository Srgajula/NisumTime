package com.nisum.mytime.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.nisum.mytime.exception.handler.MyTimeException;
import com.nisum.mytime.model.EmpLoginData;
import com.nisum.mytime.service.UserService;

@RestController
@RequestMapping("/attendance")
public class AttendanceController {

	@Autowired
	private UserService userService;

	@RequestMapping(value = "employee/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<EmpLoginData>> fetchEmployeeDataBasedOnEmpId(@PathVariable("id") long id)
			throws MyTimeException {
		List<EmpLoginData> empLoginData = userService.fetchEmployeeDataBasedOnEmpId(id);
		return new ResponseEntity<List<EmpLoginData>>(empLoginData, HttpStatus.OK);
	}

	@RequestMapping(value = "employeeLoginsBasedOnDate/{id}/{fromDate}/{toDate}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<EmpLoginData>> employeeLoginsBasedOnDate(@PathVariable("id") long id,
			@PathVariable("fromDate") String fromDate, @PathVariable("toDate") String toDate) throws MyTimeException {
		List<EmpLoginData> message = userService.employeeLoginsBasedOnDate(id, fromDate, toDate);
		return new ResponseEntity<List<EmpLoginData>>(message, HttpStatus.OK);
	}
	
	@RequestMapping(value = "generatePdfReport/{id}/{fromDate}/{toDate}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> generatePdfReport(@PathVariable("id") long id,
			@PathVariable("fromDate") String fromDate, @PathVariable("toDate") String toDate)
			throws MyTimeException {
		Boolean result= userService.generatePdfReport(id, fromDate, toDate);
		return new ResponseEntity<Boolean>(result, HttpStatus.OK);
	}

}