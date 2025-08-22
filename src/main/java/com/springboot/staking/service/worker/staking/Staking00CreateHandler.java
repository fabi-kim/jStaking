package com.springboot.staking.service.worker.staking;

import com.springboot.staking.common.constant.Symbol;
import com.springboot.staking.data.dto.request.StakingRequest;
import com.springboot.staking.data.entity.StakingTx;
import com.springboot.staking.data.entity.StakingTx.Step;
import com.springboot.staking.service.flow.DelegateTxWorkflow;
import com.springboot.staking.service.worker.StepHandler;
import com.springboot.staking.service.worker.StepHandlerFactory;
import org.springframework.stereotype.Component;

@Component
class Staking00CreateHandler implements StepHandler {

  private final DelegateTxWorkflow delegateTxWorkflow;

  public Staking00CreateHandler(DelegateTxWorkflow delegateTxWorkflow,
      StepHandlerFactory stepHandlerFactory) {
    this.delegateTxWorkflow = delegateTxWorkflow;
    stepHandlerFactory.addHandler(step(), this);
  }

  @Override
  public Step step() {
    return Step.CREATE;
  }

  @Override
  public StepResult process(StakingTx tx) {
    var symbol = Symbol.valueOf(tx.getProduct().getSymbol());
    var requestId = tx.getRequestId();
    var req = StakingRequest.of(tx.getDelegator(), tx.getValidator(), tx.getAmount());

    delegateTxWorkflow.create(requestId, symbol, req);

    return new StepResult(StakingTx.Step.SIGN);
  }
}