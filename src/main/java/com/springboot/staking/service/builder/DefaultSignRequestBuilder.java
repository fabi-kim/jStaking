package com.springboot.staking.service.builder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.staking.common.constant.Symbol;
import com.springboot.staking.data.dto.request.SignRequest;
import com.springboot.staking.data.entity.StakingTx;
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
  public SignRequest buildSignRequest(StakingTx tx) {
    return SignRequest.of(tx.getUnsignedTx());
  }
}