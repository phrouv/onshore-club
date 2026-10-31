package com.onshore.services.payload;

import jakarta.validation.constraints.NotBlank;

public record CategoryRequest(
    @NotBlank String code, @NotBlank String name, String description, @NotBlank String image) {}
