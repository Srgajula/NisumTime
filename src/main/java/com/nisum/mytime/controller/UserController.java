package com.nisum.mytime.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.nisum.mytime.model.EmpLoginData;
import com.nisum.mytime.service.UserService;

@RestController
@RequestMapping("/")
public class UserController {

	@Autowired
	private UserService userService;

	@RequestMapping(value = "employee/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<EmpLoginData>> fetchEmployeeDataBasedOnEmpId(@PathVariable("id") long id)
			throws FileNotFoundException, ParseException {
		List<EmpLoginData> empLoginData = userService.fetchEmployeeDataBasedOnEmpId(id);
		return new ResponseEntity<List<EmpLoginData>>(empLoginData, HttpStatus.OK);
	}

	@RequestMapping(value = "employeesDataSave/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<EmpLoginData>> fetchEmployeesData() throws ParseException, IOException {
		List<EmpLoginData> message = userService.fetchEmployeesData();
		return new ResponseEntity<List<EmpLoginData>>(message, HttpStatus.OK);
	}

}