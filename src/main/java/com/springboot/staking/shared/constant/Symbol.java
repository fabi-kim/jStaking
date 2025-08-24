package com.springboot.staking.shared.constant;

public enum Symbol {
  ATOM("ATOM"), ETH("ETH"), TIA("TIA");

  public static final String ETH_BEAN = "ETH";
  public static final String TIA_BEAN = "TIA";
  private final String symbol;

  Symbol(String symbol) {
    this.symbol = symbol;
  }

  public String getSymbol() {
    return symbol;
  }
}
