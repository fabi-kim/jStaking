package com.springboot.staking.domain.node.service;

import com.springboot.staking.shared.constant.Symbol;

public interface NodeServiceFactory {

  NodeService getServiceBySymbol(Symbol symbol);
}