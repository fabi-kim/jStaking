package com.springboot.staking.application.dto.command;

import com.springboot.staking.common.constant.Step;
import com.springboot.staking.domain.staking.vo.StakingTransactionId;

public record ProcessStakingTransactionCommand(
    StakingTransactionId transactionId,
    Step step
) {
  
  public static ProcessStakingTransactionCommand of(
      StakingTransactionId transactionId,
      Step step) {
    
    return new ProcessStakingTransactionCommand(transactionId, step);
  }
}