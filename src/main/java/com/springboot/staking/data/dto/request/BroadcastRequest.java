package com.springboot.staking.data.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record BroadcastRequest(
    @Schema(description = "서명 트랜잭션")
    String signedTx
) {

}