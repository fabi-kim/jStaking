package com.springboot.staking.domain.staking.service;

import com.springboot.staking.application.dto.request.SignRequest;
import com.springboot.staking.domain.staking.model.StakingTransaction;
import com.springboot.staking.shared.constant.Symbol;

public interface SignRequestBuilderDomainService {

  SignRequest buildSignRequest(StakingTransaction transaction, Symbol symbol);
}