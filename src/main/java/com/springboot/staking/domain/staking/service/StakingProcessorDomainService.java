package com.springboot.staking.domain.staking.service;

import com.springboot.staking.common.constant.Step;
import com.springboot.staking.domain.staking.model.StakingTransaction;
import com.springboot.staking.domain.staking.vo.StakingTransactionId;

public interface StakingProcessorDomainService {
  
  void processStep(StakingTransactionId transactionId, Step step);
  
  boolean claimTransaction(StakingTransactionId transactionId, Step step);
  
  void completeStep(StakingTransaction transaction, Step nextStep);
  
  void markAsFailed(StakingTransaction transaction);
}