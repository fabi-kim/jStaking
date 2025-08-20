package com.springboot.staking.service;

import com.springboot.staking.common.constant.Symbol;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NodeServiceFactory {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private final Map<String, NodeService> nodeServices;

  @Autowired
  public NodeServiceFactory(Map<String, NodeService> nodeServices) {
    this.nodeServices = nodeServices;
  }

  public NodeService getServiceBySymbol(Symbol symbol) {
    var nodeService = nodeServices.get(symbol.getSymbol());

    logger.info("[getServiceBySymbol] get node service by symbol:{}, service: {}", symbol,
        nodeService.getClass().getName());
    return nodeService;

  }
}

