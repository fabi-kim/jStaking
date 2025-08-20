package com.springboot.staking.data.dto.request;

import com.springboot.staking.adaptor.proxy.dto.request.SignerSignRequest.Options;
import io.swagger.v3.oas.annotations.media.Schema;

public record SignRequest(
    @Schema(description = "서명대상 미서명 트랜잭션")
    String unsignedTx,
    Options options
) {

}