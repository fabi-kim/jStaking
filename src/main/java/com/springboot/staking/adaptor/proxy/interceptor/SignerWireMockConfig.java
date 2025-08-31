package com.springboot.staking.adaptor.proxy.interceptor;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("localmock")
@Configuration
public class SignerWireMockConfig {

  @Value("${external.mockwire.signer.port:19991}")
  private int port;

  @Bean(initMethod = "start", destroyMethod = "stop")
  public WireMockServer wireMockServer() {
    var options = WireMockConfiguration.options()
        .bindAddress("127.0.0.1")
        .port(port)
        .usingFilesUnderClasspath("wiremock/signer"); // resources/wiremock/signer/{mappings,__files}
    return new WireMockServer(options);
  }
}