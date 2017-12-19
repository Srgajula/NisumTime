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
	public final static String ID = "_id";
	public final static String HYPHEN = "-";
	public final static String ZERO = "0";
	public final static String COLON = ":";
	public final static String EMP_NAME_QUERY="SELECT * FROM EMPLOYEES Where EMPLOYEECODE=?";
	public final static String QUERY = "SELECT * FROM DeviceLogs_";


}
