package com.springboot.staking.adaptor.proxy;

import com.springboot.staking.adaptor.proxy.dto.response.CosmosBalanceResponse;
import com.springboot.staking.adaptor.proxy.dto.response.CosmosTxResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "tia", url = "${external.proxy.tia.url}")
public interface CelestiaNodeProxy {

  @GetMapping("/cosmos/bank/v1beta1/balances/{address}")
  CosmosBalanceResponse getBalance(@PathVariable String address);

  @GetMapping("/cosmos/tx/v1beta1/txs/{txHash}")
  CosmosTxResponse getTx(@PathVariable String txHash);
}
