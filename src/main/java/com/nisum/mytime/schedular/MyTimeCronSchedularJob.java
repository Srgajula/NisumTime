package com.nisum.mytime.schedular;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

import com.nisum.mytime.exception.handler.MyTimeException;
import com.nisum.mytime.service.EmployeeDataService;
import com.nisum.mytime.utils.MyTimeLogger;

@DisallowConcurrentExecution
public class MyTimeCronSchedularJob implements Job {

	@Autowired
	private EmployeeDataService employeeDataService;

	@Override
	public void execute(JobExecutionContext jobExecutionContext) {
		try {
			if (employeeDataService.fetchEmployeesDataOnDayBasis()) {
				MyTimeLogger.getInstance().info("Shedular Executed Successfully Records Saved in DB");
			}
		} catch (MyTimeException e) {
			MyTimeLogger.getInstance().error("Shedular failed to Executed ::: " + e.getMessage());
		}
	}
}
