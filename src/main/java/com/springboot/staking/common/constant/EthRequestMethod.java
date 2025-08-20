package com.springboot.staking.common.constant;

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
