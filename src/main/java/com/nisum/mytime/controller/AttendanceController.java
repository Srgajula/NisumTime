package com.nisum.mytime.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nisum.mytime.exception.handler.MyTimeException;
import com.nisum.mytime.model.EmpLoginData;
import com.nisum.mytime.service.UserService;

@RestController
@RequestMapping("/attendance")
public class AttendanceController {

	@Autowired
	private UserService userService;

	@RequestMapping(value = "employeeLoginsBasedOnDate", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Map<List<EmpLoginData>, String>> employeeLoginsBasedOnDate(@RequestParam("empId") long id,
			@RequestParam("fromDate") String fromDate, @RequestParam("toDate") String toDate) throws MyTimeException {
		Map<List<EmpLoginData>, String> message = userService.employeeLoginsBasedOnDate(id, fromDate, toDate);
		return new ResponseEntity<>(message, HttpStatus.OK);
	}

	@RequestMapping(value = "generatePdfReport/{id}/{fromDate}/{toDate}", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> generatePdfReport(@PathVariable("id") long id,
			@PathVariable("fromDate") String fromDate, @PathVariable("toDate") String toDate) throws MyTimeException {
		String result = userService.generatePdfReport(id, fromDate, toDate);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

}