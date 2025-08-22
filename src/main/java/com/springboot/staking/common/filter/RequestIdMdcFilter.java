package com.springboot.staking.common.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestIdMdcFilter extends OncePerRequestFilter {

  public static final String HEADER = "X-Request-Id"; // 클라이언트가 보내는 헤더 이름
  public static final String MDC_KEY = "requestId";

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                  FilterChain chain) throws ServletException, IOException {
    String rid = request.getHeader(HEADER);
    // 없으면 생성(선택)
    /*
        String rid = Optional.ofNullable(request.getHeader(HEADER))
                         .orElse(UUID.randomUUID().toString()); // 없으면 생성(선택)
    * */
    MDC.put(MDC_KEY, rid);
    try {
      response.setHeader(HEADER, rid); // 응답에도 에코(편의)
      chain.doFilter(request, response);
    } finally {
      MDC.remove(MDC_KEY); // 누수 방지
    }
  }
}