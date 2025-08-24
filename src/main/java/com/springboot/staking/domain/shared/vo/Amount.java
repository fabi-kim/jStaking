package com.springboot.staking.domain.shared.vo;

import java.math.BigDecimal;
import java.util.Objects;

public record Amount(BigDecimal value) {

  public Amount {
    Objects.requireNonNull(value, "Amount cannot be null");
    if (value.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Amount must be greater than zero");
    }
  }

  public static Amount of(String value) {
    return new Amount(new BigDecimal(value));
  }

  public static Amount of(BigDecimal value) {
    return new Amount(value);
  }

  @Override
  public String toString() {
    return value.toString();
  }
}