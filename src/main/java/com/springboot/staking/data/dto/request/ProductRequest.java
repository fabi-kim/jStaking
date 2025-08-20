package com.springboot.staking.data.dto.request;

import com.springboot.staking.common.constant.ProductType;
import io.swagger.v3.oas.annotations.media.Schema;

public record ProductRequest(
    @Schema(description = "상품타입", example = "STAKING")
    ProductType productType,
    @Schema(description = "상품 심볼", example = "ATOM")
    String symbol) {

}