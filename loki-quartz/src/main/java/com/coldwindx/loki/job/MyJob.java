package com.coldwindx.loki.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.*;

@Slf4j
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class MyJob implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDetail jobDetail = context.getJobDetail();
        log.info("jobDetail={}, fireTime={}", jobDetail, context.getFireTime());

        int count = jobDetail.getJobDataMap().getIntValue("count");
        count++;
        jobDetail.getJobDataMap().put("count", count);
        log.info("count={}", count);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            log.error("MyJob execute error", e);
        }
    }
}
