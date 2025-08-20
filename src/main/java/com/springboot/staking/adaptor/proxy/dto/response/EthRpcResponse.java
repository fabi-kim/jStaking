package com.springboot.staking.adaptor.proxy.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record EthRpcResponse<T>(
    String jsonrpc,
    T result,
    int id
) {

}