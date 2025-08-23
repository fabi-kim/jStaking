package com.springboot.staking.adaptor.proxy.dto.response;

import com.springboot.staking.data.dto.Options;

public record SignerDelegateTxResponse(
    String unsignedTx,
    String accountNumber,
    Options options
) {
}