package com.nisum.mytime.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import com.nisum.mytime.model.EmpLoginData;

public interface UserService {

	List<EmpLoginData> fetchEmployeeDataBasedOnEmpId(long id) throws FileNotFoundException, ParseException;

	List<EmpLoginData> fetchEmployeesData() throws FileNotFoundException, ParseException, IOException;

}
