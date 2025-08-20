package com.springboot.staking.common.constant;

public enum ErrorCode {
  PRODUCT_DUPLICATE(409, "이미 존재하는 상품입니다."),
  PRODUCT_NOT_FOUND(404, "상품을 찾을 수 없습니다."),
  INVALID_REQUEST(400, "요청이 올바르지 않습니다."),
  INTERNAL_ERROR(500, "서버 오류가 발생했습니다.");

  private final int httpStatus;
  private final String defaultMessage;

  ErrorCode(int httpStatus, String defaultMessage) {
    this.httpStatus = httpStatus;
    this.defaultMessage = defaultMessage;
  }

  public int httpStatus() {
    return httpStatus;
  }

  public String defaultMessage() {
    return defaultMessage;
  }
}