package com.springboot.staking.service.worker;

import com.springboot.staking.common.constant.Step;
import com.springboot.staking.data.entity.StakingTx;
import com.springboot.staking.data.entity.StakingTx.Status;

public interface StepHandler {

  Step step();

  StepResult process(StakingTx tx) throws Exception;

  default StepResult success() {
    Step nextStep = step().getNext();
    if (nextStep == null) {
      throw new IllegalStateException("Terminal step has no next step");
    }
    return new StepResult(nextStep, Status.READY, null);
  }

  default StepResult failure(String reason) {
    return new StepResult(step(), Status.FAILED, reason);
  }

  record StepResult(Step next, Status status, String message) {

    public StepResult(Step next) {
      this(next, Status.READY, null);
    }
  }
}