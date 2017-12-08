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
	public List<EmpLoginData> fetchEmployeesData() throws MyTimeException {
		List<EmpLoginData> listOfEmpLoginData = employeeDataBaseService.fetchEmployeesData();
		return employeeLoginsRepo.save(listOfEmpLoginData);
	}

	@Override
	public List<EmpLoginData> employeeLoginsBasedOnDate(long id, String fromDate, String toDate)
			throws MyTimeException {
		return employeeDataBaseService.fetchEmployeeLoginsBasedOnDates(id, fromDate, toDate);
	}

	@Override
	public Boolean generatePdfReport(long id, String fromDate, String toDate) throws MyTimeException {
		return pdfReportGenerator.generateEmployeeReport(id, fromDate, toDate);
	}

	@Override
	public List<EmployeeRoles> getEmployeesRole() throws MyTimeException {
		return employeeRolesRepo.findAll();
	}

	@Override
	public void assigingEmployeeRole(EmployeeRoles employeeRoles) throws MyTimeException {
		employeeRolesRepo.save(employeeRoles);
	}

}
