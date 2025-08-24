package com.springboot.staking.infrastructure.adaptor.proxy.dto.response;

import com.springboot.staking.application.dto.Options;

public record SignerDelegateTxResponse(
    String unsignedTx,
    String accountNumber,
    Options options
) {

}