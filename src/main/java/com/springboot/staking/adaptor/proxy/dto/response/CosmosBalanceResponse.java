package com.springboot.staking.adaptor.proxy.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record CosmosBalanceResponse(
    List<Balance> balances,
    Pagination pagination
) {

  public record Balance(
      String denom,
      String amount
  ) {

  }

  public record Pagination(
      @JsonProperty("next_key")
      String nextKey,
      String total
  ) {

  }
}

