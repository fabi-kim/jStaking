package com.springboot.staking.shared.exception;

import com.springboot.staking.shared.constant.ErrorCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(annotations = {RestController.class}, basePackages = {
    "com.springboot.staking.controller"})
public class GlobalExceptionHandler {

  @ExceptionHandler(ApplicationException.class)
  public ResponseEntity<ProblemDetail> handleBusiness(ApplicationException ex) {
    var pd = ProblemDetail.forStatus(ex.code().httpStatus());
    pd.setTitle(ex.code().name());
    pd.setDetail(ex.getMessage());
    ex.details().forEach(pd::setProperty);
    return ResponseEntity.status(ex.code().httpStatus()).body(pd);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ProblemDetail> handleEtc(Exception ex) {
    var pd = ProblemDetail.forStatus(500);
    pd.setTitle(ErrorCode.INTERNAL_ERROR.name());
    pd.setDetail(ErrorCode.INTERNAL_ERROR.defaultMessage());
    return ResponseEntity.status(500).body(pd);
  }
}