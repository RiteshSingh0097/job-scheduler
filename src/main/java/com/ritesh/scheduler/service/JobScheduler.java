package com.ritesh.scheduler.service;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class JobScheduler extends QuartzJobBean {
    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {

        String uniqueIdentifier = jobExecutionContext.getJobDetail().getKey().getName();

        log.info("Scheduled job details :: {}", uniqueIdentifier);
    }
}
