package com.springboot.staking.data.dto.request;

import com.springboot.staking.data.dto.Options;
import com.springboot.staking.data.dto.response.DelegateTxResponse;
import io.swagger.v3.oas.annotations.media.Schema;

public record SignRequest(
    @Schema(description = "서명대상 미서명 트랜잭션")
    String unsignedTx,
    Options options
) {

  public static SignRequest from(DelegateTxResponse delegateTxResponse) {
    return new SignRequest(delegateTxResponse.unsignedTx(),
        delegateTxResponse.options());
  }

  public static SignRequest of(String unsignedTx) {
    return new SignRequest(unsignedTx, null);
  }

  public static SignRequest forCosmos(String unsignedTx, String accountNumber, String sequence, String chainId) {
    return new SignRequest(unsignedTx, new Options(accountNumber, sequence, chainId));
  }
}