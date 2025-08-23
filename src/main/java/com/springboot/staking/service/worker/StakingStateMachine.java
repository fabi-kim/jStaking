package com.springboot.staking.service.worker;

import com.springboot.staking.common.constant.Step;
import com.springboot.staking.data.dao.StakingTxDao;
import com.springboot.staking.data.entity.StakingTx;
import com.springboot.staking.service.worker.StepHandler.StepResult;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StakingStateMachine {

  private final Map<Step, StepHandler> handlers;
  private final StakingTxDao stakingTxDao;

  public StakingStateMachine(List<StepHandler> stepHandlers,
      StakingTxDao stakingTxDao) {
    this.handlers = stepHandlers.stream()
        .collect(Collectors.toMap(StepHandler::step, Function.identity()));
    this.stakingTxDao = stakingTxDao;

    validateHandlers();
  }

  private void validateHandlers() {
    for (Step step : Step.values()) {
      if (!handlers.containsKey(step)) {
        throw new IllegalStateException("No handler found for step: " + step);
      }
    }
  }

  public void processTransaction(Long txId) {
    StakingTx tx = stakingTxDao.findById(txId);
    executeStep(tx);
  }

  private void executeStep(StakingTx tx) {
    Step currentStep = tx.getStep();
    StepHandler handler = handlers.get(currentStep);

    log.info("Executing step {} for tx {}", currentStep, tx.getId());

    // 1. READY → IN_PROGRESS로 변경하여 락 획득
    boolean claimed = stakingTxDao.claimForProcessing(tx.getId(), currentStep);
    if (!claimed) {
      log.info("Transaction {} already being processed by another worker", tx.getId());
      return;
    }

    try {
      // 2. 외부 호출 (트랜잭션 밖에서)
      StepResult result = handler.process(tx);

      // 3. 다음 스텝으로 전이 (IN_PROGRESS → 다음스텝/READY)
      stakingTxDao.completeStep(tx.getId(), currentStep, result.next(), result.status());

      log.info("Completed step {} for tx {}", currentStep, tx.getId());
    } catch (Exception e) {
      log.error("Step execution failed for tx {} at step {}", tx.getId(), currentStep, e);
      handleStepFailure(tx);
    }
  }

  private void handleStepFailure(StakingTx tx) {
    stakingTxDao.markAsFailed(tx.getId(), tx.getStep());
  }
}