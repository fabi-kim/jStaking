package com.springboot.staking.application.dto.response;

import com.springboot.staking.infrastructure.persistence.entity.StakingTx.TxType;
import com.springboot.staking.shared.constant.Step;
import java.util.UUID;

public record StakingTxResponse(
    Long id,
    UUID requestId,
    TxType txType,
    String delegator,
    String validator,
    String amount,
    Step step,
    String status
) {

  public static StakingTxResponse of(UUID requestId, Long id, Step step, String status) {
    return new StakingTxResponse(id, requestId, null, null, null, null, step, status);
  }
}
