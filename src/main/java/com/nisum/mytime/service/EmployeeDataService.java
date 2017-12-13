package com.nisum.mytime.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
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

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.nisum.mytime.configuration.DbConnection;
import com.nisum.mytime.exception.handler.MyTimeException;
import com.nisum.mytime.model.DateCompare;
import com.nisum.mytime.model.EmpLoginData;
import com.nisum.mytime.utils.MyTimeLogger;
import com.nisum.mytime.utils.MyTimeUtils;

import jcifs.smb.SmbFile;

@Component
@Transactional
public class EmployeeDataService {

	private String dateOnly = null;
	private String empDatestr = null;
	private EmpLoginData empDetails = new EmpLoginData();
	private Connection connection = null;
	private PreparedStatement preparedStatement = null;
	private String query = "SELECT * FROM DeviceLogs_";

	@Autowired
	private MongoTemplate mongoTemplate;

	@Value("${mytime.remote.connection}")
	private String remotePath;

	@Value("${mytime.localFile.directory}")
	private String localFileDirectory;

	@Value("${mytime.attendance.fileName}")
	private String mdbFile;

	@Value("${mytime.remoteFileTransfer.required}")
	private Boolean isRemoteFileTransfer;

	@Value("${mytime.remote.directory}")
	private String remoteFilesDirectory;

	//private List<EmpLoginData> listOfEmpLoginData = null;
	//private DBCursor cursor = null;

	public List<EmpLoginData> fetchEmployeesData() throws MyTimeException {
		String queryMonthDecider = null;
		long start_ms = System.currentTimeMillis();
		List<EmpLoginData> loginsData = new ArrayList<>();
		Map<String, List<EmpLoginData>> map = new HashMap<>();
		boolean frstQuery = true;
		Map<String, EmpLoginData> emp = new HashMap<>();
		try {
			int count = 3;
			File file = fetchRemoteFilesAndCopyToLocal();
			if (null != file && file.getName().equals(mdbFile)) {
				Calendar calendar = new GregorianCalendar();
				int month = (calendar.get(Calendar.MONTH)) + 1;
				int year = calendar.get(Calendar.YEAR);
				
				String dbURL = MyTimeUtils.driverUrl + file.getCanonicalPath();
				MyTimeLogger.getInstance().info(dbURL);
				connection = DbConnection.getDBConnection(dbURL);
				Statement statement = connection.createStatement();
				
				while (month >= count) {
					queryMonthDecider = count + "_" + year;
					ResultSet resultSet = statement.executeQuery(query + queryMonthDecider.trim());
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
							statement1.setLong(1, Long.valueOf(loginData.getEmployeeId()));
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
					count++;
				}
				Iterator<EmpLoginData> iter = loginsData.iterator();
				while (iter.hasNext()) {
					EmpLoginData empLoginData = iter.next();
					getSingleEmploginData(loginsData, map, empLoginData);
				}
				for (Entry<String, List<EmpLoginData>> empMap : map.entrySet()) {
					calculatingEachEmployeeLoginsByDate(empMap.getValue(), emp);
				}
				MyTimeLogger.getInstance().info("Time Taken for " + (System.currentTimeMillis() - start_ms));
			}
		} catch (Exception sqlex) {
			MyTimeLogger.getInstance().info(sqlex.getMessage());
			throw new MyTimeException(sqlex.getMessage());
		}
		return new ArrayList<>(emp.values());
	}

	private File fetchRemoteFilesAndCopyToLocal() throws IOException {
		File Finalfile = null;
		if (Boolean.TRUE.equals(isRemoteFileTransfer)) {
			SmbFile[] smbFiles = new SmbFile(remotePath).listFiles();
			for (SmbFile file : smbFiles) {
				if (file.getCanonicalPath().contains(mdbFile)) {
					Finalfile = new File(localFileDirectory + file.getName());
					try (InputStream in = file.getInputStream();
							FileOutputStream out = new FileOutputStream(Finalfile)) {
						IOUtils.copy(in, out);
					}
				}
			}
		} else {
			File dir = new File(remoteFilesDirectory);
			for (File file : dir.listFiles()) {
				if (file.getCanonicalPath().contains(mdbFile)) {
					Finalfile = new File(file.getCanonicalPath());

				}
			}
		}
		return Finalfile;
	}

	public List<EmpLoginData> fetchEmployeeLoginsBasedOnDates(long employeeId, String fromDate, String toDate)
			throws MyTimeException {
			Query query = null;
			if(employeeId == 0){
				query = new Query(Criteria.where("dateOfLogin").gte(fromDate).lte(toDate));
			}else{
				query = new Query(Criteria.where("_id").gte(employeeId + "-" + fromDate).lte(employeeId + "-" + toDate));
			}
			return mongoTemplate.find(query,EmpLoginData.class);
//			try {
//				listOfEmpLoginData = new ArrayList<>();
//			BasicDBObject gtQuery = new BasicDBObject();
//			if(employeeId == 0){
//				gtQuery.put("_id",
//						new BasicDBObject("$gt", fromDate).append("$lt", toDate));
//			}else{
//				gtQuery.put("_id",
//						new BasicDBObject("$gt", employeeId + "-" + fromDate).append("$lt", employeeId + "-" + toDate));
//			}
//			
//			cursor = mongoTemplate.getCollection("EmployeesLoginData").find(gtQuery);
//			while (cursor.hasNext()) {
//				DBObject dbObject = cursor.next();
//				EmpLoginData empLoginData = new EmpLoginData();
//				empLoginData.setEmployeeId(dbObject.get("employeeId").toString());
//				empLoginData.setEmployeeName(dbObject.get("employeeName").toString());
//				empLoginData.setDateOfLogin(dbObject.get("dateOfLogin").toString());
//				empLoginData.setFirstLogin(dbObject.get("firstLogin").toString());
//				empLoginData.setLastLogout(dbObject.get("lastLogout").toString());
//				empLoginData.setTotalLoginTime(dbObject.get("totalLoginTime").toString());
//				listOfEmpLoginData.add(empLoginData);
//			}
//		} catch (Exception ex) {
//			MyTimeLogger.getInstance().info(ex.getMessage());
//			throw new MyTimeException(ex.getMessage());
//		}
//		cursor.close();
//		return listOfEmpLoginData;
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
				firstAndLastLoginDates.add(MyTimeUtils.df.parse(empDatestr) + "");
				internalEmpMap.put(dateOnly, empLoginData);
				first = false;
			} else {
				empDatestr = empLoginData.getFirstLogin();
				Date dt = MyTimeUtils.df.parse(empDatestr);
				String timeOnly = MyTimeUtils.tdf.format(dt);
				String nextDate = MyTimeUtils.dfmt.format(dt);
				if (dateOnly.equals(nextDate)) {
					dates.add(timeOnly);
					firstAndLastLoginDates.add(MyTimeUtils.df.parse(empDatestr) + "");
					if (count == loginsData.size()) {
						addingEmpDatesBasedonLogins(empLoginData, dates, firstAndLastLoginDates, dateOnly,
								internalEmpMap);
						internalEmpMap.get(dateOnly).setId(employeeId + "-" + dateOnly);
						empMap.put(employeeId + "-" + dateOnly, internalEmpMap.get(dateOnly));
					}
				} else {
					EmpLoginData empLoginData1 = internalEmpMap.get(dateOnly);
					addingEmpDatesBasedonLogins(empLoginData1, dates, firstAndLastLoginDates, dateOnly, internalEmpMap);
					internalEmpMap.get(dateOnly).setId(employeeId + "-" + dateOnly);
					empMap.put(employeeId + "-" + dateOnly, internalEmpMap.get(dateOnly));
					firstLoginAndLastRecordAdding(empLoginData, dates);
					firstAndLastLoginDates.add(MyTimeUtils.df.parse(empDatestr) + "");
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

	public List<EmpLoginData> fetchEmployeeDataBasedOnEmpId(long id) throws MyTimeException {

		List<String> dates = new ArrayList<>();
		List<String> firstAndLastLoginDates = new ArrayList<>();
		Map<String, EmpLoginData> empMap = new HashMap<>();
		long start_ms = System.currentTimeMillis();
		List<EmpLoginData> loginsData = new ArrayList<>();
		boolean first = true;
		boolean frstQuery = true;
		try {
			File dir = new File(localFileDirectory);
			for (File file : dir.listFiles()) {
				String msAccDB = localFileDirectory + file.getName();
				String dbURL = MyTimeUtils.driverUrl + msAccDB;
				connection = DbConnection.getDBConnection(dbURL);
				Calendar calendar = new GregorianCalendar();
				int date = calendar.get(Calendar.MONTH);
				int year = calendar.get(Calendar.YEAR);
				preparedStatement = connection
						.prepareStatement("SELECT * FROM DeviceLogs_" + "_" + date + "_" + year + " Where USERID=?");
				preparedStatement.setLong(1, id);
				ResultSet resultSet = preparedStatement.executeQuery();

				if (frstQuery) {
					PreparedStatement statement1 = connection
							.prepareStatement("SELECT * FROM EMPLOYEES Where EMPLOYEECODE=?");
					statement1.setLong(1, id);
					ResultSet resultSet1 = statement1.executeQuery();
					while (resultSet1.next() && frstQuery) {
						empDetails.setEmployeeName(resultSet1.getString(2));
						frstQuery = false;
						statement1.close();
						resultSet1.close();
					}
				}

				while (resultSet.next()) {
					if (resultSet.getString(4).length() >= 5) {
						EmpLoginData loginData = new EmpLoginData();
						loginData.setEmployeeId(resultSet.getString(4));
						loginData.setEmployeeName(empDetails.getEmployeeName());
						loginData.setFirstLogin(resultSet.getString(5));
						loginData.setDirection(resultSet.getString(6));
						loginsData.add(loginData);
					}
				}
			}
			System.out.println(loginsData.size());

			Collections.sort(loginsData, new DateCompare());
			int count = 0;
			for (EmpLoginData empLoginData : loginsData) {
				count++;
				if (first) {
					firstLoginAndLastRecordAdding(empLoginData, dates);
					firstAndLastLoginDates.add(MyTimeUtils.df.parse(empDatestr) + "");
					empMap.put(dateOnly, empLoginData);
					first = false;
				} else {
					empDatestr = empLoginData.getFirstLogin();
					Date dt = MyTimeUtils.df.parse(empDatestr);
					String timeOnly = MyTimeUtils.tdf.format(dt);
					String nextDate = MyTimeUtils.dfmt.format(dt);
					if (dateOnly.equals(nextDate)) {
						dates.add(timeOnly);
						firstAndLastLoginDates.add(MyTimeUtils.df.parse(empDatestr) + "");
						if (count == loginsData.size()) {
							addingEmpDatesBasedonLogins(empLoginData, dates, firstAndLastLoginDates, dateOnly, empMap);
						}
					} else {
						EmpLoginData empLoginData1 = empMap.get(dateOnly);
						addingEmpDatesBasedonLogins(empLoginData1, dates, firstAndLastLoginDates, dateOnly, empMap);
						firstLoginAndLastRecordAdding(empLoginData, dates);
						firstAndLastLoginDates.add(MyTimeUtils.df.parse(empDatestr) + "");
						empMap.put(dateOnly, empLoginData);
						if (count == loginsData.size()) {
							addingEmpDatesBasedonLogins(empLoginData, dates, firstAndLastLoginDates, dateOnly, empMap);
						}
					}
				}
			}
			MyTimeLogger.getInstance().info("Time :" + (System.currentTimeMillis() - start_ms));

		} catch (Exception sqlex) {
			MyTimeLogger.getInstance().info(sqlex.getMessage());
			throw new MyTimeException(sqlex.getMessage());
		}
		return new ArrayList<EmpLoginData>(empMap.values());
	}

	private void firstLoginAndLastRecordAdding(EmpLoginData empLoginData, List<String> dates) throws ParseException {
		empDatestr = empLoginData.getFirstLogin();
		Date dt = MyTimeUtils.df.parse(empDatestr);
		String timeOnly = MyTimeUtils.tdf.format(dt);
		dateOnly = MyTimeUtils.dfmt.format(dt);
		empLoginData.setDateOfLogin(dateOnly);
		dates.add(timeOnly);
	}

	private EmpLoginData addingEmpDatesBasedonLogins(EmpLoginData empLoginData, List<String> dates,
			List<String> firstAndLastLoginDates, String dateOnly, Map<String, EmpLoginData> empMap)
			throws ParseException {
		String roundingMinutes = null;
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
		if (minutes < 10) {
			roundingMinutes = "0" + minutes;
			empLoginData.setTotalLoginTime(hours + ":" + roundingMinutes);
		} else {
			empLoginData.setTotalLoginTime(hours + ":" + minutes);
		}
		empLoginData.setDateOfLogin(dateOnly);
		empMap.put(dateOnly, empLoginData);
		dates.clear();
		firstAndLastLoginDates.clear();
		return empLoginData;
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

}