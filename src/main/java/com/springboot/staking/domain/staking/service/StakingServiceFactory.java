package com.springboot.staking.domain.staking.service;

import com.springboot.staking.shared.constant.Symbol;

public interface StakingServiceFactory {

  StakingService getServiceBySymbol(Symbol symbol);
}