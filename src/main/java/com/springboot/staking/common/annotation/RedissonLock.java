package com.springboot.staking.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RedissonLock {

  String[] keys() default {}; // Lock의 이름 (고유값)

  String error() default "";
  
  long waitTime() default 100L; // Lock획득을 시도하는 최대 시간 (ms)

  long leaseTime() default 60000L; // 락을 획득한 후, 점유하는 최대 시간 (ms)
}
