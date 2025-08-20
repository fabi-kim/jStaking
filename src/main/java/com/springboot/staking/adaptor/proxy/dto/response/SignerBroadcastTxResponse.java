package com.springboot.staking.adaptor.proxy.dto.response;

public record SignerBroadcastTxResponse(
    String txhash,
    String response
) {

}