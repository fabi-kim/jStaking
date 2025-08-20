package com.springboot.staking.data.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record StakingRequest(
    @Schema(description = "위임 주소")
    String delegateAddress,
    @Schema(description = "벨리데이터 주소")
    String validatorAddress,
    @Schema(description = "요청금액", example = "1000")
    String amount
) {

}