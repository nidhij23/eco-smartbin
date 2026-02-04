package com.bin_service.model;

public record Bin(
        Long id,
        String name,
        double lat,
        double lng,
        String type,       // "STATIC" or "MOBILE"
        int fillLevel      // 0 to 100
) {}