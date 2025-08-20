package com.springboot.staking.adaptor.proxy;

import com.springboot.staking.adaptor.proxy.dto.request.EthRpcRequest;
import com.springboot.staking.adaptor.proxy.dto.response.EthRpcResponse;
import com.springboot.staking.adaptor.proxy.dto.response.EthTxResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "eth", url = "${external.proxy.eth.url}")
public interface EthNodeProxy {

  @PostMapping(value = "", consumes = "application/json", produces = "application/json")
  EthRpcResponse<String> getBalance(@RequestBody EthRpcRequest request);

  @PostMapping(value = "", consumes = "application/json", produces = "application/json")
  EthRpcResponse<EthTxResponse> getTx(@RequestBody EthRpcRequest request);
}
