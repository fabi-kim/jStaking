package com.springboot.staking.infrastructure.parser;

import com.springboot.staking.application.dto.response.TransactionResponse;
import com.springboot.staking.infrastructure.adaptor.proxy.dto.response.EthRpcResponse;
import com.springboot.staking.infrastructure.adaptor.proxy.dto.response.EthTxResponse;
import org.springframework.stereotype.Component;

@Component
public class EthTxParser implements TxParser<EthRpcResponse<EthTxResponse>> {

  @Override
  public TransactionResponse parse(EthRpcResponse<EthTxResponse> txResponse) {
    if (txResponse.result() == null) {
      return new TransactionResponse();
    }
    var txHash = txResponse.result().hash();
    var type = txResponse.result().input();
    var sender = txResponse.result().from();
    return new TransactionResponse(txHash, type, sender);
  }
}
