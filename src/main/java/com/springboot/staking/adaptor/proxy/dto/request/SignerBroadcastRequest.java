package com.springboot.staking.adaptor.proxy.dto.request;

import com.springboot.staking.data.dto.request.BroadcastRequest;

public record SignerBroadcastRequest(
    String signedTx
) {

  public static SignerBroadcastRequest of(BroadcastRequest broadcastRequest) {
    return new SignerBroadcastRequest(broadcastRequest.signedTx());
  }
}

