package com.springboot.staking.domain.shared.vo;

import java.util.Objects;
import java.util.UUID;

public record RequestId(UUID value) {
  
  public RequestId {
    Objects.requireNonNull(value, "RequestId cannot be null");
  }
  
  public static RequestId of(UUID value) {
    return new RequestId(value);
  }
  
  public static RequestId generate() {
    return new RequestId(UUID.randomUUID());
  }
  
  @Override
  public String toString() {
    return value.toString();
  }
}