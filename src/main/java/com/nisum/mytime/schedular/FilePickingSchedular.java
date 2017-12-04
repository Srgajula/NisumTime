package com.nisum.mytime.schedular;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nisum.mytime.configuration.DbConnection;
import com.nisum.mytime.model.DateCompare;
import com.nisum.mytime.model.EmpLoginData;
import com.nisum.mytime.repository.EmployeeLoginsRepo;

@Service
@Transactional(rollbackOn = RuntimeException.class)
public class FilePickingSchedular {

	private String dateOnly = null;
	private String empDatestr = null;
	private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static DateFormat tdf = new SimpleDateFormat("HH:mm");
	private static DateFormat dfmt = new SimpleDateFormat("yyyy-MM-dd");
	private static String filePath = "/Users/nisum/Documents/workspace-sts-3.8/newMdb/";
	private Connection connection = null;
	private Statement statement = null;
	private ResultSet resultSet = null;
	private PreparedStatement preparedStatement = null;
	private static String driverUrl = "jdbc:ucanaccess://";
	private static String log = "Taking time to do all operation ";

	@Autowired
	private EmployeeLoginsRepo employeeLoginsRepo;

	public List<EmpLoginData> fetchEmployeesData() throws ParseException, IOException {

		long start_ms = System.currentTimeMillis();
		List<EmpLoginData> loginsData = new ArrayList<>();
		Map<String, List<EmpLoginData>> map = new HashMap<>();
		boolean frstQuery = true;
		Map<String, EmpLoginData> emp = new HashMap<>();
		String mdbFileName = "eTimeTrackLite_";
		String underScore = "_";
		String fileExtension = "_1.mdb";
		String logfilename = "DeviceLogs_";
		String query = "SELECT * FROM ";

		String file = "eTimeTrackLite1.mdb";

		try {
			long dbTime = System.currentTimeMillis();

			Calendar calendar = new GregorianCalendar();
			int month = (calendar.get(Calendar.MONTH)) + 1;
			int year = calendar.get(Calendar.YEAR);
			int day = calendar.get(Calendar.DAY_OF_MONTH);

			StringBuilder fileNameBuilder = new StringBuilder();

			fileNameBuilder.append(filePath);
			fileNameBuilder.append(mdbFileName);
			fileNameBuilder.append(year);
			fileNameBuilder.append(underScore);
			fileNameBuilder.append(month);
			fileNameBuilder.append(underScore);
			fileNameBuilder.append(day);
			fileNameBuilder.append(fileExtension);

			StringBuilder queryBuilder = new StringBuilder();

			queryBuilder.append(query);
			queryBuilder.append(logfilename);
			queryBuilder.append(month);
			queryBuilder.append(underScore);
			queryBuilder.append(year);

			file = filePath + file;

			String msAccDB = filePath + file.toString();
			String dbURL = driverUrl + file;

			System.out.println(file);

			connection = DbConnection.getDBConnection(dbURL);
			statement = connection.createStatement();

			System.out.println(queryBuilder.toString());

			resultSet = statement.executeQuery(queryBuilder.toString());

			String empname = null;
			while (resultSet.next()) {
				frstQuery = true;
				if (resultSet.getString(4).length() >= 5) {
					EmpLoginData loginData = new EmpLoginData();
					loginData.setEmployeeId(resultSet.getString(4));
					loginData.setFirstLogin(resultSet.getString(5));
					loginData.setDirection(resultSet.getString(6));
					PreparedStatement statement1 = connection
							.prepareStatement("SELECT * FROM EMPLOYEES Where EMPLOYEECODE=?");
					statement1.setLong(1, Long.valueOf(resultSet.getString(4)));
					ResultSet resultSet1 = statement1.executeQuery();
					while (resultSet1.next() && frstQuery) {
						empname = resultSet1.getString(2);
						loginData.setEmployeeName(empname);
						frstQuery = false;
					}
					loginData.setId(resultSet.getString(4) + empname);
					loginsData.add(loginData);
				}
			}
			System.out.println(" Taking time to do Db-Time job " + (System.currentTimeMillis() - dbTime));

			System.out.println(loginsData.size());

			long start_ms1 = System.currentTimeMillis();

			Iterator<EmpLoginData> iter = loginsData.iterator();
			while (iter.hasNext()) {
				EmpLoginData empLoginData = iter.next();
				getSingleEmploginData(loginsData, map, empLoginData);
			}

			System.out.println(" Taking time to do iterator job " + (System.currentTimeMillis() - start_ms1));

			System.out.println("map size " + map.size());

			for (Entry<String, List<EmpLoginData>> empMap : map.entrySet()) {
				calculatingEachEmployeeLoginsByDate(empMap.getValue(), emp);
			}

			employeeLoginsRepo.save(emp.values());

			System.out.println(log + " and Savng also Done ::: " + (System.currentTimeMillis() - start_ms));

		} catch (SQLException sqlex) {
			sqlex.printStackTrace();
		} finally {
			try {
				if (null != connection) {
					resultSet.close();
					statement.close();
					connection.close();
				}
			} catch (SQLException sqlex) {
				sqlex.printStackTrace();
			}
		}
		return new ArrayList<EmpLoginData>(emp.values());
	}

	private void calculatingEachEmployeeLoginsByDate(List<EmpLoginData> loginsData, Map<String, EmpLoginData> empMap)
			throws ParseException {
		boolean first = true;
		List<String> dates = new ArrayList<>();
		List<String> firstAndLastLoginDates = new ArrayList<>();
		Map<String, EmpLoginData> internalEmpMap = new HashMap<>();
		Collections.sort(loginsData, new DateCompare());
		int count = 0;
		String employeeId = loginsData.get(0).getEmployeeId();
		for (EmpLoginData empLoginData : loginsData) {
			count++;
			if (first) {
				firstLoginAndLastRecordAdding(empLoginData, dates);
				firstAndLastLoginDates.add(df.parse(empDatestr) + "");
				internalEmpMap.put(dateOnly, empLoginData);
				first = false;
			} else {
				empDatestr = empLoginData.getFirstLogin();
				Date dt = df.parse(empDatestr);
				String timeOnly = tdf.format(dt);
				String nextDate = dfmt.format(dt);
				if (dateOnly.equals(nextDate)) {
					dates.add(timeOnly);
					firstAndLastLoginDates.add(df.parse(empDatestr) + "");
				} else {
					EmpLoginData empLoginData1 = internalEmpMap.get(dateOnly);
					addingEmpDatesBasedonLogins(empLoginData1, dates, firstAndLastLoginDates, dateOnly, internalEmpMap);
					internalEmpMap.get(dateOnly).setId(employeeId + "-" + dateOnly);
					empMap.put(employeeId + "-" + dateOnly, internalEmpMap.get(dateOnly));
					firstLoginAndLastRecordAdding(empLoginData, dates);
					firstAndLastLoginDates.add(df.parse(empDatestr) + "");
					internalEmpMap.put(dateOnly, empLoginData);
					if (count == loginsData.size()) {
						addingEmpDatesBasedonLogins(empLoginData, dates, firstAndLastLoginDates, dateOnly,
								internalEmpMap);
						internalEmpMap.get(dateOnly).setId(employeeId + "-" + dateOnly);
						empMap.put(employeeId + "-" + dateOnly, internalEmpMap.get(dateOnly));
					}
				}
			}
		}
	}

	private void getSingleEmploginData(List<EmpLoginData> loginsData, Map<String, List<EmpLoginData>> map,
			EmpLoginData empLoginData) {
		List<EmpLoginData> singleEmpLogindata = new ArrayList<>();
		Iterator<EmpLoginData> iter = loginsData.iterator();
		while (iter.hasNext()) {
			EmpLoginData empLoginData1 = iter.next();
			if (empLoginData.getEmployeeId().equals(empLoginData1.getEmployeeId())) {
				singleEmpLogindata.add(empLoginData1);
			}
		}
		map.put(empLoginData.getEmployeeId(), singleEmpLogindata);
	}

	private void firstLoginAndLastRecordAdding(EmpLoginData empLoginData, List<String> dates) throws ParseException {
		empDatestr = empLoginData.getFirstLogin();
		Date dt = df.parse(empDatestr);
		String timeOnly = tdf.format(dt);
		dateOnly = dfmt.format(dt);
		empLoginData.setDateOfLogin(dateOnly);
		dates.add(timeOnly);
	}

	private EmpLoginData addingEmpDatesBasedonLogins(EmpLoginData empLoginData, List<String> dates,
			List<String> firstAndLastLoginDates, String dateOnly, Map<String, EmpLoginData> empMap)
			throws ParseException {
		String min = dates.get(0);
		String max = dates.get(dates.size() - 1);
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		Date maxDate = format.parse(max);
		Date minDate = format.parse(min);
		empLoginData.setFirstLogin(min);
		empLoginData.setLastLogout(max);
		long diffHours = (maxDate.getTime() - minDate.getTime());
		int seconds = ((int) diffHours) / 1000;
		int hours = seconds / 3600;
		int minutes = (seconds % 3600) / 60;
		empLoginData.setTotalLoginTime(hours + ":" + minutes);
		empLoginData.setDateOfLogin(dateOnly);
		empMap.put(dateOnly, empLoginData);
		dates.clear();
		firstAndLastLoginDates.clear();
		return empLoginData;
	}

}
