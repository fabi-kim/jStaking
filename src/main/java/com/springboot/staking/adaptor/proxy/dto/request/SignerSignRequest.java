package com.springboot.staking.adaptor.proxy.dto.request;

import com.springboot.staking.data.dto.request.SignRequest;

public record SignerSignRequest(
    String payload,
    int productType,
    Options options
) {

  public static SignerSignRequest from(SignRequest signerRequest) {
    var options = signerRequest.options();
    return new SignerSignRequest(signerRequest.unsignedTx(), 0,
        new Options(options.accountNumber, options.sequence, "mocha-4"));
  }

  public static SignerSignRequest ofTia(String payload, String accountNumber, String sequence) {
    return new SignerSignRequest(payload, 0, new Options(accountNumber, sequence, "mocha-4"));
  }

  public record Options(
      String accountNumber,
      String sequence,
      String chainId
  ) {

  }
}

