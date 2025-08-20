package com.springboot.staking.common.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableCaching
@EnableJpaAuditing
@EnableFeignClients(basePackages = "com.springboot.staking.adaptor.proxy")
public class CommonConfig {

}
