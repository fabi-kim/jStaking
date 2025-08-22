package com.springboot.staking.service.worker.staking;

import com.springboot.staking.common.constant.Symbol;
import com.springboot.staking.data.dto.response.DelegateTxResponse;
import com.springboot.staking.data.entity.StakingTx;
import com.springboot.staking.data.entity.StakingTx.Step;
import com.springboot.staking.service.flow.DelegateTxWorkflow;
import com.springboot.staking.service.worker.StepHandler;
import com.springboot.staking.service.worker.StepHandlerFactory;
import org.springframework.stereotype.Component;

@Component
class Staking01SignHandler implements StepHandler {

  private final DelegateTxWorkflow delegateTxWorkflow;

  public Staking01SignHandler(DelegateTxWorkflow delegateTxWorkflow,
      StepHandlerFactory stepHandlerFactory) {
    this.delegateTxWorkflow = delegateTxWorkflow;
    stepHandlerFactory.addHandler(step(), this);
  }

  @Override
  public Step step() {
    return Step.SIGN;
  }

  @Override
  public StepResult process(StakingTx tx) {
    var symbol = Symbol.valueOf(tx.getProduct().getSymbol());
    var requestId = tx.getRequestId();

    DelegateTxResponse delegateTxResponse = DelegateTxResponse.of(tx.getUnsignedTx(), "120019",
        "12");

    delegateTxWorkflow.sign(requestId, symbol, delegateTxResponse);
    return new StepResult(StakingTx.Step.BROADCAST);
  }
}