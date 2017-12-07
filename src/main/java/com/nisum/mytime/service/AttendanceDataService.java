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
import org.springframework.stereotype.Service;

import com.nisum.mytime.configuration.DbConnection;
import com.nisum.mytime.exception.handler.MyTimeException;
import com.nisum.mytime.model.DateCompare;
import com.nisum.mytime.model.EmpLoginData;
import com.nisum.mytime.repository.EmployeeLoginsRepo;
import com.nisum.mytime.utils.MyTimeLogger;
import com.nisum.mytime.utils.MyTimeUtils;

import jcifs.smb.SmbFile;

@Service
@Transactional(rollbackOn = RuntimeException.class)
public class AttendanceDataService {

	private String dateOnly = null;
	private String empDatestr = null;
	private Connection connection = null;
	private Statement statement = null;
	private ResultSet resultSet = null;

	private PreparedStatement statement1 = null;

	@Autowired
	private EmployeeLoginsRepo employeeLoginsRepo;

	@Value("${mytime.remoteFile.location}")
	private String remotePath;

	@Value("${mytime.localFile.location}")
	private String localFile;

	@Value("${mytime.attendance.fileName}")
	private String mdbFile;

	@Value("${mytime.attendance.fileExtension}")
	private String fileExtension;

	public List<EmpLoginData> populateEmployeeAttendanceData() throws MyTimeException {

		long start_ms = System.currentTimeMillis();
		List<EmpLoginData> loginsData = new ArrayList<>();
		Map<String, List<EmpLoginData>> map = new HashMap<>();
		boolean frstQuery = true;
		Map<String, EmpLoginData> emp = new HashMap<>();
		String underScore = "_";
		String logfilename = "DeviceLogs_";
		String query = "SELECT * FROM ";

		try {
			long dbTime = System.currentTimeMillis();

			Calendar calendar = new GregorianCalendar();
			int month = (calendar.get(Calendar.MONTH)) + 1;
			int year = calendar.get(Calendar.YEAR);
			int day = calendar.get(Calendar.DAY_OF_MONTH);

			StringBuilder queryBuilder = new StringBuilder();

			queryBuilder.append(query);
			queryBuilder.append(logfilename);
			queryBuilder.append(month);
			queryBuilder.append(underScore);
			queryBuilder.append(year);

			File localFile = fetchRemoteFilesAndCopyToLocal();
			String dbURL = MyTimeUtils.driverUrl + localFile;
			MyTimeLogger.getInstance().info(dbURL);
			connection = DbConnection.getDBConnection(dbURL);
			statement = connection.createStatement();
			MyTimeLogger.getInstance().info(queryBuilder.toString());

			resultSet = statement.executeQuery(queryBuilder.toString());
			String empname = null;
			while (resultSet.next()) {
				frstQuery = true;
				if (resultSet.getString(4).length() >= 5) {
					EmpLoginData loginData = new EmpLoginData();
					loginData.setEmployeeId(resultSet.getString(4));
					loginData.setFirstLogin(resultSet.getString(5));
					loginData.setDirection(resultSet.getString(6));
					statement1 = connection.prepareStatement("SELECT * FROM EMPLOYEES Where EMPLOYEECODE=?");
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
			MyTimeLogger.getInstance().info(" Taking time to do Db-Time job " + (System.currentTimeMillis() - dbTime));

			long start_ms1 = System.currentTimeMillis();

			Iterator<EmpLoginData> iter = loginsData.iterator();
			while (iter.hasNext()) {
				EmpLoginData empLoginData = iter.next();
				getSingleEmploginData(loginsData, map, empLoginData);
			}

			MyTimeLogger.getInstance()
					.info(" Taking time to do iterator job " + (System.currentTimeMillis() - start_ms1));

			for (Entry<String, List<EmpLoginData>> empMap : map.entrySet()) {
				calculatingEachEmployeeLoginsByDate(empMap.getValue(), emp);
			}

			employeeLoginsRepo.save(emp.values());

			MyTimeLogger.getInstance()
					.info(" Time Taken for 1st to Savng also Done ::: " + (System.currentTimeMillis() - start_ms));

		} catch (Exception ex) {
			MyTimeLogger.getInstance().info(ex.getMessage());
			throw new MyTimeException(ex.getMessage());
		} finally {
			try {
				if (null != connection) {
					resultSet.close();
					statement.close();
					statement1.close();
					connection.close();
				}
			} catch (Exception sqlex) {
				MyTimeLogger.getInstance().info(sqlex.getMessage());
				throw new MyTimeException(sqlex.getMessage());
			}
		}
		return new ArrayList<>(emp.values());
	}

	private File fetchRemoteFilesAndCopyToLocal() throws IOException {
		SmbFile[] files = new SmbFile(remotePath).listFiles();
		File file1 = new File(localFile);
		for (SmbFile file : files) {
			if (file.getCanonicalPath().contains(mdbFile)) {
				try (InputStream in = file.getInputStream(); FileOutputStream out = new FileOutputStream(file1)) {
					IOUtils.copy(in, out);
				}
			}
		}
		return file1;
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
					ifCountEqualsSize(count, loginsData, empLoginData, dates, firstAndLastLoginDates, internalEmpMap,
							empMap, employeeId);
				} else {
					EmpLoginData empLoginData1 = internalEmpMap.get(dateOnly);
					addingEmpDatesBasedonLogins(empLoginData1, dates, firstAndLastLoginDates, dateOnly, internalEmpMap);
					internalEmpMap.get(dateOnly).setId(employeeId + "-" + dateOnly);
					empMap.put(employeeId + "-" + dateOnly, internalEmpMap.get(dateOnly));
					firstLoginAndLastRecordAdding(empLoginData, dates);
					firstAndLastLoginDates.add(MyTimeUtils.df.parse(empDatestr) + "");
					internalEmpMap.put(dateOnly, empLoginData);
					ifCountEqualsSize(count, loginsData, empLoginData, dates, firstAndLastLoginDates, internalEmpMap,
							empMap, employeeId);
				}
			}
		}
	}

	private void ifCountEqualsSize(int count, List<EmpLoginData> loginsData, EmpLoginData empLoginData,
			List<String> dates, List<String> firstAndLastLoginDates, Map<String, EmpLoginData> internalEmpMap,
			Map<String, EmpLoginData> empMap, String employeeId) throws ParseException {
		if (count == loginsData.size()) {
			addingEmpDatesBasedonLogins(empLoginData, dates, firstAndLastLoginDates, dateOnly, internalEmpMap);
			internalEmpMap.get(dateOnly).setId(employeeId + "-" + dateOnly);
			empMap.put(employeeId + "-" + dateOnly, internalEmpMap.get(dateOnly));
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
		Date dt = MyTimeUtils.df.parse(empDatestr);
		String timeOnly = MyTimeUtils.tdf.format(dt);
		dateOnly = MyTimeUtils.dfmt.format(dt);
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
