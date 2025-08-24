package com.springboot.staking.infrastructure.service;

import com.springboot.staking.domain.staking.service.StakingService;
import com.springboot.staking.domain.staking.service.StakingServiceFactory;
import com.springboot.staking.shared.constant.Symbol;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class StakingServiceFactoryImpl implements StakingServiceFactory {


  private final Map<String, StakingService> stakingServices;

  @Autowired
  public StakingServiceFactoryImpl(Map<String, StakingService> stakingServices) {
    this.stakingServices = stakingServices;
  }

  public StakingService getServiceBySymbol(Symbol symbol) {
    log.debug("Available services: {}", stakingServices.keySet());
    var stakingService = stakingServices.get(symbol.getSymbol().toLowerCase() + "StakingService");

    log.info("[getServiceBySymbol] get staking service by symbol:{}, service: {}", symbol,
        stakingService.getClass().getName());
    return stakingService;

  }
}

