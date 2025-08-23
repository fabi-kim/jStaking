package com.springboot.staking.service.flow;

import com.springboot.staking.common.constant.Symbol;
import com.springboot.staking.data.dto.request.BroadcastRequest;
import com.springboot.staking.data.dto.request.SignRequest;
import com.springboot.staking.data.dto.request.StakingRequest;
import com.springboot.staking.data.dto.response.BroadcastResponse;
import com.springboot.staking.data.dto.response.DelegateTxResponse;
import com.springboot.staking.data.dto.response.TransactionResponse;
import com.springboot.staking.service.NodeServiceFactory;
import com.springboot.staking.service.StakingServiceFactory;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class DelegateTxWorkflow extends
    TxWorkflowTemplate<StakingRequest, DelegateTxResponse, String, BroadcastResponse, TransactionResponse> {


  private final StakingServiceFactory stakingServices;
  private final NodeServiceFactory nodeServices;

  @Override
  public DelegateTxResponse create(UUID requestId, Symbol symbol, StakingRequest req) {
    var staking = stakingServices.getServiceBySymbol(symbol);
    log.info("-> Create Delegate Tx Workflow");

    var response = staking.createDelegateTx(req);
    log.info("* Create Delegate Tx Response");

    return response;
  }

  @Override
  public String sign(UUID requestId, Symbol symbol, DelegateTxResponse delegateTxResponse) {
    var nodeService = nodeServices.getServiceBySymbol(symbol);
    log.info("--> Sign Delegate Tx Workflow");

    var response = nodeService.sign(SignRequest.from(delegateTxResponse));
    log.info("* Sign Delegate Tx Response");

    return response;
  }

  @Override
  public BroadcastResponse broadcast(UUID requestId, Symbol symbol, String signedRawTx) {
    var nodeService = nodeServices.getServiceBySymbol(symbol);
    log.info("---> Broadcast Delegate Tx Workflow");

    var response = nodeService.broadcast(BroadcastRequest.of(signedRawTx));
    log.info("* Broadcast Delegate Tx Response");

    return response;
  }

  @Override
  public TransactionResponse confirmed(UUID requestId, Symbol symbol,
      BroadcastResponse broadcastResponse) {
    var nodeService = nodeServices.getServiceBySymbol(symbol);
    log.info("----> Confirm Delegate Tx Workflow");

    var response = nodeService.getTx(broadcastResponse.txhash());
    log.info("* Confirm Delegate Tx Response");

    return response;
  }
}