package com.onshore.services.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.UUID;

public record ActivityRequest(
    @NotNull UUID clubId,
    @NotBlank @Size(max = 100, message = "name must not exceed 100 characters") String name,
    @NotNull UUID categoryId,
    String description,
    String instructorFirstName,
    String instructorLastName,
    @Email String instructorEmail,
    String instructorPhoneNumber,
    LocalDate subscriptionDate) {}
