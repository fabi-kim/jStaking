package com.springboot.staking.data.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record TransactionResponse(
    @Schema(description = "트랜잭션 해시", example = "175983612E38EA5A6A68086BC8046E521F30253F86B600F641A7E8C005718DB9")
    String txHash,
    @Schema(description = "트랜잭션 타입", example = "staking")
    String type,
    @Schema(description = "송신자 주소", example = "celestia1vlcup62es8zym6twjpsck79r5ngpdzmky850d8")
    String sender) {

  public TransactionResponse() {
    this(null, null, null);
  }
}
