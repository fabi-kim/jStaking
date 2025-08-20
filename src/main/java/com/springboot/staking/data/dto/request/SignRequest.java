package com.springboot.staking.data.dto.request;

import com.springboot.staking.adaptor.proxy.dto.request.SignerSignRequest.Options;
import com.springboot.staking.data.dto.response.DelegateTxResponse;
import io.swagger.v3.oas.annotations.media.Schema;

public record SignRequest(
    @Schema(description = "서명대상 미서명 트랜잭션")
    String unsignedTx,
    Options options
) {

  public static SignRequest from(DelegateTxResponse delegateTxResponse) {
    return new SignRequest(delegateTxResponse.unsignedTx(),
        new Options(delegateTxResponse.accountNumber(), delegateTxResponse.sequence(), null));
  }
}