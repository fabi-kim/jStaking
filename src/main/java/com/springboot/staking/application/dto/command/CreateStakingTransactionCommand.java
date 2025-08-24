package com.springboot.staking.application.dto.command;

import com.springboot.staking.common.constant.Symbol;
import com.springboot.staking.domain.shared.vo.RequestId;
import com.springboot.staking.domain.staking.model.StakingTransaction;

public record CreateStakingTransactionCommand(
    RequestId requestId,
    Symbol symbol,
    StakingTransaction.TxType txType,
    String delegator,
    String validator,
    String amount
) {
  
  public static CreateStakingTransactionCommand of(
      RequestId requestId,
      Symbol symbol,
      StakingTransaction.TxType txType,
      String delegator,
      String validator,
      String amount) {
    
    return new CreateStakingTransactionCommand(
        requestId, symbol, txType, delegator, validator, amount);
  }
}