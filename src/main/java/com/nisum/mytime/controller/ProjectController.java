package com.nisum.mytime.controller;

import java.util.List;

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
import com.nisum.mytime.model.EmployeeRoles;
import com.nisum.mytime.model.Project;
import com.nisum.mytime.repository.AccountRepo;
import com.nisum.mytime.service.ProjectService;
import com.nisum.mytime.service.UserService;

@RestController
@RequestMapping("/project")
public class ProjectController {

	@Autowired
	private UserService userService;
	@Autowired
	private ProjectService projectService;
	@Autowired
	private AccountRepo accountRepo;
	@RequestMapping(value = "/employee", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<EmployeeRoles> getEmployeeRole(@RequestParam("emailId") String emailId)
			throws MyTimeException {
		EmployeeRoles employeesRole = userService.getEmployeesRole(emailId);
		return new ResponseEntity<>(employeesRole, HttpStatus.OK);
	}

	@RequestMapping(value = "/addProject", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Project> addProject(@RequestBody Project projectAdded) throws MyTimeException {
		    String accountName=projectAdded.getAccount();
		   Account account= accountRepo.findByAccountName(accountName);
		  int sequenceNumber= account.getAccountProjectSequence();
		       account.setAccountProjectSequence(sequenceNumber+1);
		       accountRepo.save(account);
		     String projectId=  accountName+String.format("%04d", sequenceNumber+1);
		     projectAdded.setProjectId(projectId);
		Project project = projectService.addProject(projectAdded);
		System.out.println(project.getProjectId());
		return new ResponseEntity<>(project, HttpStatus.OK);
	}

	@RequestMapping(value = "/updateProject", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Project> updateEmployeeRole(@RequestBody Project project) throws MyTimeException {
		Project updatedProject = projectService.updateProject(project);
		return new ResponseEntity<>(updatedProject, HttpStatus.OK);
	}

	@RequestMapping(value = "/deleteProject", method = RequestMethod.DELETE, produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> deleteProject(@RequestParam("projectId") String projectId) throws MyTimeException {
		projectService.deleteProject(projectId);
		return new ResponseEntity<>("Success", HttpStatus.OK);
	}

	@RequestMapping(value = "/getProjects", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Project>> getProjects() throws MyTimeException {
		List<Project> projects = projectService.getProjects();
		return new ResponseEntity<>(projects, HttpStatus.OK);
	}

	@RequestMapping(value = "/getEmployeeRoleData", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<EmployeeRoles> getEmployeeRoleData(@RequestParam("empId") String empId)
			throws MyTimeException {
		EmployeeRoles employeesRole = userService.getEmployeesRoleData(empId);
		return new ResponseEntity<>(employeesRole, HttpStatus.OK);
	}
	
}