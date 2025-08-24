package com.springboot.staking.application.dto.response;

import com.springboot.staking.application.dto.Options;
import com.springboot.staking.infrastructure.adaptor.proxy.dto.response.SignerDelegateTxResponse;
import io.swagger.v3.oas.annotations.media.Schema;

public record DelegateTxResponse(
    @Schema(description = "미서명 트랜잭션")
    String unsignedTx,
    @Schema(nullable = true)
    Options options
) {


  public static DelegateTxResponse from(SignerDelegateTxResponse v) {
    return new DelegateTxResponse(v.unsignedTx(), v.options());
  }
}