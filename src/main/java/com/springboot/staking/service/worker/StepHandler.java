package com.springboot.staking.service.worker;

import com.springboot.staking.data.entity.StakingTx;

public interface StepHandler {

  StakingTx.Step step();

  StepResult process(StakingTx tx) throws Exception; // 외부 I/O 수행, 산출물 리턴

  record StepResult(StakingTx.Step next) {

  }
}