package com.onshore.services.payload;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record ClubRequest(
    @NotNull UUID partnerId,
    @NotBlank String name,
    @NotBlank String street,
    @NotBlank String city,
    @NotBlank String zipCode,
    @DecimalMin("-90") @DecimalMax("90") BigDecimal latitude,
    @DecimalMin("-180") @DecimalMax("180") BigDecimal longitude,
    String description,
    String contactFirstName,
    String contactLastName,
    @Email String contactEmail,
    String contactPhoneNumber) {}
