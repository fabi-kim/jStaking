package com.springboot.staking.service.worker.staking;

import com.springboot.staking.common.constant.Symbol;
import com.springboot.staking.data.entity.StakingTx;
import com.springboot.staking.data.entity.StakingTx.Step;
import com.springboot.staking.service.flow.DelegateTxWorkflow;
import com.springboot.staking.service.worker.StepHandler;
import com.springboot.staking.service.worker.StepHandlerFactory;
import org.springframework.stereotype.Component;

@Component
class Staking02BroadcastHandler implements StepHandler {

  private final DelegateTxWorkflow delegateTxWorkflow;


  public Staking02BroadcastHandler(DelegateTxWorkflow delegateTxWorkflow,
      StepHandlerFactory stepHandlerFactory) {
    this.delegateTxWorkflow = delegateTxWorkflow;
    stepHandlerFactory.addHandler(step(), this);
  }


  @Override
  public Step step() {
    return Step.BROADCAST;
  }

  @Override
  public StepResult process(StakingTx tx) {
    var symbol = Symbol.valueOf(tx.getProduct().getSymbol());
    var requestId = tx.getRequestId();
    var signedTx = tx.getSignedTx();

    delegateTxWorkflow.broadcast(requestId, symbol, signedTx);
    return new StepResult(Step.CONFIRM);
  }
}