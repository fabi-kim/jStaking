package com.springboot.staking.domain.staking.service;

import com.springboot.staking.common.constant.Step;
import com.springboot.staking.domain.staking.model.StakingTransaction;

public interface StepProcessorService {
  
  void processCreateStep(StakingTransaction transaction);
  
  void processSignStep(StakingTransaction transaction);
  
  void processBroadcastStep(StakingTransaction transaction);
  
  void processConfirmStep(StakingTransaction transaction);
  
  Step getNextStep(Step currentStep);
}