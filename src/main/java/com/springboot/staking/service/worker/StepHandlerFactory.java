package com.springboot.staking.service.worker;

import com.springboot.staking.common.constant.Step;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StepHandlerFactory {

  private final Map<Step, StepHandler> stepHandlers;

  public StepHandlerFactory(List<StepHandler> stepHandlers) {
    this.stepHandlers = stepHandlers.stream()
        .collect(Collectors.toMap(StepHandler::step, Function.identity()));

    log.info("Registered {} step handlers: {}",
        this.stepHandlers.size(),
        this.stepHandlers.keySet());

    validateHandlers();
  }

  private void validateHandlers() {
    for (Step step : Step.values()) {
      if (!stepHandlers.containsKey(step)) {
        throw new IllegalStateException("No handler found for step: " + step);
      }
    }
  }
}

