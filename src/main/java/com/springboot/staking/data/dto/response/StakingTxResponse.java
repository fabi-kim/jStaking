package com.springboot.staking.data.dto.response;

import com.springboot.staking.common.constant.Step;
import com.springboot.staking.data.entity.StakingTx.TxType;
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
