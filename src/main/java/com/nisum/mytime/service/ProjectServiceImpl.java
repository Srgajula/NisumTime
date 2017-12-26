package com.nisum.mytime.service;

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
import com.nisum.mytime.repository.EmployeeAttendanceRepo;
import com.nisum.mytime.repository.EmployeeRolesRepo;
import com.nisum.mytime.repository.ProjectRepo;
import com.nisum.mytime.utils.PdfReportGenerator;

@Service("projectService")
public class ProjectServiceImpl implements ProjectService {

	@Autowired
	private EmployeeAttendanceRepo employeeLoginsRepo;

	@Autowired
	private EmployeeRolesRepo employeeRolesRepo;
	
	@Autowired
	private ProjectRepo projectRepo;

	@Autowired
	private EmployeeDataService employeeDataBaseService;

	@Autowired
	private PdfReportGenerator pdfReportGenerator;

	@Autowired
	private MongoTemplate mongoTemplate;


	@Override
	public Boolean fetchEmployeesData() throws MyTimeException {
		Boolean result = false;
		List<EmpLoginData> listOfEmpLoginData = employeeDataBaseService.fetchEmployeesData();
		employeeLoginsRepo.save(listOfEmpLoginData);
		result = true;
		return result;
	}

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
		update.set("employeeIds", project.getEmployeeIds());
		FindAndModifyOptions options = new FindAndModifyOptions();
		options.returnNew(true);
		options.upsert(true);
		return mongoTemplate.findAndModify(query, update, options, Project.class);
	}

	@Override
	public EmployeeRoles getEmployeesRoleData(String employeeId) {
		return employeeRolesRepo.findByEmployeeId(employeeId);
	}

}
