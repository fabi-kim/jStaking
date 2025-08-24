package com.springboot.staking.domain.staking.service;

import com.springboot.staking.domain.staking.model.StakingTransaction;
import com.springboot.staking.shared.constant.Step;

public interface StepProcessor {

  Step getStep();

  void process(StakingTransaction transaction);
}