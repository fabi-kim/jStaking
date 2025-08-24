package com.springboot.staking.domain.staking.service;

import com.springboot.staking.domain.staking.model.StakingTransaction;
import com.springboot.staking.shared.constant.Step;

public interface StepManagerFactory {

  void processStep(StakingTransaction transaction, Step step);

  Step getNextStep(Step currentStep);
}