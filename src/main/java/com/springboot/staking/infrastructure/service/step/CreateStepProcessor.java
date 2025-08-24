package com.springboot.staking.infrastructure.service.step;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.staking.application.dto.request.StakingRequest;
import com.springboot.staking.domain.staking.model.StakingTransaction;
import com.springboot.staking.domain.staking.service.StakingServiceFactory;
import com.springboot.staking.domain.staking.service.StepProcessor;
import com.springboot.staking.infrastructure.persistence.StakingTransactionRepositoryImpl;
import com.springboot.staking.shared.constant.Step;
import com.springboot.staking.shared.constant.Symbol;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateStepProcessor implements StepProcessor {

  private final StakingServiceFactory stakingServiceFactory;
  private final StakingTransactionRepositoryImpl repositoryImpl;
  private final ObjectMapper objectMapper;

  @Override
  public Step getStep() {
    return Step.CREATE;
  }

  @Override
  @Transactional
  public void process(StakingTransaction transaction) {
    var symbol = Symbol.valueOf(transaction.getProduct().getSymbol());
    var stakingRequest = StakingRequest.of(
        transaction.getDelegator().value(),
        transaction.getValidator().value(),
        transaction.getAmount().toString()
    );

    log.info("Creating delegate tx for requestId: {}", transaction.getRequestId());

    var stakingService = stakingServiceFactory.getServiceBySymbol(symbol);
    var response = stakingService.createDelegateTx(stakingRequest);

    // unsignedTx와 extraData 업데이트
    String extraDataJson = null;
    if (response.options() != null) {
      try {
        extraDataJson = objectMapper.writeValueAsString(response.options());
      } catch (Exception e) {
        log.warn("Failed to serialize options to JSON for requestId: {}",
            transaction.getRequestId(), e);
      }
    }

    // Repository를 통해 직접 업데이트
    repositoryImpl.updateUnsignedTxAndExtraData(transaction.getId(), response.unsignedTx(),
        extraDataJson);

    log.info("Created delegate tx with unsignedTx length: {}", response.unsignedTx().length());
  }
}