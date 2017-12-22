package com.nisum.mytime.configuration;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ReportsCleanUpScheduler {

	private static final Logger logger = LoggerFactory.getLogger(ReportsCleanUpScheduler.class);
	
	@Autowired
	ResourceLoader resourceLoader;
	
	@Scheduled(cron= "0 0/55 23 * * *")
	public boolean cleanReports(){
		boolean flag = false;
		try {
			File dir = resourceLoader.getResource("/WEB-INF/reports/").getFile();
			for(File file : dir.listFiles()){
				String fileName = file.getName();
				if(file.exists() && !"MyTime.pdf".equals(fileName)){
					flag = file.delete();
				}
			}
			logger.info("Reports cleanup performed successfully");
		} catch (IOException e) {
			logger.error("Report deletion failed due to: ");
		}
		return flag;
	}
}
