package com.ritesh.scheduler.config;

import java.util.Properties;
import javax.sql.DataSource;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
public class QuartzConfig {

  @Autowired
  @Qualifier("dataSource")
  private DataSource dataSource;

  @Bean
  public JobFactory jobFactory(ApplicationContext applicationContext) {

    QuartzJobFactory sampleJobFactory = new QuartzJobFactory();
    sampleJobFactory.setApplicationContext(applicationContext);
    return sampleJobFactory;
  }

  @Bean
  public SchedulerFactoryBean schedulerFactoryBean(ApplicationContext applicationContext) {

    SchedulerFactoryBean factory = new SchedulerFactoryBean();

    factory.setOverwriteExistingJobs(true);
    factory.setJobFactory(jobFactory(applicationContext));

    Properties quartzProperties = new Properties();
    quartzProperties.setProperty("org.quartz.scheduler.instanceName", "background-scheduler");
    quartzProperties.setProperty("org.quartz.scheduler.instanceId", "AUTO");
    quartzProperties.setProperty("org.quartz.threadPool.threadCount", "10");
    quartzProperties.setProperty("org.quartz.threadPool.threadPriority", "5");
    quartzProperties.setProperty(
        "org.quartz.jobStore.class",
        "org.springframework.scheduling.quartz.LocalDataSourceJobStore");
    quartzProperties.setProperty(
        "org.quartz.jobStore.driverDelegateClass", "org.quartz.impl.jdbcjobstore.StdJDBCDelegate");

    quartzProperties.setProperty("org.quartz.jobStore.isClustered", "true");
    quartzProperties.setProperty("org.quartz.jobStore.clusterCheckinInterval", "20000");

    factory.setDataSource(dataSource);

    factory.setQuartzProperties(quartzProperties);

    return factory;
  }

  @Bean
  public Scheduler quartzScheduler(ApplicationContext applicationContext)
      throws SchedulerException {
    Scheduler scheduler = schedulerFactoryBean(applicationContext).getScheduler();
    scheduler.start();
    return scheduler;
  }
}
