package com.springboot.staking.domain.staking.service;

import com.springboot.staking.common.constant.Symbol;
import com.springboot.staking.data.dto.request.SignRequest;
import com.springboot.staking.domain.staking.model.StakingTransaction;

public interface SignRequestBuilderDomainService {
  
  SignRequest buildSignRequest(StakingTransaction transaction, Symbol symbol);
}