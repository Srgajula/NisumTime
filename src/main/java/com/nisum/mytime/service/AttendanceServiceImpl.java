package com.nisum.mytime.service;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.nisum.mytime.configuration.DbConnection;
import com.nisum.mytime.exception.handler.MyTimeException;
import com.nisum.mytime.model.AttendenceData;
import com.nisum.mytime.utils.MyTimeLogger;
import com.nisum.mytime.utils.MyTimeUtils;

@Service
public class AttendanceServiceImpl implements AttendanceService {

	@Value("${mytime.attendance.fileName}")
	private String mdbFile;

	@Value("${mytime.remote.directory}")
	private String remoteFilesDirectory;

	@Autowired
	private MongoTemplate mongoTemplate;

	private Connection connection = null;
	private Statement statement = null;
	private ResultSet resultSet = null;
	private List<AttendenceData> listOfAbsentEmployees = null;
	private StringBuilder queryMonthDecider = null;
	private List<String> presentiesList = null;
	private File finalfile = null;
	private List<String> listOfPresentEmployees = new ArrayList<>();
	private Calendar calendar = new GregorianCalendar();
	private int month = (calendar.get(Calendar.MONTH)) + 1;
	private int year = calendar.get(Calendar.YEAR);
	private Date toDay = calendar.getTime();

	@Override
	public List<AttendenceData> getAttendanciesReport(String reportDate) throws MyTimeException {
		long start_ms = System.currentTimeMillis();

		try {
			File dir = new File(remoteFilesDirectory);
			for (File file : dir.listFiles()) {
				if (file.getCanonicalPath().contains(mdbFile)) {
					finalfile = new File(file.getCanonicalPath());
				}
			}
			if (null != finalfile) {
				int dayOftoDay = calendar.get(Calendar.DAY_OF_MONTH);
				Date selectedDate = MyTimeUtils.dfmt.parse(reportDate);
				calendar.setTime(MyTimeUtils.dfmt.parse(reportDate));
				int dayOfSelectedDate = calendar.get(Calendar.DAY_OF_MONTH);

				if (dayOfSelectedDate == dayOftoDay) {
					fecthRecordsFromMDb();
				} else if (selectedDate.before(toDay)) {
					calendar.setTime(selectedDate);
					fecthRecordsFromMongoDb(reportDate);
				}
				MyTimeLogger.getInstance().info("Time Taken for " + (System.currentTimeMillis() - start_ms));
			}
		} catch (Exception e) {
			MyTimeLogger.getInstance().error(e.getMessage());
			throw new MyTimeException(e.getMessage());
		}
		return listOfAbsentEmployees;
	}
	
	private void createDbStatementWithFile() throws MyTimeException {
		try {
			queryMonthDecider = new StringBuilder();
			String dbURL = MyTimeUtils.driverUrl + finalfile.getCanonicalPath();
			MyTimeLogger.getInstance().info(dbURL);
			connection = DbConnection.getDBConnection(dbURL);
			statement = connection.createStatement();

			Calendar calendar1 = Calendar.getInstance();
			calendar1.set(year, (month - 1), calendar.get(Calendar.DAY_OF_MONTH), 6, 00, 00);
			Date dayStartsTime = calendar1.getTime();

			Date maxTimeToLogin = DateUtils.addHours(dayStartsTime, 12);

			queryMonthDecider.append(MyTimeUtils.USERID_QUERY);
			queryMonthDecider.append(month);
			queryMonthDecider.append(MyTimeUtils.UNDER_SCORE);
			queryMonthDecider.append(year);
			queryMonthDecider.append(MyTimeUtils.WHERE_COND);
			queryMonthDecider.append(MyTimeUtils.df.format(dayStartsTime) + MyTimeUtils.SINGLE_QUOTE);
			queryMonthDecider.append(MyTimeUtils.AND_COND);
			queryMonthDecider.append(MyTimeUtils.df.format(maxTimeToLogin) + MyTimeUtils.SINGLE_QUOTE);

			MyTimeLogger.getInstance().info(queryMonthDecider.toString());

		} catch (Exception e) {
			MyTimeLogger.getInstance().error(e.getMessage());
			throw new MyTimeException(e.getMessage());
		}
	}

	private void fecthRecordsFromMDb() throws MyTimeException, SQLException {
		presentiesList=new ArrayList<>();
		try {
			createDbStatementWithFile();
			resultSet = statement.executeQuery(queryMonthDecider.toString());
			while (resultSet.next()) {
				if (resultSet.getString(1).length() >= 5) {
					listOfPresentEmployees.add(resultSet.getString(1));
				}
			}
			presentiesList = listOfPresentEmployees.stream().distinct().collect(Collectors.toList());
			fetchAbsenteesListFromDb();
		} catch (Exception e) {
			MyTimeLogger.getInstance().error(e.getMessage());
			throw new MyTimeException(e.getMessage());
		} finally {
			if (null != connection) {
				connection.close();
				statement.close();
				resultSet.close();
			}
		}
	}

	private void fecthRecordsFromMongoDb(String reportDate) throws MyTimeException {
		DBCursor cursor = null;
		presentiesList=new ArrayList<>();
		createDbStatementWithFile();
		BasicDBObject gtQuery = new BasicDBObject();
		gtQuery.put(MyTimeUtils.DATE_OF_LOGIN, reportDate);

		cursor = mongoTemplate.getCollection(MyTimeUtils.EMPLOYEE_COLLECTION).find(gtQuery)
				.sort(new BasicDBObject(MyTimeUtils.EMPLOYEE_ID, 1));
		while (cursor.hasNext()) {
			DBObject dbObject = cursor.next();
			listOfPresentEmployees.add(dbObject.get(MyTimeUtils.EMPLOYEE_ID).toString());
		}
		presentiesList = listOfPresentEmployees.stream().distinct().collect(Collectors.toList());
		fetchAbsenteesListFromDb();
	}

	private void fetchAbsenteesListFromDb() throws MyTimeException {
		try {
			listOfAbsentEmployees = new ArrayList<>();
			StringBuilder absentiesQuery = new StringBuilder();
			absentiesQuery.append(MyTimeUtils.ABESENT_QUERY);
			absentiesQuery.append(queryMonthDecider.toString());
			absentiesQuery.append(MyTimeUtils.ABESENT_QUERY1);
			MyTimeLogger.getInstance().info(absentiesQuery.toString());

			resultSet = statement.executeQuery(absentiesQuery.toString());
			while (resultSet.next()) {
				if (resultSet.getString(3).length() >= 5) {
					AttendenceData attendenceData = new AttendenceData();
					attendenceData.setEmployeeName(resultSet.getString(2));
					attendenceData.setEmployeeId(resultSet.getString(3));
					attendenceData.setIfPresent(MyTimeUtils.ABESENT);
					listOfAbsentEmployees.add(attendenceData);
				}
			}
			listOfAbsentEmployees.get(0).setTotalPresent(presentiesList.size());
			listOfAbsentEmployees.get(0).setTotalAbsent(listOfAbsentEmployees.size());

		} catch (Exception e) {
			MyTimeLogger.getInstance().error(e.getMessage());
			throw new MyTimeException(e.getMessage());
		}

	}
}
