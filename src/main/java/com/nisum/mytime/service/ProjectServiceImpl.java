package com.nisum.mytime.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.nisum.mytime.exception.handler.MyTimeException;
import com.nisum.mytime.model.EmpLoginData;
import com.nisum.mytime.model.EmployeeRoles;
import com.nisum.mytime.model.Project;
import com.nisum.mytime.model.ProjectTeamMate;
import com.nisum.mytime.repository.EmployeeRolesRepo;
import com.nisum.mytime.repository.ProjectRepo;
import com.nisum.mytime.repository.ProjectTeamMatesRepo;
import com.nisum.mytime.utils.PdfReportGenerator;

@Service("projectService")
public class ProjectServiceImpl implements ProjectService {

	@Autowired
	private EmployeeRolesRepo employeeRolesRepo;

	@Autowired
	private ProjectRepo projectRepo;
	@Autowired
	private ProjectTeamMatesRepo projectTeamMatesRepo;

	@Autowired
	private EmployeeDataService employeeDataBaseService;

	@Autowired
	private PdfReportGenerator pdfReportGenerator;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public List<EmpLoginData> employeeLoginsBasedOnDate(long id, String fromDate, String toDate)
			throws MyTimeException {
		return employeeDataBaseService.fetchEmployeeLoginsBasedOnDates(id, fromDate, toDate);
	}

	@Override
	public String generatePdfReport(long id, String fromDate, String toDate) throws MyTimeException {
		return pdfReportGenerator.generateEmployeeReport(id, fromDate, toDate);
	}

	@Override
	public List<Project> getProjects() throws MyTimeException {
		return projectRepo.findAll();
	}

	@Override
	public Project addProject(Project project) throws MyTimeException {
		return projectRepo.save(project);
	}

	@Override
	public EmployeeRoles getEmployeesRole(String emailId) {
		return employeeRolesRepo.findByEmailId(emailId);

	}

	@Override
	public void deleteProject(String projectId) {
		Project project = projectRepo.findByProjectId(projectId);
		projectRepo.delete(project);
	}

	@Override
	public Project updateProject(Project project) {
		Query query = new Query(Criteria.where("projectId").is(project.getProjectId()));
		Update update = new Update();
		update.set("projectName", project.getProjectName());
		update.set("managerId", project.getManagerId());
		update.set("managerName", project.getManagerName());
		update.set("account", project.getAccount());
		update.set("status", project.getStatus());
		FindAndModifyOptions options = new FindAndModifyOptions();
		options.returnNew(true);
		options.upsert(true);
		Project projectDB= mongoTemplate.findAndModify(query, update, options, Project.class);
		List<ProjectTeamMate> employeeDetails=	projectTeamMatesRepo.findByProjectId(project.getProjectId());
		if(employeeDetails!=null&&projectDB!=null) {
		for (ProjectTeamMate emp : employeeDetails) {
			emp.setManagerId(projectDB.getManagerId());
			emp.setManagerName(projectDB.getManagerName());
			emp.setAccount(projectDB.getAccount());
			emp.setProjectName(projectDB.getProjectName());
			projectTeamMatesRepo.save(emp);
		}
		}
		return projectDB;
	}

	@Override
	public EmployeeRoles getEmployeesRoleData(String employeeId) {
		return employeeRolesRepo.findByEmployeeId(employeeId);
	}

	@Override
	public List<ProjectTeamMate> getTeamDetails(String empId) {
		return projectTeamMatesRepo.findByManagerId(empId);

	}

	@Override
	public ProjectTeamMate addProject(ProjectTeamMate project) throws MyTimeException {
		return projectTeamMatesRepo.save(project);
	}

	@Override
	public ProjectTeamMate updateTeammate(ProjectTeamMate projectTeamMate) {

		ProjectTeamMate existingTeammate = projectTeamMatesRepo
				.findByEmployeeIdAndManagerId(projectTeamMate.getEmployeeId(), projectTeamMate.getManagerId());
		existingTeammate.setProjectId(projectTeamMate.getProjectId());
		existingTeammate.setProjectName(projectTeamMate.getProjectName());
		existingTeammate.setExperience(projectTeamMate.getExperience());
		existingTeammate.setDesignation(projectTeamMate.getDesignation());
		existingTeammate.setBillableStatus(projectTeamMate.getBillableStatus());
		existingTeammate.setMobileNumber(projectTeamMate.getMobileNumber());
		existingTeammate.setShift(projectTeamMate.getShift());
		ProjectTeamMate teamMate= projectTeamMatesRepo.save(existingTeammate);
		try {
		EmployeeRoles employeeDB= employeeRolesRepo.findByEmployeeId(teamMate.getEmployeeId());
		employeeDB.setShift(teamMate.getShift());
		employeeDB.setMobileNumber(teamMate.getMobileNumber());
		employeeRolesRepo.save(employeeDB);
		}
		catch(Exception e) {}
		return teamMate;
	}

	@Override
	public void deleteTeammate(String empId, String managerId) {
		ProjectTeamMate existingTeammate = projectTeamMatesRepo.findByEmployeeIdAndManagerId(empId, managerId);
		projectTeamMatesRepo.delete(existingTeammate);
	}

	@Override
	public List<Project> getProjects(String managerId) throws MyTimeException {
		return projectRepo.findByManagerId(managerId);

	}

	@Override
	public List<ProjectTeamMate> getMyTeamDetails(String empId) {
		List<ProjectTeamMate> teamMates = new ArrayList<>();
		List<ProjectTeamMate> empRecords = projectTeamMatesRepo.findByEmployeeId(empId);
		for (ProjectTeamMate pt : empRecords) {
			teamMates.addAll(projectTeamMatesRepo.findByProjectId(pt.getProjectId()));
		}
		return teamMates;
	}

	@Override
	public List<EmployeeRoles> getUnAssignedEmployees() {
		List<EmployeeRoles> allEmployees = employeeRolesRepo.findAll();
		List<EmployeeRoles> notAssignedEmployees = new ArrayList<>();
		List<String> teamMates = new ArrayList<>();
		List<ProjectTeamMate> empRecords = projectTeamMatesRepo.findAll();
		for (ProjectTeamMate pt : empRecords) {
			Project project= projectRepo.findByProjectId(pt.getProjectId());
			if(project!=null&&project.getStatus()!=null&&!project.getStatus().equalsIgnoreCase("Completed")) {
			teamMates.add(pt.getEmployeeId());
			}
		}
		for (EmployeeRoles emp : allEmployees) {

			if (!teamMates.contains(emp.getEmployeeId())) {
				notAssignedEmployees.add(emp);
			}
		}

		return notAssignedEmployees;
	}
	@Override
	public List<ProjectTeamMate> getShiftDetails(String shift) {
		List<Project> projects = projectRepo.findAll();
		List<ProjectTeamMate> shiftEmpDetails =new ArrayList<>();  //projectTeamMatesRepo.findAll();
		for (Project pt : projects) {
			if(pt.getStatus().equalsIgnoreCase("Active")) {
			List<ProjectTeamMate> employeeDetails=	projectTeamMatesRepo.findByProjectId(pt.getProjectId());
		for (ProjectTeamMate emp : employeeDetails) {

			if (emp.getShift()!=null&&emp.getShift().equalsIgnoreCase(shift)) {
				shiftEmpDetails.add(emp);
			}else if(emp.getShift()==null&&shift.equalsIgnoreCase("Shift 1(09:00 AM - 06:00 PM)")) {
				shiftEmpDetails.add(emp);
			}
		}
		}
		}
		return shiftEmpDetails;
	}
	
	@Override
	public List<ProjectTeamMate> getAllProjectDetails() {
		List<Project> projects = projectRepo.findAll();
		List<ProjectTeamMate> allprojectMates =new ArrayList<>();  
		for (Project pt : projects) {
			if(!pt.getStatus().equalsIgnoreCase("Completed")) {
			List<ProjectTeamMate> employeeDetails=	projectTeamMatesRepo.findByProjectId(pt.getProjectId());
			allprojectMates.addAll(employeeDetails);
		}
		
	}
		return allprojectMates;	
	}
	@Override
	public List<ProjectTeamMate> getProjectDetails(String projectId) {
		return projectTeamMatesRepo.findByProjectId(projectId);

	}
}
