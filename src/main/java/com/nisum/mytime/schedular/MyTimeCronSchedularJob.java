package com.nisum.mytime.schedular;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

import com.nisum.mytime.exception.handler.MyTimeException;
import com.nisum.mytime.utils.MyTimeLogger;

@DisallowConcurrentExecution
public class MyTimeCronSchedularJob implements Job {

	@Autowired
	private AttendanceDataService attendanceDataService;

	@Override
	public void execute(JobExecutionContext jobExecutionContext) {
		try {
			attendanceDataService.populateEmployeeAttendanceData();

		} catch (MyTimeException e) {
			MyTimeLogger.getInstance().info(e.getMessage());
		}
	}
}
