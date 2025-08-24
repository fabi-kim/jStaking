package com.springboot.staking.infrastructure.service;

import com.springboot.staking.application.dto.request.SignRequest;
import com.springboot.staking.domain.staking.model.StakingTransaction;
import com.springboot.staking.domain.staking.service.SignRequestBuilderDomainService;
import com.springboot.staking.domain.staking.service.SignRequestBuilderFactory;
import com.springboot.staking.shared.constant.Symbol;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignRequestBuilderDomainServiceImpl implements SignRequestBuilderDomainService {

  private final SignRequestBuilderFactory signRequestBuilderFactory;

  @Override
  public SignRequest buildSignRequest(StakingTransaction transaction, Symbol symbol) {
    var builder = signRequestBuilderFactory.getBuilder(symbol);
    return builder.buildSignRequest(transaction);
  }
}