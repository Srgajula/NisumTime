package com.nisum.mytime.controller;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nisum.mytime.exception.handler.MyTimeException;
import com.nisum.mytime.model.Account;
import com.nisum.mytime.model.Designation;
import com.nisum.mytime.model.EmployeeRoles;
import com.nisum.mytime.model.Shift;
import com.nisum.mytime.model.Skill;
import com.nisum.mytime.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/employee", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<EmployeeRoles> getEmployeeRole(@RequestParam("emailId") String emailId)
			throws MyTimeException {
		EmployeeRoles employeesRole = userService.getEmployeesRole(emailId);
		return new ResponseEntity<>(employeesRole, HttpStatus.OK);
	}

	@RequestMapping(value = "/assignEmployeeRole", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<EmployeeRoles> assigingEmployeeRole(@RequestBody EmployeeRoles employeeRoles)
			throws MyTimeException {
		EmployeeRoles employeeRole = userService.assigingEmployeeRole(employeeRoles);
		return new ResponseEntity<>(employeeRole, HttpStatus.OK);
	}

	@RequestMapping(value = "/updateEmployeeRole", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<EmployeeRoles> updateEmployeeRole(@RequestBody EmployeeRoles employeeRoles)
			throws MyTimeException {
		EmployeeRoles employeeRole = userService.updateEmployeeRole(employeeRoles);
		return new ResponseEntity<>(employeeRole, HttpStatus.OK);
	}

	@RequestMapping(value = "/deleteEmployee", method = RequestMethod.DELETE, produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> deleteEmployee(@RequestParam("empId") String empId) throws MyTimeException {
		userService.deleteEmployee(empId);
		return new ResponseEntity<>("Success", HttpStatus.OK);
	}

	@RequestMapping(value = "/getUserRoles", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<EmployeeRoles>> getUserRoles() throws MyTimeException {
		List<EmployeeRoles> employeesRoles = userService.getEmployeeRoles();
		return new ResponseEntity<>(employeesRoles, HttpStatus.OK);
	}

	@RequestMapping(value = "/getEmployeeRoleData", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<EmployeeRoles> getEmployeeRoleData(@RequestParam("empId") String empId)
			throws MyTimeException {
		EmployeeRoles employeesRole = userService.getEmployeesRoleData(empId);
		return new ResponseEntity<>(employeesRole, HttpStatus.OK);
	}

	@RequestMapping(value = "/getManagers", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<EmployeeRoles>> getManagers() throws MyTimeException {
		List<EmployeeRoles> employeesRoles = userService.getEmployeeRoles();
		List<EmployeeRoles> managers = employeesRoles
				.stream().filter(e -> ("Manager".equalsIgnoreCase(e.getRole())
						|| "HR Manager".equalsIgnoreCase(e.getRole()) || "Lead".equalsIgnoreCase(e.getRole())))
				.sorted(Comparator.comparing(EmployeeRoles::getEmployeeName)).collect(Collectors.toList());
		return new ResponseEntity<>(managers, HttpStatus.OK);
	}

	@RequestMapping(value = "/getAllShifts", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<String>> getAllShifts() throws MyTimeException {
		List<String> shifts = userService.getAllShifts().stream().filter(e -> "Y".equalsIgnoreCase(e.getActiveStatus()))
				.map(Shift::getShiftName).sorted().collect(Collectors.toList());
		return new ResponseEntity<>(shifts, HttpStatus.OK);
	}

	@RequestMapping(value = "/getAllDesignations", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<String>> getAllDesignations() throws MyTimeException {
		List<String> designations = userService.getAllDesignations().stream()
				.filter(e -> "Y".equalsIgnoreCase(e.getActiveStatus())).map(Designation::getDesignationName).sorted()
				.collect(Collectors.toList());
		return new ResponseEntity<>(designations, HttpStatus.OK);
	}

	@RequestMapping(value = "/getSkills", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<String>> getTechnologies() throws MyTimeException {
		List<String> technologies = userService.getTechnologies().stream()
				.filter(e -> "Y".equalsIgnoreCase(e.getActiveStatus())).map(Skill::getSkillName).sorted()
				.collect(Collectors.toList());
		return new ResponseEntity<>(technologies, HttpStatus.OK);

	}

	@RequestMapping(value = "/updateProfile", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<EmployeeRoles> updateProfile(@RequestBody EmployeeRoles employeeRoles)
			throws MyTimeException {
		EmployeeRoles employeeDB = userService.getEmployeesRoleData(employeeRoles.getEmployeeId());
		employeeDB.setMobileNumber(employeeRoles.getMobileNumber());
		employeeDB.setAlternateMobileNumber(employeeRoles.getAlternateMobileNumber());
		employeeDB.setPersonalEmailId(employeeRoles.getPersonalEmailId());
		employeeDB.setBaseTechnology(employeeRoles.getBaseTechnology());
		employeeDB.setTechnologyKnown(employeeRoles.getTechnologyKnown());
		EmployeeRoles employeeRole = userService.updateProfile(employeeDB);
		return new ResponseEntity<>(employeeRole, HttpStatus.OK);
	}

	@RequestMapping(value = "/getAccounts", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<String>> getAccounts() throws MyTimeException {
		List<String> technologies = userService.getAccounts().stream()
				.filter(e -> "Y".equalsIgnoreCase(e.getStatus())).map(Account::getAccountName).sorted()
				.collect(Collectors.toList());
		System.out.println("technologies"+technologies);
		return new ResponseEntity<>(technologies, HttpStatus.OK);
}
}