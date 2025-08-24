package com.springboot.staking.domain.staking.service;

import com.springboot.staking.application.dto.request.StakingRequest;
import com.springboot.staking.application.dto.response.DelegateTxResponse;
import com.springboot.staking.shared.constant.Symbol;

public interface StakingService {

  Symbol getSymbol();

  DelegateTxResponse createDelegateTx(StakingRequest request);
}