package com.onshore.services.payload;

import jakarta.validation.constraints.NotBlank;

public record PartnerRequest(
    @NotBlank String name,
    @NotBlank String street,
    @NotBlank String city,
    @NotBlank String zipCode,
    String description,
    String website,
    String instagram,
    String managerFirstName,
    String managerLastName,
    String managerEmail,
    String managerPhoneNumber) {}
