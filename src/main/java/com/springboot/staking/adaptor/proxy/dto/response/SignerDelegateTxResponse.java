package com.springboot.staking.adaptor.proxy.dto.response;

public record SignerDelegateTxResponse(
    String unsignedTx,
    String accountNumber,
    String sequence
) {

}