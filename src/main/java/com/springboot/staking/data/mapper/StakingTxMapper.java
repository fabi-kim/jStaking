package com.springboot.staking.data.mapper;

import com.springboot.staking.data.dto.response.StakingTxResponse;
import com.springboot.staking.data.entity.StakingTx;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StakingTxMapper {

  StakingTxResponse toDto(StakingTx stakingTx);
}
