package com.springboot.staking.service.staking;

import com.springboot.staking.adaptor.proxy.CelestiaSignerProxy;
import com.springboot.staking.adaptor.proxy.dto.request.SignerDelegateRequest;
import com.springboot.staking.data.dto.request.StakingRequest;
import com.springboot.staking.data.dto.response.DelegateTxResponse;
import com.springboot.staking.service.StakingService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TiaStakingService implements StakingService {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private final CelestiaSignerProxy celestiaSignerProxy;

  @Override
  public DelegateTxResponse createDelegateTx(StakingRequest stakingRequest) {

    SignerDelegateRequest signerRequest = SignerDelegateRequest.from(stakingRequest);
    var response = celestiaSignerProxy.createDelegateTx(signerRequest);

    return DelegateTxResponse.from(response);
  }


}
