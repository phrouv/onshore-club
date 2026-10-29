package com.onshore.services.payload;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record PurchaseRequest(@NotNull Integer credits, @NotNull BigDecimal price) {}
