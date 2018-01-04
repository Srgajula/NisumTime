package com.nisum.mytime.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.nisum.mytime.exception.handler.MyTimeException;
import com.nisum.mytime.model.Designation;
import com.nisum.mytime.model.EmpLoginData;
import com.nisum.mytime.model.EmployeeRoles;
import com.nisum.mytime.model.ProjectTeamMate;
import com.nisum.mytime.model.Shift;
import com.nisum.mytime.model.Skill;
import com.nisum.mytime.repository.DesignationRepo;
import com.nisum.mytime.repository.EmployeeRolesRepo;
import com.nisum.mytime.repository.ProjectTeamMatesRepo;
import com.nisum.mytime.repository.ShiftRepo;
import com.nisum.mytime.repository.TechnologyRepo;
import com.nisum.mytime.utils.PdfReportGenerator;

@Service("userService")
public class UserServiceImpl implements UserService {

	@Autowired
	private EmployeeRolesRepo employeeRolesRepo;
	@Autowired
	private ProjectTeamMatesRepo projectTeamMatesRepo;
	@Autowired
	private ShiftRepo shiftRepo;
	
	@Autowired
	private DesignationRepo designationRepo;
	@Autowired
	private TechnologyRepo technologyRepo;

	@Autowired
	private EmployeeDataService employeeDataBaseService;

	@Autowired
	private PdfReportGenerator pdfReportGenerator;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public Boolean fetchEmployeesData(String perticularDate) throws MyTimeException {
		return employeeDataBaseService.fetchEmployeesData(perticularDate);
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
	public List<EmployeeRoles> getEmployeeRoles() throws MyTimeException {
		return employeeRolesRepo.findAll();
	}

	@Override
	public EmployeeRoles assigingEmployeeRole(EmployeeRoles employeeRoles) throws MyTimeException {
		employeeRoles.setCreatedOn(new Date());
			return employeeRolesRepo.save(employeeRoles);
	}

	@Override
	public EmployeeRoles getEmployeesRole(String emailId) {
		return employeeRolesRepo.findByEmailId(emailId);

	}

	@Override
	public void deleteEmployee(String employeeId) {
		EmployeeRoles role = employeeRolesRepo.findByEmployeeId(employeeId);
		employeeRolesRepo.delete(role);
	}

	@Override
	public EmployeeRoles updateEmployeeRole(EmployeeRoles employeeRoles) {
		Query query = new Query(Criteria.where("employeeId").is(employeeRoles.getEmployeeId()));
		Update update = new Update();
		update.set("employeeName", employeeRoles.getEmployeeName());
		update.set("emailId", employeeRoles.getEmailId());
		update.set("role", employeeRoles.getRole());
		update.set("shift", employeeRoles.getShift());
		update.set("lastModifiedOn", new Date());
		FindAndModifyOptions options = new FindAndModifyOptions();
		options.returnNew(true);
		options.upsert(true);
		return mongoTemplate.findAndModify(query, update, options, EmployeeRoles.class);
	}

	@Override
	public EmployeeRoles getEmployeesRoleData(String employeeId) {
		return employeeRolesRepo.findByEmployeeId(employeeId);
	}

	@Override
	public List<Shift> getAllShifts() throws MyTimeException {
		return shiftRepo.findAll();
	}

	@Override
	public List<Designation> getAllDesignations() throws MyTimeException {
		return designationRepo.findAll();
	}

	@Override
	public List<Skill> getTechnologies() throws MyTimeException {
	return technologyRepo.findAll();
	}
	@Override
	public EmployeeRoles updateProfile(EmployeeRoles employeeRoles) throws MyTimeException {
		boolean mobileNumberChnaged=false;
		employeeRoles.setLastModifiedOn(new Date());
		EmployeeRoles existingEmployee=employeeRolesRepo.findByEmployeeId(employeeRoles.getEmployeeId());
		String newMobileNumber=employeeRoles.getMobileNumber();
		if(newMobileNumber!=null&&!newMobileNumber.equalsIgnoreCase("")) {
			if(existingEmployee!=null&&existingEmployee.getMobileNumber()!=null&&!existingEmployee.getMobileNumber().equalsIgnoreCase(newMobileNumber)){
				mobileNumberChnaged=true;
			}
		}
		EmployeeRoles employeeRolesDB= employeeRolesRepo.save(employeeRoles);
		if(mobileNumberChnaged) {
			try {
		List<ProjectTeamMate>  employeeProfiles=	projectTeamMatesRepo.findByEmployeeId(employeeRoles.getEmployeeId());
		if(employeeProfiles!=null&&!employeeProfiles.isEmpty()) {
			for(ProjectTeamMate profile:employeeProfiles){
				profile.setMobileNumber(employeeRolesDB.getMobileNumber());
				projectTeamMatesRepo.save(profile);
			}
		}}catch(Exception e) {}
		}
		return employeeRolesDB;
	
	}
}
