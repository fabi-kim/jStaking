package com.springboot.staking.adaptor.proxy.dto.request;

import com.springboot.staking.data.dto.Options;
import com.springboot.staking.data.dto.request.SignRequest;

public record SignerSignRequest(
    String payload,
    int productType,
    Options options
) {

  public static SignerSignRequest from(SignRequest signerRequest) {
    return new SignerSignRequest(signerRequest.unsignedTx(), 0,
        signerRequest.options());
  }

  public static SignerSignRequest ofTia(String payload, String accountNumber, String sequence) {
    return new SignerSignRequest(payload, 0, new Options(accountNumber, sequence, "mocha-4"));
  }
}

