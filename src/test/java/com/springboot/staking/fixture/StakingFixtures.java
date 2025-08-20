package com.springboot.staking.fixture;

import com.springboot.staking.data.dto.request.StakingRequest;
import com.springboot.staking.data.dto.response.BroadcastResponse;
import com.springboot.staking.data.dto.response.DelegateTxResponse;
import com.springboot.staking.data.dto.response.TransactionResponse;

public final class StakingFixtures {

  private StakingFixtures() {
  }

  public static StakingRequest stakingRequest() {
    return new StakingRequest("alice", "val1", "100");
  }

  public static DelegateTxResponse delegateTxResponse() {
    return new DelegateTxResponse("RAW_TX", "", "");
  }

  public static DelegateTxResponse delegateTxResponseWithUnsignedTx(String unsignedTx) {
    return new DelegateTxResponse("RAW_TX", "", "");
  }

  public static BroadcastResponse broadcastResponse() {
    return new BroadcastResponse("0xABC", "");
  }

  public static TransactionResponse transactionResponse() {
    return new TransactionResponse("0xABC", "CONFIRMED", "");
  }


}