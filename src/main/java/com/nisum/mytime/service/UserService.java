package com.nisum.mytime.service;

import java.util.List;

import com.nisum.mytime.exception.handler.MyTimeException;
import com.nisum.mytime.model.EmpLoginData;
import com.nisum.mytime.model.EmployeeRoles;

public interface UserService {

	List<EmpLoginData> fetchEmployeeDataBasedOnEmpId(long id) throws MyTimeException;

	List<EmpLoginData> fetchEmployeesData() throws MyTimeException;

	List<EmpLoginData> employeeLoginsBasedOnDate(long id, String fromDate, String toDate) throws MyTimeException;

	List<EmployeeRoles> getEmployeesRole() throws MyTimeException;

	void assigingEmployeeRole(EmployeeRoles employeeRoles) throws MyTimeException;
	
	Boolean generatePdfReport(long id, String fromDate, String toDate) throws MyTimeException;

}
