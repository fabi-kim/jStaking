package com.springboot.staking.quartz;

import com.springboot.staking.common.exception.ApplicationException;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

@Slf4j
public class GlobalJobListener implements JobListener {
    @Override
    public String getName() {
        return "globalJobListener";
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        log.info("Quartz jobToBeExecuted, JobExecutionContext: {}", context);

    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        log.info("Quartz jobExecutionVetoed, JobExecutionContext: {}", context);

    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        if (jobException != null) {
            Throwable cause = jobException.getCause(); // 원래 Job에서 던진 예외
            if (cause instanceof ApplicationException) {
                log.error("jobWasExecuted > ApplicationException 발생: {}", cause.getMessage(), cause);
                // 여기서 알림/처리
            } else {
                log.error("Quartz Job 실패: {}", jobException.getMessage(), jobException);
            }
        }
    }
}