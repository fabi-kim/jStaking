package com.springboot.staking.service;

import com.springboot.staking.data.dto.request.BroadcastRequest;
import com.springboot.staking.data.dto.request.SignRequest;
import com.springboot.staking.data.dto.response.BroadcastResponse;
import com.springboot.staking.data.dto.response.TransactionResponse;

public interface NodeService {

  String getBalance(String address);

  TransactionResponse getTx(String txHash);

  String sign(SignRequest signerRequest);

  BroadcastResponse broadcast(BroadcastRequest broadcastRequest);
}

