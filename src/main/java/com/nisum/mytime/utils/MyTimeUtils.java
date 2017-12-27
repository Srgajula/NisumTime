package com.nisum.mytime.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class MyTimeUtils {

	private MyTimeUtils() {

	}

	public final static String driverUrl = "jdbc:ucanaccess://";
	public final static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public final static DateFormat tdf = new SimpleDateFormat("HH:mm");
	public final static DateFormat dfmt = new SimpleDateFormat("yyyy-MM-dd");
	public final static String UNDER_SCORE = "_";
	public final static String DATE_OF_LOGIN = "dateOfLogin";
	public final static String EMPLOYEE_ID = "employeeId";
	public final static String EMPLOYEE_NAME = "employeeName";
	public final static String FIRST_LOGIN = "firstLogin";
	public final static String LAST_LOGOUT = "lastLogout";
	public final static String TOTAL_LOGIN_TIME = "totalLoginTime";
	public final static String EMPLOYEE_COLLECTION = "EmployeesLoginData";
	public final static String ID = "_id";
	public final static String HYPHEN = "-";
	public final static String ZERO = "0";
	public final static String COLON = ":";
	public final static String EMP_NAME_QUERY = "SELECT * FROM EMPLOYEES Where EMPLOYEECODE=?";

	public final static String WORKING_EMPLOYEES = "SELECT * FROM EMPLOYEES WHERE EMPLOYEECODE NOT IN(SELECT UserId FROM DeviceLogs_12_2017 WHERE LogDate BETWEEN '2017-12-27 06:00:00' AND '2017-12-27 11:00:00') AND STATUS='Working'";
	public final static String QUERY = "SELECT * FROM DeviceLogs_";
	public final static String USERID_QUERY = "SELECT USERID FROM DeviceLogs_";
	public final static String WHERE_COND = " WHERE LogDate between '";
	public final static String AND_COND = " AND '";
	public final static String SINGLE_QUOTE = "'";
	public final static String ABESENT_QUERY = "SELECT * FROM EMPLOYEES WHERE EMPLOYEECODE NOT IN(";
	public final static String ABESENT_QUERY1 = ") AND STATUS='Working' AND EMPLOYEECODE NOT LIKE 'del%' ";
	public final static String ABESENT ="Absent";

}
