package com.springboot.staking.controller;

import com.springboot.staking.data.dto.request.ProductRequest;
import com.springboot.staking.data.dto.response.ProductResponse;
import com.springboot.staking.service.ProductService;
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

  private final ProductService productService;

  @PostMapping("")
  public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest product) {

    return ResponseEntity.ok(
        productService.createProduct(product));
  }
}
