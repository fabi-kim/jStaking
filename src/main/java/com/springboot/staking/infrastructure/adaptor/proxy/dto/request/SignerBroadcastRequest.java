package com.springboot.staking.infrastructure.adaptor.proxy.dto.request;

import com.springboot.staking.application.dto.request.BroadcastRequest;

public record SignerBroadcastRequest(
    String signedTx
) {

  public static SignerBroadcastRequest of(BroadcastRequest broadcastRequest) {
    return new SignerBroadcastRequest(broadcastRequest.signedTx());
  }
}

