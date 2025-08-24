package com.springboot.staking.application.dto;

public record Options(
    String accountNumber,
    String sequence,
    String chainId
) {

}