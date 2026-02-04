package com.smartbin.dto;

import com.smartbin.enums.WasteType;

import java.time.OffsetDateTime;

public record BinResponse(
        Long id,
        String serialNumber,
        String address,
        WasteType wasteType,
        double latitude,
        double longitude,
        String geohash,
        Double distanceInMeters, // Null unless returned via search
        OffsetDateTime lastUpdated
) {}


