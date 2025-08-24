package com.springboot.staking.application.dto.command;

import com.springboot.staking.domain.staking.vo.StakingTransactionId;
import com.springboot.staking.shared.constant.Step;

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