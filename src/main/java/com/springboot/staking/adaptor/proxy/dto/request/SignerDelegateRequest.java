package com.springboot.staking.adaptor.proxy.dto.request;

import com.springboot.staking.data.dto.request.StakingRequest;

public record SignerDelegateRequest(
    String delegateAddress,
    String validatorAddress,
    String amount
) {

  public static SignerDelegateRequest from(StakingRequest stakingRequest) {
    return new SignerDelegateRequest(stakingRequest.delegateAddress(),
        stakingRequest.validatorAddress(), stakingRequest.amount());
  }
}
