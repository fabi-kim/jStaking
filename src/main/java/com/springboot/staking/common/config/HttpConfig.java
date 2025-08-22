package com.springboot.staking.common.config;

import com.springboot.staking.common.filter.RequestIdMdcFilter;
import org.slf4j.MDC;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.springboot.staking.adaptor.proxy")
public class HttpConfig {

  @Bean
  public feign.RequestInterceptor feignMdcInterceptor() {
    return template -> {
      String rid = MDC.get(RequestIdMdcFilter.MDC_KEY);
      if (rid != null) {
        template.header("request-id", rid);
      }
    };
  }
}
