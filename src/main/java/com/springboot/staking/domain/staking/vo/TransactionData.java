package com.springboot.staking.domain.staking.vo;

import java.util.Objects;

public record TransactionData(String value) {
  
  public TransactionData {
    Objects.requireNonNull(value, "TransactionData cannot be null");
  }
  
  public static TransactionData of(String value) {
    return new TransactionData(value);
  }
  
  public static TransactionData empty() {
    return new TransactionData("");
  }
  
  public boolean isEmpty() {
    return value == null || value.trim().isEmpty();
  }
  
  @Override
  public String toString() {
    return value;
  }
}