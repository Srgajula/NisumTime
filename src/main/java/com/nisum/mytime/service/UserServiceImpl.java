package com.nisum.mytime.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nisum.mytime.model.EmpLoginData;
import com.nisum.mytime.repository.EmployeeLoginsRepo;

@Service("userService")
public class UserServiceImpl implements UserService {

	@Autowired
	private EmployeeLoginsRepo employeeLoginsRepo;

	@Autowired
	private EmployeeDataService employeeDataBaseService;

	@Override
	public List<EmpLoginData> fetchEmployeeDataBasedOnEmpId(long id) throws FileNotFoundException, ParseException {
		return employeeDataBaseService.fetchEmployeeDataBasedOnEmpId(id);
	}

	@Override
	public List<EmpLoginData> fetchEmployeesData() throws ParseException, IOException {
		List<EmpLoginData> listOfEmpLoginData = employeeDataBaseService.fetchEmployeesData();
		System.out.println("final size:::" + listOfEmpLoginData.size());
		return employeeLoginsRepo.save(listOfEmpLoginData);
	}

}
