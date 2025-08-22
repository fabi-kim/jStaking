package com.springboot.staking.service.worker;

import com.springboot.staking.data.entity.StakingTx.Step;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class StepHandlerFactory {

  private final Map<Step, StepHandler> stepHandlers;

  public StepHandlerFactory() {
    this.stepHandlers = new HashMap<>();
  }

  public void addHandler(Step step, StepHandler stepHandler) {
    stepHandlers.put(step, stepHandler);
  }

  public StepHandler getHandler(Step step) {
    return stepHandlers.get(step);
  }
}

