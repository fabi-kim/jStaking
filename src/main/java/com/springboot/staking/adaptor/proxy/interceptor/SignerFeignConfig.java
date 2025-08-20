package com.springboot.staking.adaptor.proxy.interceptor;

import feign.RequestInterceptor;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SignerFeignConfig {

  @Value("${external.proxy.signer.id}")
  private String id;
  @Value("${external.proxy.signer.pw}")
  private String pw;

  public class BasicAuthConfig {

    @Bean
    public RequestInterceptor celestiaInterceptor() {
      return requestTemplate -> {
        String basicAuth = "Basic " + Base64.getEncoder()
            .encodeToString((id + ":" + pw).getBytes());

        requestTemplate.header("Authorization", basicAuth);
        requestTemplate.header("admin-id", "plus-batch");
      };
    }
  }


}