package com.onshore.services;

import com.onshore.common_lib.exception.GlobalExceptionHandler;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@Import(GlobalExceptionHandler.class)
@EnableFeignClients
@OpenAPIDefinition(
    info =
        @Info(
            title = "Payout service API",
            version = "1.0",
            description = "Documentation Payout service API v1.0"))
public class PayoutServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(PayoutServiceApplication.class, args);
  }
}
