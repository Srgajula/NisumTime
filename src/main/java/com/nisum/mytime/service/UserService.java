package com.nisum.mytime.service;

import java.util.List;

import com.nisum.mytime.exception.handler.MyTimeException;
import com.nisum.mytime.model.EmpLoginData;
import com.nisum.mytime.model.EmployeeRoles;

public interface UserService {

	Boolean fetchEmployeesData() throws MyTimeException;

	List<EmpLoginData> employeeLoginsBasedOnDate(long id, String fromDate, String toDate) throws MyTimeException;

	List<EmployeeRoles> getEmployeeRoles() throws MyTimeException;

	EmployeeRoles assigingEmployeeRole(EmployeeRoles employeeRoles) throws MyTimeException;

	String generatePdfReport(long id, String fromDate, String toDate) throws MyTimeException;

	EmployeeRoles getEmployeesRole(String emailId);

	void deleteEmployee(String empId);

	EmployeeRoles updateEmployeeRole(EmployeeRoles employeeRoles);

	EmployeeRoles getEmployeesRoleData(String empId);
}
