package com.springboot.staking.application.dto.response;

import com.springboot.staking.infrastructure.adaptor.proxy.dto.response.SignerBroadcastTxResponse;
import io.swagger.v3.oas.annotations.media.Schema;

public record BroadcastResponse(
    @Schema(description = "트랜잭션 txHash")
    String txhash,
    @Schema(nullable = true)
    String response
) {

  public static BroadcastResponse of(String txhash) {
    return new BroadcastResponse(txhash,
        null);
  }

  public static BroadcastResponse from(SignerBroadcastTxResponse signerBroadcastTxResponse) {
    return new BroadcastResponse(signerBroadcastTxResponse.txhash(),
        signerBroadcastTxResponse.response());
  }
}