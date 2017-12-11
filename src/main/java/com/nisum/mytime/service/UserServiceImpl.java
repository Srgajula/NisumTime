package com.nisum.mytime.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nisum.mytime.exception.handler.MyTimeException;
import com.nisum.mytime.model.EmpLoginData;
import com.nisum.mytime.model.EmployeeRoles;
import com.nisum.mytime.repository.EmployeeAttendanceRepo;
import com.nisum.mytime.repository.EmployeeRolesRepo;
import com.nisum.mytime.utils.PdfReportGenerator;

@Service("userService")
public class UserServiceImpl implements UserService {

	@Autowired
	private EmployeeAttendanceRepo employeeLoginsRepo;

	@Autowired
	private EmployeeRolesRepo employeeRolesRepo;

	@Autowired
	private EmployeeDataService employeeDataBaseService;

	@Autowired
	private PdfReportGenerator pdfReportGenerator;

	@Override
	public List<EmpLoginData> fetchEmployeeDataBasedOnEmpId(long id) throws MyTimeException {
		return employeeDataBaseService.fetchEmployeeDataBasedOnEmpId(id);
	}

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
	public List<EmployeeRoles> getEmployeeRoles() throws MyTimeException {
		return employeeRolesRepo.findAll();
	}

	@Override
	public EmployeeRoles assigingEmployeeRole(EmployeeRoles employeeRoles) throws MyTimeException {
		return employeeRolesRepo.save(employeeRoles);
	}

	@Override
	public EmployeeRoles getEmployeesRole(String emailId) {
		return employeeRolesRepo.findByEmailId(emailId);

	}

	@Override
	public void deleteEmployee(EmployeeRoles employeeRoles) {
		employeeRolesRepo.delete(employeeRoles);
	}

}
