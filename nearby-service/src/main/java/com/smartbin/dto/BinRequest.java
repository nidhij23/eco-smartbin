package com.smartbin.dto;


import com.smartbin.enums.WasteType;
import jakarta.validation.constraints.*;

public record BinRequest(
        @NotBlank(message = "Serial number is required")
        String serialNumber,
        String model,

        @NotBlank(message = "Address is required")
        @Size(max = 200)
        String address,

        @NotNull(message = "Waste type must be specified")
        WasteType wasteType,

        @DecimalMin("-90.0") @DecimalMax("90.0")
        double latitude,

        @DecimalMin("-180.0") @DecimalMax("180.0")
        double longitude,

        @Positive
        double totalCapacity
) {}