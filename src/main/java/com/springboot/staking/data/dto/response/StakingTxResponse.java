package com.springboot.staking.data.dto.response;

import com.springboot.staking.data.entity.StakingTx.TxType;
import java.util.UUID;

public record StakingTxResponse(
    Long id,
    UUID requestId,
    TxType txType,
    String delegator,
    String validator,
    String amount
) {

}
