package com.springboot.staking.shared.constant;

public enum EthRequestMethod {
  GET_BALANCE("eth_getBalance");

  private final String method;

  EthRequestMethod(String method) {
    this.method = method;
  }

  public String getMethod() {
    return method;
  }
}
