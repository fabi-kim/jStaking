package com.springboot.staking.infrastructure.adaptor.proxy;

import com.springboot.staking.infrastructure.adaptor.proxy.dto.request.SignerBroadcastRequest;
import com.springboot.staking.infrastructure.adaptor.proxy.dto.request.SignerDelegateRequest;
import com.springboot.staking.infrastructure.adaptor.proxy.dto.request.SignerSignRequest;
import com.springboot.staking.infrastructure.adaptor.proxy.dto.response.SignerBroadcastTxResponse;
import com.springboot.staking.infrastructure.adaptor.proxy.dto.response.SignerDelegateTxResponse;
import com.springboot.staking.infrastructure.adaptor.proxy.interceptor.SignerFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "signer",
    url = "${external.proxy.signer.url}",
    configuration = SignerFeignConfig.class
)
public interface CelestiaSignerProxy {

  @PostMapping("/v1/admin/tx/delegate-protoc")
  SignerDelegateTxResponse createDelegateTx(@RequestBody SignerDelegateRequest signerRequest);

  @PostMapping("/v1/admin/sign")
  String sign(@RequestBody SignerSignRequest signRequest);

  @PostMapping("/v1/admin/tx/broadcast")
  SignerBroadcastTxResponse broadcast(@RequestBody SignerBroadcastRequest broadcastRequest);
}
