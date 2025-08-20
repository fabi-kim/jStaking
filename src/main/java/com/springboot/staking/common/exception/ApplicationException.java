package com.springboot.staking.common.exception;

import com.springboot.staking.common.constant.ErrorCode;
import java.util.Map;

public class ApplicationException extends RuntimeException {

  private final ErrorCode code;
  private final Map<String, Object> details;

  public ApplicationException(ErrorCode code) {
    super(code.defaultMessage());
    this.code = code;
    this.details = Map.of();
  }

  public ApplicationException(ErrorCode code, String message) {
    super(message);
    this.code = code;
    this.details = Map.of();
  }

  public ApplicationException(ErrorCode code, Map<String, Object> details) {
    super(code.defaultMessage());
    this.code = code;
    this.details = details == null ? Map.of() : Map.copyOf(details);
  }

  public ErrorCode code() {
    return code;
  }

  public Map<String, Object> details() {
    return details;
  }
}