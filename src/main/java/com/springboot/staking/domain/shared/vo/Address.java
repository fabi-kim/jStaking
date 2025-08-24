package com.springboot.staking.domain.shared.vo;

import java.util.Objects;

public record Address(String value) {
  
  public Address {
    Objects.requireNonNull(value, "Address cannot be null");
    if (value.trim().isEmpty()) {
      throw new IllegalArgumentException("Address cannot be empty");
    }
  }
  
  public static Address of(String value) {
    return new Address(value.trim());
  }
  
  @Override
  public String toString() {
    return value;
  }
}