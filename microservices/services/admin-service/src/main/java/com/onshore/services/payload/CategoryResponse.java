package com.onshore.services.payload;

import java.util.UUID;

public record CategoryResponse(
    UUID id, String code, String name, String description, String image, boolean active) {}
