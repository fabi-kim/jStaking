package com.springboot.staking.data.parser;

import com.springboot.staking.adaptor.proxy.dto.response.EthRpcResponse;
import com.springboot.staking.adaptor.proxy.dto.response.EthTxResponse;
import com.springboot.staking.data.dto.response.TransactionResponse;
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
