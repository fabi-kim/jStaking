package com.springboot.staking.infrastructure.service;

import com.springboot.staking.domain.staking.model.StakingTransaction;
import com.springboot.staking.domain.staking.service.StepManagerFactory;
import com.springboot.staking.domain.staking.service.StepProcessor;
import com.springboot.staking.shared.constant.Step;
import jakarta.annotation.PostConstruct;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StepManagerFactoryImpl implements StepManagerFactory {

  private final List<StepProcessor> stepProcessors;
  private final Map<Step, StepProcessor> stepProcessorMap = new EnumMap<>(Step.class);
  private final Map<Step, Step> nextStepMap = new EnumMap<>(Step.class);

  @PostConstruct
  private void initializeStepMaps() {
    // Initialize step processor map
    stepProcessors.forEach(processor ->
        stepProcessorMap.put(processor.getStep(), processor));

    // Initialize next step map
    nextStepMap.put(Step.CREATE, Step.SIGN);
    nextStepMap.put(Step.SIGN, Step.BROADCAST);
    nextStepMap.put(Step.BROADCAST, Step.CONFIRM);
    nextStepMap.put(Step.CONFIRM, Step.CONFIRM); // 마지막 단계

    log.info("Initialized StepManagerFactory with {} processors", stepProcessorMap.size());
  }

  @Override
  public void processStep(StakingTransaction transaction, Step step) {
    StepProcessor processor = stepProcessorMap.get(step);
    if (processor == null) {
      throw new IllegalArgumentException("No processor found for step: " + step);
    }

    log.info("Processing step {} for transaction {}", step, transaction.getId());
    processor.process(transaction);
  }

  @Override
  public Step getNextStep(Step currentStep) {
    Step nextStep = nextStepMap.get(currentStep);
    if (nextStep == null) {
      throw new IllegalArgumentException("No next step defined for: " + currentStep);
    }
    return nextStep;
  }
}