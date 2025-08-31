package com.springboot.staking.quartz;

import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "quartz")
@Data
public class QuartzProperties {
    private List<JobConfig> jobs;

    @Data
    public static class JobConfig {
        private String name;
        private String className;
        private TriggerConfig trigger;
    }

    @Data
    public static class TriggerConfig {
        private String type; // simple or cron
        private Long intervalMillis;
        private String expression;
    }
}