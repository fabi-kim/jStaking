package com.springboot.staking.adaptor.proxy.dto.request;

public record EthRpcRequest(
    int id,
    String jsonrpc,
    String method,
    String[] params
) {

  public EthRpcRequest(String method, String[] params) {
    this(0, "2.0", method, params);
  }

  public static EthRpcRequest ofGetTransaction(String txHash) {
    return new EthRpcRequest(0, "2.0", "eth_getTransactionByHash", new String[]{txHash});
  }
}
