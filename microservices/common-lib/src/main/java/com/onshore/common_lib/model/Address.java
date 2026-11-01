package com.onshore.common_lib.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class Address {

  @NotNull(message = "street is required")
  @Size(max = 500, message = "street must not exceed 500 characters")
  @Column(name = "street", length = 500, nullable = false)
  private String street;

  @NotNull(message = "city is required")
  @Size(max = 100, message = "city must not exceed 100 characters")
  @Column(name = "city", length = 100, nullable = false)
  private String city;

  @NotNull(message = "zipCode is required")
  @Size(max = 20, message = "zipCode must not exceed 20 characters")
  @Column(name = "zip_code", length = 20, nullable = false)
  private String zipCode;
}
