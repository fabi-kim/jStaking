package com.springboot.staking.presentation.controller;

import com.springboot.staking.application.dto.request.ProductRequest;
import com.springboot.staking.application.dto.response.ProductResponse;
import com.springboot.staking.application.service.ProductApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
@RequestMapping("product")
public class ProductController {

  private final ProductApplicationService productApplicationService;

  @PostMapping("")
  public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest product) {

    return ResponseEntity.ok(
        productApplicationService.createProduct(product));
  }
}
