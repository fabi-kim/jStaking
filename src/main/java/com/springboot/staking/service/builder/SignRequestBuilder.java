package com.springboot.staking.service.builder;

import com.springboot.staking.common.constant.Symbol;
import com.springboot.staking.data.dto.request.SignRequest;
import com.springboot.staking.data.entity.StakingTx;

public interface SignRequestBuilder {
  
  Symbol getSymbol();
  
  SignRequest buildSignRequest(StakingTx tx);
}