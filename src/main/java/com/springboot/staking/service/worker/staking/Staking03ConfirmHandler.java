package com.springboot.staking.service.worker.staking;

import com.springboot.staking.common.constant.Symbol;
import com.springboot.staking.data.dto.response.BroadcastResponse;
import com.springboot.staking.data.entity.StakingTx;
import com.springboot.staking.data.entity.StakingTx.Step;
import com.springboot.staking.service.flow.DelegateTxWorkflow;
import com.springboot.staking.service.worker.StepHandler;
import com.springboot.staking.service.worker.StepHandlerFactory;
import org.springframework.stereotype.Component;

@Component
class Staking03ConfirmHandler implements StepHandler {

  private final DelegateTxWorkflow delegateTxWorkflow;

  public Staking03ConfirmHandler(DelegateTxWorkflow delegateTxWorkflow,
      StepHandlerFactory stepHandlerFactory) {
    this.delegateTxWorkflow = delegateTxWorkflow;
    stepHandlerFactory.addHandler(step(), this);
  }

  @Override
  public Step step() {
    return Step.CONFIRM;
  }

  @Override
  public StepResult process(StakingTx tx) {
    var symbol = Symbol.valueOf(tx.getProduct().getSymbol());
    var requestId = tx.getRequestId();
    var txHash = tx.getTxHash();

    BroadcastResponse broadcastResponse = BroadcastResponse.of(txHash);
    delegateTxWorkflow.confirmed(requestId, symbol, broadcastResponse);
    return new StepResult(Step.CONFIRM);
  }
}