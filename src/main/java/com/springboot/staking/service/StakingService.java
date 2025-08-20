package com.springboot.staking.service;

import com.springboot.staking.data.dto.request.StakingRequest;
import com.springboot.staking.data.dto.response.DelegateTxResponse;

public interface StakingService {

  DelegateTxResponse createDelegateTx(StakingRequest StakingRequest);


}

