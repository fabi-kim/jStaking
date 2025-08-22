package com.springboot.staking.common.config;

import com.springboot.staking.common.annotation.RequestIdHeaderArgumentResolver;
import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final RequestIdHeaderArgumentResolver requestIdHeaderArgumentResolver;

    public WebConfig(RequestIdHeaderArgumentResolver resolver) {
        this.requestIdHeaderArgumentResolver = resolver;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(requestIdHeaderArgumentResolver);
    }
}