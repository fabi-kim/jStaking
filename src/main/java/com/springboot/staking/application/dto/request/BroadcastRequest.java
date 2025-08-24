package com.springboot.staking.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record BroadcastRequest(
    @Schema(description = "서명 트랜잭션")
    String signedTx
) {

  public static BroadcastRequest of(String signedTx) {
    return new BroadcastRequest(signedTx);
  }
}