package com.smartbin.dto;

import com.smartbin.enums.WasteType;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

public class BinSearchRequest {
    @DecimalMin("-90.0") @DecimalMax("90.0")
    public double lat;
    @Min(-180) @Max(180) double lng;
    @Min(1)
    public double radiusInKm;
    @Min(5) @Max(100) int count;
    WasteType wasteType;

    public double lat() {
        return 0;
    }

    public double lon() {
        return 0;
    }

    public int radiusInKm() {
        return 0;
    }
}
