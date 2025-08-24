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
public class EthStakingService implements StakingService {

  @Override
  public Symbol getSymbol() {
    return Symbol.ETH;
  }

  @Override
  public DelegateTxResponse createDelegateTx(StakingRequest stakingRequest) {
    // TODO: ETH specific implementation
    log.info("Creating ETH delegate tx for request: {}", stakingRequest);
    throw new UnsupportedOperationException("ETH staking not yet implemented");
  }
}