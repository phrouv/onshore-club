package com.onshore.services;

import com.onshore.common_lib.exception.GlobalExceptionHandler;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@Import(GlobalExceptionHandler.class)
@OpenAPIDefinition(
    info =
        @Info(
            title = "Partner service API",
            version = "1.0",
            description = "Documentation Partner service API v1.0"))
public class PartnerServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(PartnerServiceApplication.class, args);
  }
}
