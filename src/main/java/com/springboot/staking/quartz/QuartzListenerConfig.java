package com.springboot.staking.quartz;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzListenerConfig {

    @Bean
    public SchedulerListener schedulerListener(Scheduler scheduler) throws SchedulerException {
        scheduler.getListenerManager().addJobListener(new GlobalJobListener());
        return null;
    }
}