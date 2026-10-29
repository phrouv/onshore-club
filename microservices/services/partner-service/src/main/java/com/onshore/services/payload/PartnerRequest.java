package com.onshore.services.payload;

import jakarta.validation.constraints.NotBlank;

public record PartnerRequest(
    @NotBlank String name,
    @NotBlank String address,
    @NotBlank String city,
    String description,
    @NotBlank String category,
    Double latitude,
    Double longitude,
    String phone,
    String professionalEmail,
    String website,
    String instagram,
    String activityTypes,
    String desiredSeats,
    String managerFirstName,
    String managerLastName,
    String managerRole) {}
