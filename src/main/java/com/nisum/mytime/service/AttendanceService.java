package com.nisum.mytime.service;

import java.sql.SQLException;
import java.util.List;

import com.nisum.mytime.exception.handler.MyTimeException;
import com.nisum.mytime.model.AttendenceData;

public interface AttendanceService {

	List<AttendenceData> getAttendanciesReport(String reportDate) throws MyTimeException, SQLException;

	Boolean copyRemoteMdbFileToLocal() throws MyTimeException;

}
