package com.onshore.services.payload;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record BookRequest(@NotNull UUID classId) {}
