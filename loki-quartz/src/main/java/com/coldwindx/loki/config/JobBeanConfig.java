package com.coldwindx.loki.config;

import com.coldwindx.loki.job.MyJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JobBeanConfig {
    @Bean
    public JobDetail jobDetail() {
        return JobBuilder.newJob(MyJob.class)
                .storeDurably()
                .withIdentity("task-name-1", "group-1")
                .usingJobData("count", 0)
                .build();
    }

    @Bean
    public Trigger trigger() {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail())
                .withIdentity("trigger-name-1", "group-1")
                .startNow()
                .withSchedule(CronScheduleBuilder.cronSchedule("0/2 * * * * ? *"))
                .build();
    }
}
