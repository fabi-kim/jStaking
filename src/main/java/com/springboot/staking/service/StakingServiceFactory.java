package com.springboot.staking.service;

import com.springboot.staking.common.constant.Symbol;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StakingServiceFactory {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private final Map<String, StakingService> stakingServices;

  @Autowired
  public StakingServiceFactory(Map<String, StakingService> stakingServices) {
    this.stakingServices = stakingServices;
  }

  public StakingService getServiceBySymbol(Symbol symbol) {
    stakingServices.forEach((k, v) -> System.out.println(k));
    var stakingService = stakingServices.get(symbol.getSymbol().toLowerCase() + "StakingService");

    logger.info("[getServiceBySymbol] get staking service by symbol:{}, service: {}", symbol,
        stakingService.getClass().getName());
    return stakingService;

  }
}

