package com.springboot.staking.shared.annotation;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.ValueConstants;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Parameter(
    name = "x-request-id",
    in = ParameterIn.HEADER,
    description = "request ID",
    required = true,
    schema = @Schema(format = "uuid", example = "123e4567-e89b-12d3-a456-426614174001")
)
public @interface RequestIdHeader {

  @AliasFor("name")
  String value() default "x-request-id";

  @AliasFor("value")
  String name() default "x-request-id";

  boolean required() default true;

  String defaultValue() default ValueConstants.DEFAULT_NONE;
}