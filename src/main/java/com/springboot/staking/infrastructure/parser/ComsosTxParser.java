package com.springboot.staking.infrastructure.parser;

import com.springboot.staking.application.dto.response.TransactionResponse;
import com.springboot.staking.infrastructure.adaptor.proxy.dto.response.CosmosTxResponse;
import com.springboot.staking.infrastructure.adaptor.proxy.dto.response.CosmosTxResponse.TxResponse.Event.Attribute;
import org.springframework.stereotype.Component;

@Component
public class ComsosTxParser implements TxParser<CosmosTxResponse> {

  @Override
  public TransactionResponse parse(CosmosTxResponse txResponse) {
    var txHash = txResponse.tx_response().txhash();
    var type = txResponse.tx().body().messages().get(0).type();
    var sender = txResponse.tx_response().events().stream()
        .filter(e -> e.type().equalsIgnoreCase("coin_spent"))
        .flatMap(e -> e.attributes().stream())
        .filter(a -> a.key().equalsIgnoreCase("spender"))
        .map(Attribute::value)
        .findFirst()
        .orElse(null);
    return new TransactionResponse(txHash, type, sender);
  }
}
