package com.springboot.staking.infrastructure.service.node;

import com.springboot.staking.application.dto.request.BroadcastRequest;
import com.springboot.staking.application.dto.request.SignRequest;
import com.springboot.staking.application.dto.response.BroadcastResponse;
import com.springboot.staking.application.dto.response.TransactionResponse;
import com.springboot.staking.domain.node.service.NodeService;
import com.springboot.staking.infrastructure.adaptor.proxy.CelestiaNodeProxy;
import com.springboot.staking.infrastructure.adaptor.proxy.CelestiaSignerProxy;
import com.springboot.staking.infrastructure.adaptor.proxy.dto.request.SignerBroadcastRequest;
import com.springboot.staking.infrastructure.adaptor.proxy.dto.request.SignerSignRequest;
import com.springboot.staking.infrastructure.adaptor.proxy.dto.response.CosmosBalanceResponse;
import com.springboot.staking.infrastructure.adaptor.proxy.dto.response.CosmosTxResponse;
import com.springboot.staking.infrastructure.parser.TxParser;
import com.springboot.staking.shared.constant.RedisKey.NODE;
import com.springboot.staking.shared.constant.Symbol;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service(Symbol.TIA_BEAN)
public class TiaNodeService implements NodeService {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private final CelestiaNodeProxy celestiaProxy;
  private final CelestiaSignerProxy celestiaSignerProxy;
  private final TxParser<CosmosTxResponse> comsosTxParser;

  @Cacheable(cacheNames = NODE.BALANCE, key = "#address")
  public String getBalance(String address) {
    var proxyResponse = celestiaProxy.getBalance(address);
    return proxyResponse.balances()
        .stream()
        .filter(b -> b.denom().equals("utia"))
        .map(CosmosBalanceResponse.Balance::amount)
        .findFirst().orElse("0");
  }

  @Override
  public TransactionResponse getTx(String txHash) {
    var proxyResponse = celestiaProxy.getTx(txHash);

    return comsosTxParser.parse(proxyResponse);
  }


  public String sign(SignRequest signerRequest) {

    var singerSingRequest = SignerSignRequest.from(signerRequest);
    var response = celestiaSignerProxy.sign(singerSingRequest);

    logger.info("sign response: {}", response);

    return response;
  }

  public BroadcastResponse broadcast(BroadcastRequest broadcastRequest) {

    var signerBroadcastRequest = SignerBroadcastRequest.of(broadcastRequest);
    var response = celestiaSignerProxy.broadcast(signerBroadcastRequest);
    logger.info("broadcast response: {}", response);

    return BroadcastResponse.from(response);
  }

}
