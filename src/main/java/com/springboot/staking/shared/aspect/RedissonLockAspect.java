package com.springboot.staking.shared.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.staking.shared.annotation.RedissonLock;
import com.springboot.staking.shared.constant.ErrorCode;
import com.springboot.staking.shared.exception.ApplicationException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class RedissonLockAspect {

  private final Logger logger = LoggerFactory.getLogger(RedissonLockAspect.class);
  private final RedissonClient redissonClient;
  private final AopForTransaction aopForTransaction;
  private final ObjectMapper objectMapper;
  @Value("${spring.cache.redis.key-prefix}")
  private String prefix;

  @Around("@annotation(com.springboot.staking.shared.annotation.RedissonLock)")
  public Object redissonLock(ProceedingJoinPoint joinPoint) throws Throwable {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();
    RedissonLock annotation = method.getAnnotation(RedissonLock.class);
    String lockKey =
        prefix + method.getName();
    if (annotation.keys().length > 0) {
      List<String> keyParts = new ArrayList<>();
      for (String key : annotation.keys()) {
        // 각 key마다 기존 getDynamicValue 메서드 재사용
        Object value = getDynamicValue(signature.getParameterNames(), joinPoint.getArgs(), key);
        keyParts.add(convertToString(value));
      }
      lockKey += "::" + String.join("::", keyParts);
    }

    RLock lock = redissonClient.getLock(lockKey);
    String errMsg = Optional.ofNullable(annotation.error())
        .filter(s -> !s.isBlank())
        .orElse("잠시후 다시 시도하세요.");
    boolean locked = false;
    try {
      locked = lock.tryLock(annotation.waitTime(), annotation.leaseTime(),
          TimeUnit.MILLISECONDS);
      if (!locked) {
        logger.info("Lock 획득 실패={}", lockKey);
        throw new ApplicationException(ErrorCode.LOCK_ERROR, errMsg);
      }
      logger.info("로직 수행");
      return aopForTransaction.proceed(joinPoint);
    } catch (InterruptedException e) {
      logger.info("에러 발생");
      throw e;
    } finally {
      try {
        lock.unlock();   // (4)
      } catch (IllegalMonitorStateException e) {
        logger.info("Redisson Lock Already UnLock {} {}", method.getName(), lockKey);
      }
    }
  }

  Object getDynamicValue(String[] parameterNames, Object[] args, String key) {
    SpelExpressionParser parser = new SpelExpressionParser();
    StandardEvaluationContext context = new StandardEvaluationContext();

    for (int i = 0; i < parameterNames.length; i++) {
      context.setVariable(parameterNames[i], args[i]);
    }

    return parser.parseExpression(key).getValue(context, Object.class);
  }

  private String convertToString(Object value) {
    if (value == null) {
      return "null";
    }

    // 기본 타입들은 그대로 toString()
    if (value instanceof String || value instanceof Number || value instanceof Boolean
        || value.getClass().isEnum()) {
      return String.valueOf(value);
    }

    // 객체는 JSON으로 변환
    try {
      return objectMapper.writeValueAsString(value);
    } catch (Exception e) {
      logger.warn("Failed to convert object to JSON, using toString(): {}", e.getMessage());
      return String.valueOf(value);
    }
  }
}
