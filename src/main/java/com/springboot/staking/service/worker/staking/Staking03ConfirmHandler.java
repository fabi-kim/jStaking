package com.springboot.staking.service.worker.staking;

import com.springboot.staking.common.constant.Step;
import com.springboot.staking.common.constant.Symbol;
import com.springboot.staking.data.entity.StakingTx;
import com.springboot.staking.data.entity.StakingTx.Status;
import com.springboot.staking.service.NodeServiceFactory;
import com.springboot.staking.service.worker.StepHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
class Staking03ConfirmHandler implements StepHandler {

  private final NodeServiceFactory nodeServiceFactory;

  @Override
  public Step step() {
    return Step.CONFIRM;
  }

  @Override
  public StepResult process(StakingTx tx) {
    var symbol = Symbol.valueOf(tx.getProduct().getSymbol());
    var txHash = tx.getTxHash();

    log.info("Confirming tx with hash: {} for requestId: {}", txHash, tx.getRequestId());

    var nodeService = nodeServiceFactory.getServiceBySymbol(symbol);
    var response = nodeService.getTx(txHash);

    log.info("Confirmed tx status: {} for requestId: {}", response.txHash(), tx.getRequestId());

    return new StepResult(Step.CONFIRM, Status.CONFIRMED, "Transaction confirmed");
  }
}