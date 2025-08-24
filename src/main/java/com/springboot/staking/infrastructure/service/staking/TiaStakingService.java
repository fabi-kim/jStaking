package com.springboot.staking.infrastructure.service.staking;

import com.springboot.staking.application.dto.request.StakingRequest;
import com.springboot.staking.application.dto.response.DelegateTxResponse;
import com.springboot.staking.domain.staking.service.StakingService;
import com.springboot.staking.infrastructure.adaptor.proxy.CelestiaSignerProxy;
import com.springboot.staking.infrastructure.adaptor.proxy.dto.request.SignerDelegateRequest;
import com.springboot.staking.shared.constant.Symbol;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class TiaStakingService implements StakingService {


  private final CelestiaSignerProxy celestiaSignerProxy;

  @Override
  public Symbol getSymbol() {
    return Symbol.TIA;
  }

  @Override
  public DelegateTxResponse createDelegateTx(StakingRequest stakingRequest) {

    SignerDelegateRequest signerRequest = SignerDelegateRequest.from(stakingRequest);
    var response = celestiaSignerProxy.createDelegateTx(signerRequest);

    return DelegateTxResponse.from(response);
  }


}
