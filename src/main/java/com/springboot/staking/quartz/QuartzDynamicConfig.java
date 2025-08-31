package com.springboot.staking.quartz;

import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.quartz.SchedulerFactoryBeanCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

@Configuration
public class QuartzDynamicConfig {

    private final QuartzProperties quartzProperties;
    private final AutowireCapableBeanFactory beanFactory;

    public QuartzDynamicConfig(QuartzProperties quartzProperties, AutowireCapableBeanFactory beanFactory) {
        this.quartzProperties = quartzProperties;
        this.beanFactory = beanFactory;
    }

    @Bean
    public SchedulerFactoryBeanCustomizer schedulerCustomizer() {
        return schedulerFactoryBean -> schedulerFactoryBean.setJobFactory((bundle, scheduler) -> {
            try {
                Class<? extends Job> jobClass = bundle.getJobDetail().getJobClass();
                return beanFactory.createBean(jobClass);
            } catch (Exception e) {
                throw new SchedulerException("Failed to create job instance", e);
            }
        });
    }

    @Bean
    public CommandLineRunner scheduleJobs(Scheduler scheduler) {
        return args -> {
            for (QuartzProperties.JobConfig jobConfig : quartzProperties.getJobs()) {
                Class<? extends Job> jobClass =
                        (Class<? extends Job>) Class.forName(jobConfig.getClassName());

                JobDetail jobDetail = JobBuilder.newJob(jobClass)
                        .withIdentity(jobConfig.getName())
                        .storeDurably()
                        .build();

                Trigger trigger;
                if ("simple".equalsIgnoreCase(jobConfig.getTrigger().getType())) {
                    trigger = TriggerBuilder.newTrigger()
                            .forJob(jobDetail)
                            .withIdentity(jobConfig.getName() + "Trigger")
                            .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                                    .withIntervalInMilliseconds(jobConfig.getTrigger().getIntervalMillis())
                                    .repeatForever())
                            .build();
                } else {
                    trigger = TriggerBuilder.newTrigger()
                            .forJob(jobDetail)
                            .withIdentity(jobConfig.getName() + "Trigger")
                            .withSchedule(CronScheduleBuilder.cronSchedule(jobConfig.getTrigger().getExpression()))
                            .build();
                }

                scheduler.scheduleJob(jobDetail, trigger);
            }
        };
    }
}
