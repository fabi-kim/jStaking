package com.springboot.staking.shared.util;

import java.math.BigInteger;

public class NumberUtil {

  public static String HexToDecimalString(String hex) {
    // "0x" 접두사 제거
    if (hex.startsWith("0x") || hex.startsWith("0X")) {
      hex = hex.substring(2);
    }

    BigInteger value = new BigInteger(hex, 16);
    return value.toString(10);
  }
}
