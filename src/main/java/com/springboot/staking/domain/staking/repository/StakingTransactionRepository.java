package com.springboot.staking.domain.staking.repository;

import com.springboot.staking.domain.shared.vo.RequestId;
import com.springboot.staking.domain.staking.model.StakingTransaction;
import com.springboot.staking.domain.staking.vo.StakingTransactionId;
import java.util.Optional;

public interface StakingTransactionRepository {
  
  StakingTransaction save(StakingTransaction transaction);
  
  Optional<StakingTransaction> findById(StakingTransactionId id);
  
  Optional<StakingTransaction> findByRequestId(RequestId requestId);
  
  Optional<StakingTransactionId> findAnyReadyTransactionId();
  
  boolean claimForProcessing(StakingTransactionId id, 
                           com.springboot.staking.common.constant.Step currentStep);
  
  void updateStep(StakingTransactionId id,
                 com.springboot.staking.common.constant.Step currentStep,
                 com.springboot.staking.common.constant.Step nextStep,
                 StakingTransaction.TransactionStatus nextStatus);
}