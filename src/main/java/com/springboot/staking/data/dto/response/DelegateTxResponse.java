package com.springboot.staking.data.dto.response;

import com.springboot.staking.adaptor.proxy.dto.response.SignerDelegateTxResponse;
import com.springboot.staking.data.dto.Options;
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