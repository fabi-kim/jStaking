package com.springboot.staking.infrastructure.service;

import com.springboot.staking.domain.node.service.NodeService;
import com.springboot.staking.domain.node.service.NodeServiceFactory;
import com.springboot.staking.shared.constant.Symbol;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NodeServiceFactoryImpl implements NodeServiceFactory {

  private final Map<String, NodeService> nodeServices;

  @Autowired
  public NodeServiceFactoryImpl(Map<String, NodeService> nodeServices) {
    this.nodeServices = nodeServices;
  }

  @Override
  public NodeService getServiceBySymbol(Symbol symbol) {
    var nodeService = nodeServices.get(symbol.getSymbol());

    log.info("[getServiceBySymbol] get node service by symbol:{}, service: {}", symbol,
        nodeService.getClass().getName());
    return nodeService;

  }
}

