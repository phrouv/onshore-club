package com.onshore.services;

import com.onshore.common_lib.exception.GlobalExceptionHandler;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@Import(GlobalExceptionHandler.class)
@EnableFeignClients
@EnableScheduling
@OpenAPIDefinition(
    info =
        @Info(
            title = "Booking service API",
            version = "1.0",
            description = "Documentation Booking service API v1.0"))
public class BookingServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(BookingServiceApplication.class, args);
  }
}
