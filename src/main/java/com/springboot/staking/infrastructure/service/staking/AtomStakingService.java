package com.springboot.staking.infrastructure.service.staking;

import com.springboot.staking.application.dto.request.StakingRequest;
import com.springboot.staking.application.dto.response.DelegateTxResponse;
import com.springboot.staking.domain.staking.service.StakingService;
import com.springboot.staking.shared.constant.Symbol;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AtomStakingService implements StakingService {

  @Override
  public Symbol getSymbol() {
    return Symbol.ATOM;
  }

  @Override
  public DelegateTxResponse createDelegateTx(StakingRequest stakingRequest) {
    // TODO: ATOM specific implementation  
    log.info("Creating ATOM delegate tx for request: {}", stakingRequest);
    throw new UnsupportedOperationException("ATOM staking not yet implemented");
  }
}