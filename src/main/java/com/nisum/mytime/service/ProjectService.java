package com.nisum.mytime.service;

import java.util.List;

import com.nisum.mytime.exception.handler.MyTimeException;
import com.nisum.mytime.model.EmpLoginData;
import com.nisum.mytime.model.EmployeeRoles;
import com.nisum.mytime.model.Project;
import com.nisum.mytime.model.ProjectTeamMate;

public interface ProjectService {

	Boolean fetchEmployeesData() throws MyTimeException;

	List<EmpLoginData> employeeLoginsBasedOnDate(long id, String fromDate, String toDate) throws MyTimeException;

	List<Project> getProjects() throws MyTimeException;

	Project addProject(Project project) throws MyTimeException;

	String generatePdfReport(long id, String fromDate, String toDate) throws MyTimeException;

	EmployeeRoles getEmployeesRole(String emailId);

	void deleteProject(String projectId);

	Project updateProject(Project project);

	EmployeeRoles getEmployeesRoleData(String empId);

	List<ProjectTeamMate> getTeamDetails(String empId);

	ProjectTeamMate addProject(ProjectTeamMate project) throws MyTimeException;

	ProjectTeamMate updateTeammate(ProjectTeamMate projectTeamMate);

	void deleteTeammate(String empId, String managerId);

	List<Project> getProjects(String managerId) throws MyTimeException;

	List<ProjectTeamMate> getMyTeamDetails(String empId);

	List<EmployeeRoles> getUnAssignedEmployees();
}
