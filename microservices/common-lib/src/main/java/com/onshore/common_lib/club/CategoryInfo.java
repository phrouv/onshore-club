package com.onshore.common_lib.club;

import java.util.UUID;

public record CategoryInfo(
    UUID id, String code, String name, String description, String image, boolean active) {}
