package com.springboot.staking.common.annotation;

import java.util.UUID;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class RequestIdHeaderArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(RequestIdHeader.class)
                && UUID.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory)
        throws MissingRequestHeaderException {

        RequestIdHeader ann = parameter.getParameterAnnotation(RequestIdHeader.class);
        String headerName = ann.name();
        String raw = webRequest.getHeader(headerName);

        if ((raw == null || raw.isBlank())) {
            if (ann.required()) {
                throw new MissingRequestHeaderException(headerName, parameter);
            }
            return null;
        }

        try {
            return UUID.fromString(raw);
        } catch (IllegalArgumentException e) {
            // 400을 유도하려면 적절한 예외를 던집니다.
            throw new IllegalArgumentException("Invalid UUID in header '" + headerName + "': " + raw, e);
        }
    }
}