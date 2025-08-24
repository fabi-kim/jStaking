package com.springboot.staking.domain.staking.vo;

import java.util.Objects;

public record StakingTransactionId(Long value) {
  
  public StakingTransactionId {
    Objects.requireNonNull(value, "StakingTransactionId cannot be null");
  }
  
  public static StakingTransactionId of(Long value) {
    return new StakingTransactionId(value);
  }
  
  @Override
  public String toString() {
    return value.toString();
  }
}