package com.springboot.staking.infrastructure.service;

import com.springboot.staking.common.constant.Step;
import com.springboot.staking.domain.staking.model.StakingTransaction;
import com.springboot.staking.domain.staking.repository.StakingTransactionRepository;
import com.springboot.staking.domain.staking.service.StakingProcessorDomainService;
import com.springboot.staking.domain.staking.vo.StakingTransactionId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StakingProcessorDomainServiceImpl implements StakingProcessorDomainService {

  private final StakingTransactionRepository repository;

  @Override
  @Transactional
  public void processStep(StakingTransactionId transactionId, Step step) {
    StakingTransaction transaction = repository.findById(transactionId)
        .orElseThrow(() -> new IllegalArgumentException("Transaction not found: " + transactionId));
    
    if (!transaction.isReady()) {
      log.debug("Transaction {} is not ready for processing", transactionId);
      return;
    }
    
    transaction.markAsInProgress();
    repository.save(transaction);
  }

  @Override
  @Transactional
  public boolean claimTransaction(StakingTransactionId transactionId, Step step) {
    boolean claimed = repository.claimForProcessing(transactionId, step);
    
    if (claimed) {
      log.info("Claimed transaction {} for processing at step {}", transactionId, step);
    } else {
      log.debug("Failed to claim transaction {} - already processing or modified", transactionId);
    }
    
    return claimed;
  }

  @Override
  @Transactional
  public void completeStep(StakingTransaction transaction, Step nextStep) {
    if (!transaction.canTransitionTo(nextStep)) {
      throw new IllegalStateException(
          String.format("Invalid transition from %s to %s for transaction %s",
              transaction.getStep(), nextStep, transaction.getId()));
    }

    StakingTransaction.TransactionStatus nextStatus = 
        (nextStep == Step.CONFIRM) ? StakingTransaction.TransactionStatus.CONFIRMED 
                                   : StakingTransaction.TransactionStatus.READY;

    repository.updateStep(transaction.getId(), transaction.getStep(), nextStep, nextStatus);
    
    log.info("Completed step {} → {} for transaction {}", 
             transaction.getStep(), nextStep, transaction.getId());
  }

  @Override
  @Transactional
  public void markAsFailed(StakingTransaction transaction) {
    repository.updateStep(
        transaction.getId(), 
        transaction.getStep(), 
        transaction.getStep(), 
        StakingTransaction.TransactionStatus.FAILED
    );
    
    log.error("Marked transaction {} as failed at step {}", 
              transaction.getId(), transaction.getStep());
  }
}