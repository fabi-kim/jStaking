package com.springboot.staking.application.dto.response;

import com.springboot.staking.shared.constant.ProductType;
import io.swagger.v3.oas.annotations.media.Schema;

public record ProductResponse(
    @Schema(description = "상품Id", example = "1")
    Long id,
    @Schema(description = "상품타입", example = "STAKING")
    ProductType productType,
    @Schema(description = "상품 심볼", example = "ATOM")
    String symbol) {

}