package com.springboot.staking.infrastructure.service.step;

import com.springboot.staking.domain.node.service.NodeServiceFactory;
import com.springboot.staking.domain.staking.model.StakingTransaction;
import com.springboot.staking.domain.staking.service.StepProcessor;
import com.springboot.staking.shared.constant.Step;
import com.springboot.staking.shared.constant.Symbol;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConfirmStepProcessor implements StepProcessor {

  private final NodeServiceFactory nodeServiceFactory;

  @Override
  public Step getStep() {
    return Step.CONFIRM;
  }

  @Override
  @Transactional
  public void process(StakingTransaction transaction) {
    var symbol = Symbol.valueOf(transaction.getProduct().getSymbol());

    log.info("Confirming tx for requestId: {}", transaction.getRequestId());

    var nodeService = nodeServiceFactory.getServiceBySymbol(symbol);

    // 트랜잭션 상태 확인
    try {
      var txResponse = nodeService.getTx(transaction.getTxHash());
      if (txResponse != null) {
        log.info("Transaction confirmed for requestId: {}", transaction.getRequestId());
      } else {
        throw new RuntimeException("Transaction not found");
      }
    } catch (Exception e) {
      log.warn("Failed to confirm transaction for requestId: {}", transaction.getRequestId(), e);
      throw new RuntimeException("Transaction not confirmed yet", e);
    }
  }
}