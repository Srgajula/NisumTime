package com.nisum.mytime.service;

import java.util.List;

import com.nisum.mytime.exception.handler.MyTimeException;
import com.nisum.mytime.model.Account;
import com.nisum.mytime.model.Designation;
import com.nisum.mytime.model.EmpLoginData;
import com.nisum.mytime.model.EmployeeRoles;
import com.nisum.mytime.model.Shift;
import com.nisum.mytime.model.Skill;

public interface UserService {

	Boolean fetchEmployeesData(String perticularDate) throws MyTimeException;

	List<EmpLoginData> employeeLoginsBasedOnDate(long id, String fromDate, String toDate) throws MyTimeException;

	List<EmployeeRoles> getEmployeeRoles() throws MyTimeException;

	EmployeeRoles assigingEmployeeRole(EmployeeRoles employeeRoles) throws MyTimeException;

	String generatePdfReport(long id, String fromDate, String toDate) throws MyTimeException;

	EmployeeRoles getEmployeesRole(String emailId);

	void deleteEmployee(String empId);

	EmployeeRoles updateEmployeeRole(EmployeeRoles employeeRoles);

	EmployeeRoles getEmployeesRoleData(String empId);
	
	List<Shift> getAllShifts() throws MyTimeException;
	
	List<Designation> getAllDesignations() throws MyTimeException;
	
	List<Skill> getTechnologies() throws MyTimeException;
	
	public EmployeeRoles updateProfile(EmployeeRoles employeeRoles) throws MyTimeException;
	
	public List<Account> getAccounts() throws MyTimeException; 
}
