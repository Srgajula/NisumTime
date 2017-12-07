package com.nisum.mytime.schedular;

import java.io.IOException;
import java.util.Properties;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
@ConditionalOnProperty(name = "quartz.enabled")
public class SchedulerConfig {

	@Value("${cron.expression}")
	private String crosExp;

	@Bean
	public JobFactory jobFactory(ApplicationContext applicationContext) {
		AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
		jobFactory.setApplicationContext(applicationContext);
		return jobFactory;
	}

	@Bean
	public Scheduler schedulerFactoryBean(JobFactory jobFactory,
			@Qualifier("myTimeJobTrigger") Trigger myTimeJobTrigger) throws Exception {
		SchedulerFactoryBean factory = new SchedulerFactoryBean();
		factory.setOverwriteExistingJobs(true);
		factory.setJobFactory(jobFactory);

		factory.setQuartzProperties(quartzProperties());
		factory.afterPropertiesSet();

		Scheduler scheduler = factory.getScheduler();
		scheduler.setJobFactory(jobFactory);
		scheduler.scheduleJob((JobDetail) myTimeJobTrigger.getJobDataMap().get("jobDetail"), myTimeJobTrigger);

		scheduler.start();
		return scheduler;
	}

	@Bean
	public Properties quartzProperties() throws IOException {
		PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
		propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
		propertiesFactoryBean.afterPropertiesSet();
		return propertiesFactoryBean.getObject();
	}

	@Bean
	public JobDetailFactoryBean myTimeJobDetail() {
		return createJobDetail(MyTimeCronSchedularJob.class);
	}

	@Bean(name = "myTimeJobTrigger")
	public CronTriggerFactoryBean myTimeJobTrigger(@Qualifier("myTimeJobDetail") JobDetail jobDetail,
			@Value("${myTimejob.frequency}") long frequency) {
		return createCronTrigger(jobDetail, frequency);
	}

	private JobDetailFactoryBean createJobDetail(Class<MyTimeCronSchedularJob> jobClass) {
		JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
		factoryBean.setJobClass(jobClass);
		factoryBean.setDurability(true);
		return factoryBean;
	}

	private CronTriggerFactoryBean createCronTrigger(JobDetail jobDetail, long cronExpression) {
		CronTriggerFactoryBean factoryBean = new CronTriggerFactoryBean();
		factoryBean.setJobDetail(jobDetail);
		factoryBean.setCronExpression(crosExp);
		factoryBean.setMisfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
		return factoryBean;
	}
}