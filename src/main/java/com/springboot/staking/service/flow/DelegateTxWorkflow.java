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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class DelegateTxWorkflow extends
    TxWorkflowTemplate<StakingRequest, DelegateTxResponse, String, BroadcastResponse, TransactionResponse> {


  private final StakingServiceFactory StakingServices;
  private final NodeServiceFactory NodeServices;


  @Override
  protected DelegateTxResponse create(Symbol symbol, StakingRequest req) {
    log.info("-> Create Delegate Tx Workflow");
    var response = StakingServices.getServiceBySymbol(symbol).createDelegateTx(req);
    log.info("* Create Delegate Tx Response");
    return response;
  }

  @Override
  protected String sign(Symbol symbol, DelegateTxResponse delegateTxResponse) {
    log.info("--> Sign Delegate Tx Workflow");
    var response = NodeServices.getServiceBySymbol(symbol)
        .sign(SignRequest.from(delegateTxResponse));
    log.info("* Sign Delegate Tx Response");
    return response;
  }

  @Override
  protected BroadcastResponse broadcast(Symbol symbol, String signedRawTx) {
    log.info("---> Broadcast Delegate Tx Workflow");
    var response = NodeServices.getServiceBySymbol(symbol)
        .broadcast(BroadcastRequest.of(signedRawTx));
    log.info("* Broadcast Delegate Tx Response");
    return response;
  }

  @Override
  protected TransactionResponse confirmed(Symbol symbol, BroadcastResponse broadcastResponse) {
    log.info("----> Confirm Delegate Tx Workflow");
    var response = NodeServices.getServiceBySymbol(symbol).getTx(broadcastResponse.txhash());
    log.info("* Confirm Delegate Tx Response");
    return response;
  }
}