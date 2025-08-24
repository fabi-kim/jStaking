package com.springboot.staking.infrastructure.mapper;

import com.springboot.staking.application.dto.response.StakingTxResponse;
import com.springboot.staking.infrastructure.persistence.entity.StakingTx;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StakingTxMapper {

  StakingTxResponse toDto(StakingTx stakingTx);
}
