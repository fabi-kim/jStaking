package com.springboot.staking.shared.constant;

public class RedisKey {

  public static final String GRACEFUL = "GRACEFUL";

  public static final class NODE {

    private static final String prefix = "node::";
    public static final String BALANCE = prefix + "balance";
    public static final String TX = prefix + "tx";
  }

}