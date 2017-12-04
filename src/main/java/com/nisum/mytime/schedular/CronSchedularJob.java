package com.nisum.mytime.schedular;

import java.io.IOException;
import java.text.ParseException;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

@DisallowConcurrentExecution
public class CronSchedularJob implements Job {

	@Autowired
	private FilePickingSchedular filePickingSchedular;

	@Override
	public void execute(JobExecutionContext jobExecutionContext) {
		try {

			filePickingSchedular.fetchEmployeesData();

		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}

	}

}
