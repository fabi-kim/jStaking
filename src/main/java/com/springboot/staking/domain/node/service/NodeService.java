package com.springboot.staking.domain.node.service;

import com.springboot.staking.application.dto.request.BroadcastRequest;
import com.springboot.staking.application.dto.request.SignRequest;
import com.springboot.staking.application.dto.response.BroadcastResponse;
import com.springboot.staking.application.dto.response.TransactionResponse;

public interface NodeService {

  String getBalance(String address);

  TransactionResponse getTx(String txHash);

  String sign(SignRequest signerRequest);

  BroadcastResponse broadcast(BroadcastRequest broadcastRequest);
}