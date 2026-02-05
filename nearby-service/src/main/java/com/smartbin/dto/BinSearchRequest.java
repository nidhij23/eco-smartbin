package com.smartbin.dto;

import com.smartbin.enums.WasteType;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record BinSearchRequest(
    @DecimalMin("-90.0") @DecimalMax("90.0")
    double lat,

    @DecimalMin("-180.0") @DecimalMax("180.0")
    double lon,

    @Min(1) @Max(100)
    double radiusInKm,

    @Min(5) @Max(100)
    int count,

    WasteType wasteType
) {}
