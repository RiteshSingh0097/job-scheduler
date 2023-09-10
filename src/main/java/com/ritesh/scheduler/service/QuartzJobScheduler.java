package com.ritesh.scheduler.service;

import com.cronutils.mapper.CronMapper;
import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.parser.CronParser;
import com.ritesh.scheduler.dto.QuartzJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.util.TimeZone;

@Slf4j
@Service
public class QuartzJobScheduler {

    @Autowired
    @Qualifier("quartzScheduler")
    private Scheduler scheduler;

    public void replaceJob(QuartzJob job) {
        try {
            if (scheduler.checkExists(new JobKey(job.getKey()))) {
                deleteJob(job);
            }
            scheduleJob(job);
        } catch (SchedulerException e) {
            log.error("Failed to schedule job with key : {}", job.getKey());
        }
    }

    public void deleteJob(QuartzJob job) {

        try {
            scheduler.unscheduleJob(new TriggerKey(job.getKey()));
        } catch (SchedulerException e) {
            log.error("Failed to unscheduled job with key : {}", job.getKey());
        }

        try {
            scheduler.deleteJob(new JobKey(job.getKey()));
        } catch (SchedulerException e) {
            log.error("Failed to delete job with key : {}", job.getKey());
        }
    }

    private void scheduleJob(QuartzJob job) {

        JobDetail jobDetail =
                JobBuilder.newJob(JobScheduler.class)
                        .withIdentity(job.getKey())
                        .withDescription(job.getDescription())
                        .storeDurably()
                        .build();

        try {
            scheduler.deleteJob(jobDetail.getKey());
        } catch (SchedulerException e) {
            log.error("Failed to delete", e);
        }

        CronMapper cronMapper = CronMapper.fromUnixToQuartz();

        CronParser parser = new CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.UNIX));
        Cron unixCron = parser.parse(job.getCronExpression());
        Cron quartzCron = cronMapper.map(unixCron);

        String quartzString = quartzCron.asString();
        log.debug(
                "Quartz expression {} from unix expression {}", quartzString, job.getCronExpression());

        Trigger trigger =
                TriggerBuilder.newTrigger()
                        .forJob(jobDetail)
                        .withIdentity(job.getKey())
                        .withDescription(job.getDescription())
                        .withSchedule(
                                CronScheduleBuilder.cronSchedule(quartzString)
                                        .inTimeZone(TimeZone.getTimeZone(ZoneOffset.UTC))
                                        .withMisfireHandlingInstructionDoNothing())
                        .build();

        try {
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            log.error(e.getMessage());
        }
    }
}
