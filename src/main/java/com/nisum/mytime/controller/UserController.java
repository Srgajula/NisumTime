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
import com.nisum.mytime.model.EmployeeRoles;
import com.nisum.mytime.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;

	@RequestMapping(value = "employee/{emailId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<EmployeeRoles>> getEmployeeRole(@PathVariable("emailId") String emailId) throws MyTimeException {
		List<EmployeeRoles> employeesRoles = userService.getEmployeesRole();
		return new ResponseEntity<>(employeesRoles, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/employeesDataSave", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> employeesDataSave() throws MyTimeException {
		Boolean result = userService.fetchEmployeesData();
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/assigingEmployeeRole", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> assigingEmployeeRole(EmployeeRoles employeeRoles) throws MyTimeException {
		userService.assigingEmployeeRole(employeeRoles);
		return new ResponseEntity<>("", HttpStatus.OK);
	}

}