package com.nisum.mytime.controller;

import java.util.ArrayList;
import java.util.Date;
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
import com.nisum.mytime.model.EmployeeRoles;
import com.nisum.mytime.model.Project;
import com.nisum.mytime.model.ProjectTeamMate;
import com.nisum.mytime.service.ProjectService;
import com.nisum.mytime.service.UserService;

@RestController
@RequestMapping("/projectTeam")
public class ProjectTeamController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private ProjectService projectService;

	@RequestMapping(value = "/employee", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<EmployeeRoles> getEmployeeRole(@RequestParam("emailId") String emailId)
			throws MyTimeException {
		EmployeeRoles employeesRole = userService.getEmployeesRole(emailId);
		return new ResponseEntity<>(employeesRole, HttpStatus.OK);
	}

	@RequestMapping(value = "/addProject", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Project> addProject(@RequestBody Project employeeRoles) throws MyTimeException {
		Project project = projectService.addProject(employeeRoles);
		return new ResponseEntity<>(project, HttpStatus.OK);
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

	@RequestMapping(value = "/getEmployeeRoleData", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<EmployeeRoles> getEmployeeRoleData(@RequestParam("empId") String empId)
			throws MyTimeException {
		EmployeeRoles employeesRole = userService.getEmployeesRoleData(empId);
		return new ResponseEntity<>(employeesRole, HttpStatus.OK);
	}

	@RequestMapping(value = "/getEmployeesToTeam", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<EmployeeRoles>> getManagers() throws MyTimeException {
		List<EmployeeRoles> employeesRoles = new ArrayList<>();
		if(userService.getEmployeeRoles()!=null) {
			employeesRoles=userService.getEmployeeRoles().stream().sorted((o1, o2)->o1.getEmployeeName().
                compareTo(o2.getEmployeeName())).
                collect(Collectors.toList());
		}
		return new ResponseEntity<>(employeesRoles, HttpStatus.OK);
	}

	@RequestMapping(value = "/getTeamDetails", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<ProjectTeamMate>> getTeamDetails(@RequestParam("employeeId") String employeeId)
			throws MyTimeException {
		List<ProjectTeamMate> employeesRoles = projectService.getTeamDetails(employeeId);
		return new ResponseEntity<>(employeesRoles, HttpStatus.OK);
	}

	@RequestMapping(value = "/addEmployeeToTeam", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ProjectTeamMate> addEmployeeToTeam(@RequestBody ProjectTeamMate teamMate)
			throws MyTimeException {
		teamMate.setActive(true);
		teamMate.setStartDate(new Date());
		ProjectTeamMate teamMateDB = projectService.addProjectTeamMate(teamMate);
		return new ResponseEntity<>(teamMateDB, HttpStatus.OK);
	}

	@RequestMapping(value = "/updateTeammate", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ProjectTeamMate> updateTeammate(@RequestBody ProjectTeamMate projectTeamMate)
			throws MyTimeException {
		ProjectTeamMate updatedTeammate = projectService.updateTeammate(projectTeamMate);
		return new ResponseEntity<>(updatedTeammate, HttpStatus.OK);
	}

	@RequestMapping(value = "/deleteTeammate", method = RequestMethod.POST,produces = MediaType.TEXT_PLAIN_VALUE,  consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> deleteTeammate(@RequestBody ProjectTeamMate projectTeamMate) throws MyTimeException {
		projectService.deleteTeammate(projectTeamMate.getEmployeeId(), projectTeamMate.getProjectId(),projectTeamMate.getId());
		return new ResponseEntity<>("Success", HttpStatus.OK);
	}

	@RequestMapping(value = "/getProjects", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Project>> getProjects(@RequestParam("employeeId") String employeeId)
			throws MyTimeException {
		List<Project> projects = projectService.getProjects(employeeId);
		return new ResponseEntity<>(projects, HttpStatus.OK);
	}

	@RequestMapping(value = "/getMyTeamDetails", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<ProjectTeamMate>> getMyTeamDetails(@RequestParam("employeeId") String employeeId)
			throws MyTimeException {
		List<ProjectTeamMate> employeesRoles = projectService.getMyTeamDetails(employeeId);
		return new ResponseEntity<>(employeesRoles, HttpStatus.OK);
	}

	@RequestMapping(value = "/getUnAssignedEmployees", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<EmployeeRoles>> getUnAssignedEmployees() throws MyTimeException {
		List<EmployeeRoles> employeesRoles = projectService.getUnAssignedEmployees();
		return new ResponseEntity<>(employeesRoles, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/getShiftDetails", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<ProjectTeamMate>> getShiftDetails(@RequestParam("shift") String shift)
			throws MyTimeException {
		List<ProjectTeamMate> employeesRoles = projectService.getShiftDetails(shift);
		return new ResponseEntity<>(employeesRoles, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/getProjectAllocations", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<ProjectTeamMate>> getProjectAllocations()
			throws MyTimeException {
		List<ProjectTeamMate> employeesRoles = projectService.getAllProjectDetails();
		return new ResponseEntity<>(employeesRoles, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/getProjectDetails", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<ProjectTeamMate>> getProjectDetails(@RequestParam("projectId") String projectId)
			throws MyTimeException {
		List<ProjectTeamMate> employeesRoles = projectService.getProjectDetails(projectId);
		return new ResponseEntity<>(employeesRoles, HttpStatus.OK);
	}
	@RequestMapping(value = "/getMyProjectAllocations", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<ProjectTeamMate>> getMyProjectAllocations(@RequestParam("employeeId") String employeeId)
			throws MyTimeException {
		List<ProjectTeamMate> projectAllocations = projectService.getMyProjectAllocations(employeeId);
		return new ResponseEntity<>(projectAllocations, HttpStatus.OK);
	}
}