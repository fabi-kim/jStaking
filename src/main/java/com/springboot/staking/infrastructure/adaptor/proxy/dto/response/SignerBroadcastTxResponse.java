package com.springboot.staking.infrastructure.adaptor.proxy.dto.response;

public record SignerBroadcastTxResponse(
    String txhash,
    String response
) {

}