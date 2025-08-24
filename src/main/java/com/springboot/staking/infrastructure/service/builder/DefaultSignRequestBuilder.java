package com.springboot.staking.infrastructure.service.builder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.staking.application.dto.request.SignRequest;
import com.springboot.staking.domain.staking.model.StakingTransaction;
import com.springboot.staking.domain.staking.service.SignRequestBuilder;
import com.springboot.staking.shared.constant.Symbol;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultSignRequestBuilder implements SignRequestBuilder {

  private final ObjectMapper objectMapper;

  @Override
  public Symbol getSymbol() {
    return null; // Factory에서 default로 사용됨을 의미
  }

  @Override
  public SignRequest buildSignRequest(StakingTransaction transaction) {
    return SignRequest.of(transaction.getUnsignedTx().value());
  }
}