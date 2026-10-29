package com.onshore.services;

import com.onshore.common_lib.exception.GlobalExceptionHandler;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@Import(GlobalExceptionHandler.class)
@EnableScheduling
@OpenAPIDefinition(
    info =
        @Info(
            title = "Credit service API",
            version = "1.0",
            description = "Documentation Credit service API v1.0"))
public class CreditServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(CreditServiceApplication.class, args);
  }
}
