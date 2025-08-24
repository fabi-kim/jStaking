package com.springboot.staking.application.service;

import com.springboot.staking.application.dto.command.CreateStakingTransactionCommand;
import com.springboot.staking.application.dto.command.ProcessStakingTransactionCommand;
import com.springboot.staking.common.constant.Step;
import com.springboot.staking.data.dto.response.StakingTxResponse;
import com.springboot.staking.domain.staking.model.StakingTransaction;
import com.springboot.staking.domain.staking.vo.StakingTransactionId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StakingWorkflowApplicationService {

  private final StakingApplicationService stakingApplicationService;

  @Transactional
  public StakingTxResponse createWorkerJob(CreateStakingTransactionCommand command) {
    log.info("Creating worker job for requestId: {}", command.requestId());
    
    StakingTransaction transaction = stakingApplicationService.createStakingTransaction(command);
    
    return StakingTxResponse.of(
        transaction.getRequestId().value(),
        transaction.getId() != null ? transaction.getId().value() : null,
        transaction.getStep(),
        transaction.getStatus().name()
    );
  }

  @Transactional
  public void processNextReadyTransaction() {
    StakingTransactionId readyTransactionId = 
        stakingApplicationService.findAnyReadyTransaction();
    
    if (readyTransactionId == null) {
      log.debug("No ready transactions found");
      return;
    }

    var command = ProcessStakingTransactionCommand.of(readyTransactionId, Step.CREATE);
    
    try {
      stakingApplicationService.processStakingTransaction(command);
      log.info("Successfully processed transaction: {}", readyTransactionId);
    } catch (Exception e) {
      log.error("Failed to process transaction: {}", readyTransactionId, e);
    }
  }

  @Transactional
  public void processTransaction(StakingTransactionId transactionId, Step step) {
    var command = ProcessStakingTransactionCommand.of(transactionId, step);
    stakingApplicationService.processStakingTransaction(command);
  }

  @Transactional(readOnly = true)
  public boolean hasReadyTransactions() {
    return stakingApplicationService.findAnyReadyTransaction() != null;
  }
}