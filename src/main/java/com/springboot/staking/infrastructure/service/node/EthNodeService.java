package com.springboot.staking.infrastructure.service.node;

import com.springboot.staking.application.dto.request.BroadcastRequest;
import com.springboot.staking.application.dto.request.SignRequest;
import com.springboot.staking.application.dto.response.BroadcastResponse;
import com.springboot.staking.application.dto.response.TransactionResponse;
import com.springboot.staking.domain.node.service.NodeService;
import com.springboot.staking.infrastructure.adaptor.proxy.EthNodeProxy;
import com.springboot.staking.infrastructure.adaptor.proxy.dto.request.EthRpcRequest;
import com.springboot.staking.infrastructure.adaptor.proxy.dto.response.EthRpcResponse;
import com.springboot.staking.infrastructure.adaptor.proxy.dto.response.EthTxResponse;
import com.springboot.staking.infrastructure.parser.TxParser;
import com.springboot.staking.shared.constant.RedisKey.NODE;
import com.springboot.staking.shared.constant.Symbol;
import com.springboot.staking.shared.util.NumberUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service(Symbol.ETH_BEAN)
public class EthNodeService implements NodeService {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private final EthNodeProxy ethNodeProxy;
  private final TxParser<EthRpcResponse<EthTxResponse>> ethTxParser;

  @Cacheable(cacheNames = NODE.BALANCE, key = "#address")
  public String getBalance(String address) {
    EthRpcResponse<String> proxyResponse = ethNodeProxy.getBalance(
        new EthRpcRequest("eth_getBalance", new String[]{address, "latest"}));
    String result = proxyResponse.result();
    return NumberUtil.HexToDecimalString(result);
  }

  @Override
  public TransactionResponse getTx(String txHash) {
    EthRpcResponse<EthTxResponse> proxyResponse = ethNodeProxy.getTx(
        EthRpcRequest.ofGetTransaction(txHash));
    logger.info("[getTx] proxyResponse: {}", proxyResponse);

    return ethTxParser.parse(proxyResponse);
  }

  @Override
  public String sign(SignRequest signerRequest) {
    return "";
  }

  @Override
  public BroadcastResponse broadcast(BroadcastRequest broadcastRequest) {
    return null;
  }
}
