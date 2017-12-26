package com.nisum.mytime.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.nisum.mytime.configuration.DbConnection;
import com.nisum.mytime.exception.handler.MyTimeException;
import com.nisum.mytime.model.DateCompare;
import com.nisum.mytime.model.EmpLoginData;
import com.nisum.mytime.repository.EmployeeAttendanceRepo;
import com.nisum.mytime.utils.MyTimeLogger;
import com.nisum.mytime.utils.MyTimeUtils;

import jcifs.smb.SmbFile;

@Component
@Transactional
public class EmployeeDataService {

	private String dateOnly = null;
	private String empDatestr = null;
	private Connection connection = null;
	private Statement statement = null;
	private ResultSet resultSet = null;
	private Date currentDay = null;
	private Date nextCurrentDay = null;
	private DBCursor cursor = null;

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

	@Autowired
	private EmployeeAttendanceRepo employeeLoginsRepo;

	String todayDate = MyTimeUtils.dfmt.format(new Date());

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
				statement = connection.createStatement();

				while (month >= count) {
					queryMonthDecider = count + MyTimeUtils.UNDER_SCORE + year;
					resultSet = statement.executeQuery(MyTimeUtils.QUERY + queryMonthDecider.trim());
					while (resultSet.next()) {
						frstQuery = true;
						if (resultSet.getString(4).length() >= 5) {
							EmpLoginData loginData = new EmpLoginData();
							loginData.setEmployeeId(resultSet.getString(4));
							loginData.setFirstLogin(resultSet.getString(5));
							loginData.setDirection(resultSet.getString(6));
							PreparedStatement statement1 = connection.prepareStatement(MyTimeUtils.EMP_NAME_QUERY);
							statement1.setLong(1, Long.valueOf(loginData.getEmployeeId()));
							ResultSet resultSet1 = statement1.executeQuery();
							while (resultSet1.next() && frstQuery) {
								loginData.setEmployeeName(resultSet1.getString(2));
								frstQuery = false;
							}
							loginData.setId(resultSet.getString(4));
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
			MyTimeLogger.getInstance().error(sqlex.getMessage());
			throw new MyTimeException(sqlex.getMessage());
		} finally {
			try {
				if (null != connection) {
					connection.close();
					statement.close();
					resultSet.close();
				}
			} catch (SQLException e) {
				MyTimeLogger.getInstance().error(e.getMessage());
			}
		}
		return new ArrayList<>(emp.values());
	}

	public Boolean fetchEmployeesDataOnDayBasis() throws MyTimeException, SQLException {
		Boolean result = false;
		StringBuilder queryMonthDecider = new StringBuilder();
		long start_ms = System.currentTimeMillis();
		List<EmpLoginData> loginsData = new ArrayList<>();
		Map<String, List<EmpLoginData>> map = new HashMap<>();
		boolean frstQuery = true;
		Map<String, EmpLoginData> emp = new HashMap<>();
		try {
			File file = fetchRemoteFilesAndCopyToLocal();
			if (null != file && file.getName().equals(mdbFile)) {
				Calendar calendar = new GregorianCalendar();
				int month = (calendar.get(Calendar.MONTH)) + 1;
				int year = calendar.get(Calendar.YEAR);
				int day = calendar.get(Calendar.DAY_OF_MONTH);

				String dbURL = MyTimeUtils.driverUrl + file.getCanonicalPath();
				MyTimeLogger.getInstance().info(dbURL);
				connection = DbConnection.getDBConnection(dbURL);
				statement = connection.createStatement();

				Calendar calendar1 = Calendar.getInstance();
				calendar1.set(year, (month - 1), day - 1, 6, 00, 00);
				Date currentDayDate = calendar1.getTime();

				Calendar calendar2 = Calendar.getInstance();
				calendar2.set(year, (month - 1), day, 6, 00, 00);
				Date nextDayDate = calendar2.getTime();

				queryMonthDecider.append(MyTimeUtils.QUERY);
				queryMonthDecider.append(month);
				queryMonthDecider.append(MyTimeUtils.UNDER_SCORE);
				queryMonthDecider.append(year);
				queryMonthDecider.append(" WHERE LogDate between '");
				queryMonthDecider.append(MyTimeUtils.df.format(currentDayDate) + "'");
				queryMonthDecider.append(" AND '");
				queryMonthDecider.append(MyTimeUtils.df.format(nextDayDate) + "'");

				MyTimeLogger.getInstance().info(queryMonthDecider.toString());

				resultSet = statement.executeQuery(queryMonthDecider.toString());
				while (resultSet.next()) {
					frstQuery = true;
					if (resultSet.getString(4).length() >= 5) {
						EmpLoginData loginData = new EmpLoginData();
						loginData.setEmployeeId(resultSet.getString(4));
						loginData.setFirstLogin(resultSet.getString(5));
						loginData.setDirection(resultSet.getString(6));
						PreparedStatement statement1 = connection.prepareStatement(MyTimeUtils.EMP_NAME_QUERY);
						statement1.setLong(1, Long.valueOf(loginData.getEmployeeId()));
						ResultSet resultSet1 = statement1.executeQuery();
						while (resultSet1.next() && frstQuery) {
							loginData.setEmployeeName(resultSet1.getString(2));
							frstQuery = false;
						}
						loginData.setId(resultSet.getString(4));
						loginsData.add(loginData);
					}
				}
				Iterator<EmpLoginData> iter = loginsData.iterator();
				while (iter.hasNext()) {
					EmpLoginData empLoginData = iter.next();
					getSingleEmploginData(loginsData, map, empLoginData);
				}
				for (Entry<String, List<EmpLoginData>> empMap : map.entrySet()) {
					calculatingEachEmployeeLoginsByDate(empMap.getValue(), emp);
				}
				employeeLoginsRepo.save(emp.values());
				result = Boolean.TRUE;
				MyTimeLogger.getInstance().info("Time Taken for " + (System.currentTimeMillis() - start_ms));
			}
		} catch (Exception sqlex) {
			MyTimeLogger.getInstance().error(sqlex.getMessage());
			throw new MyTimeException(sqlex.getMessage());
		} finally {
			if (null != connection) {
				connection.close();
				statement.close();
				resultSet.close();
			}
		}
		return result;
	}

	private File fetchRemoteFilesAndCopyToLocal() throws MyTimeException {
		File Finalfile = null;
		try {
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
		} catch (Exception e) {
			MyTimeLogger.getInstance().error(e.getMessage());
			throw new MyTimeException(e.getMessage());
		}

		return Finalfile;
	}

	public List<EmpLoginData> fetchEmployeeLoginsBasedOnDates(long employeeId, String fromDate, String toDate)
			throws MyTimeException {
		long start_ms = System.currentTimeMillis();
		Query query = null;
		float countHours = 0;
		float countMin = 0;
		List<EmpLoginData> listOfEmpLoginData = new ArrayList<>();

		if (employeeId > 0) {

			BasicDBObject gtQuery = new BasicDBObject();
			gtQuery.put("_id",
					new BasicDBObject("$gt", employeeId + "-" + fromDate).append("$lt", employeeId + "-" + toDate));

			cursor = mongoTemplate.getCollection("EmployeesLoginData").find(gtQuery)
					.sort(new BasicDBObject(MyTimeUtils.DATE_OF_LOGIN, -1));

			while (cursor.hasNext()) {
				DBObject dbObject = cursor.next();
				EmpLoginData empLoginData = new EmpLoginData();
				empLoginData.setEmployeeId(dbObject.get("employeeId").toString());
				empLoginData.setEmployeeName(dbObject.get("employeeName").toString());
				empLoginData.setDateOfLogin(dbObject.get("dateOfLogin").toString());
				empLoginData.setFirstLogin(dbObject.get("firstLogin").toString());
				empLoginData.setLastLogout(dbObject.get("lastLogout").toString());
				empLoginData.setTotalLoginTime(dbObject.get("totalLoginTime").toString());
				String[] s = empLoginData.getTotalLoginTime().split(":");
				countHours = countHours + (Integer.valueOf(s[0]));
				countMin = countMin + (Integer.valueOf(s[1]));

				listOfEmpLoginData.add(empLoginData);

			}
			float totalAvgTime = (float) (countHours + (countMin / 60)) / listOfEmpLoginData.size();

			listOfEmpLoginData.get(0).setTotalAvgTime(String.valueOf(totalAvgTime).replace(".", ":"));

			MyTimeLogger.getInstance()
					.info(" Total Time Taken for with id based " + (System.currentTimeMillis() - start_ms));

		} else if (employeeId == 0) {
			query = new Query(Criteria.where(MyTimeUtils.DATE_OF_LOGIN).gte(fromDate).lte(toDate));
			query.with(new Sort(new Order(Direction.ASC, MyTimeUtils.EMPLOYEE_ID),
					new Order(Direction.DESC, MyTimeUtils.DATE_OF_LOGIN)));

			MyTimeLogger.getInstance().info(" Total Time Taken for with fecth All Employee based on Dates :::  "
					+ (System.currentTimeMillis() - start_ms));

			listOfEmpLoginData = mongoTemplate.find(query, EmpLoginData.class);
		}
		return listOfEmpLoginData;
	}

	private void calculatingEachEmployeeLoginsByDate(List<EmpLoginData> loginsData, Map<String, EmpLoginData> empMap)
			throws MyTimeException {
		boolean first = true;
		List<String> dates = new ArrayList<>();
		List<String> firstAndLastLoginDates = new ArrayList<>();
		Map<String, EmpLoginData> internalEmpMap = new HashMap<>();
		Collections.sort(loginsData, new DateCompare());
		int count = 0;
		String employeeId = loginsData.get(0).getEmployeeId();
		try {
			for (EmpLoginData empLoginData : loginsData) {
				count++;
				if (first) {
					firstLoginAndLastRecordAdding(empLoginData, dates, firstAndLastLoginDates);
					internalEmpMap.put(dateOnly, empLoginData);
					first = false;
				} else {
					empDatestr = empLoginData.getFirstLogin();
					Date dt = MyTimeUtils.df.parse(empDatestr);
					String timeOnly = MyTimeUtils.tdf.format(dt);
					if (dt.after(currentDay) && dt.before(nextCurrentDay)) {
						dates.add(timeOnly);
						firstAndLastLoginDates.add(MyTimeUtils.df.parse(empDatestr) + StringUtils.EMPTY);
						if (count == loginsData.size()) {
							addingEmpDatesBasedonLogins(empLoginData, dates, firstAndLastLoginDates, internalEmpMap);
							internalEmpMap.get(dateOnly).setId(employeeId + MyTimeUtils.HYPHEN + dateOnly);
							empMap.put(employeeId + MyTimeUtils.HYPHEN + dateOnly, internalEmpMap.get(dateOnly));
						}
					} else {
						EmpLoginData empLoginData1 = internalEmpMap.get(dateOnly);
						addingEmpDatesBasedonLogins(empLoginData1, dates, firstAndLastLoginDates, internalEmpMap);
						internalEmpMap.get(dateOnly).setId(employeeId + MyTimeUtils.HYPHEN + dateOnly);
						empMap.put(employeeId + MyTimeUtils.HYPHEN + dateOnly, internalEmpMap.get(dateOnly));
						firstLoginAndLastRecordAdding(empLoginData, dates, firstAndLastLoginDates);
						internalEmpMap.put(dateOnly, empLoginData);
						if (count == loginsData.size()) {
							addingEmpDatesBasedonLogins(empLoginData, dates, firstAndLastLoginDates, internalEmpMap);
							internalEmpMap.get(dateOnly).setId(employeeId + MyTimeUtils.HYPHEN + dateOnly);
							empMap.put(employeeId + MyTimeUtils.HYPHEN + dateOnly, internalEmpMap.get(dateOnly));
						}
					}
				}
			}
		} catch (ParseException e) {
			MyTimeLogger.getInstance().error(e.getMessage());
			throw new MyTimeException(e.getMessage());
		}
	}

	private void firstLoginAndLastRecordAdding(EmpLoginData empLoginData, List<String> dates,
			List<String> firstAndLastLoginDates) throws MyTimeException {
		try {
			empDatestr = empLoginData.getFirstLogin();
			Date dt;
			dt = MyTimeUtils.df.parse(empDatestr);
			String timeOnly = MyTimeUtils.tdf.format(dt);
			dateOnly = MyTimeUtils.dfmt.format(dt);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(dt);
			calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
					6, 00, 00);
			currentDay = calendar.getTime();
			nextCurrentDay = DateUtils.addHours(currentDay, 24);
			if (dt.after(currentDay) && dt.before(nextCurrentDay)) {
				empLoginData.setDateOfLogin(dateOnly);
				dates.add(timeOnly);
				firstAndLastLoginDates.add(MyTimeUtils.df.parse(empDatestr) + StringUtils.EMPTY);
			}
		} catch (ParseException e) {
			MyTimeLogger.getInstance().error(e.getMessage());
			throw new MyTimeException(e.getMessage());
		}
	}

	private EmpLoginData addingEmpDatesBasedonLogins(EmpLoginData empLoginData, List<String> dates,
			List<String> firstAndLastLoginDates, Map<String, EmpLoginData> empMap) throws MyTimeException {
		String roundingMinutes = null;
		try {
			if (!dates.isEmpty()) {
				String min = dates.get(0);
				String max = dates.get(dates.size() - 1);
				empLoginData.setFirstLogin(min);
				empLoginData.setLastLogout(max);
				dates.clear();
			}
			if (!firstAndLastLoginDates.isEmpty()) {
				DateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
				Date calMinDt = (Date) formatter.parse(firstAndLastLoginDates.get(0));
				Date calMaxDt = (Date) formatter.parse(firstAndLastLoginDates.get(firstAndLastLoginDates.size() - 1));

				long calDtDiffHours = (calMaxDt.getTime() - calMinDt.getTime());
				int seconds = ((int) calDtDiffHours) / 1000;
				int hours = seconds / 3600;
				int minutes = (seconds % 3600) / 60;
				if (minutes < 10) {
					roundingMinutes = MyTimeUtils.ZERO + minutes;
					empLoginData.setTotalLoginTime(hours + MyTimeUtils.COLON + roundingMinutes);
				} else {
					empLoginData.setTotalLoginTime(hours + MyTimeUtils.COLON + minutes);
				}
				firstAndLastLoginDates.clear();
			}
			empLoginData.setDateOfLogin(dateOnly);
			empMap.put(dateOnly, empLoginData);
		} catch (Exception e) {
			MyTimeLogger.getInstance().error(e.getMessage());
			throw new MyTimeException(e.getMessage());
		}
		return empLoginData;
	}

	private void getSingleEmploginData(List<EmpLoginData> loginsData, Map<String, List<EmpLoginData>> map,
			EmpLoginData empLoginData) throws ParseException {
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