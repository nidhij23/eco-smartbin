package com.smartbin.dto;

import java.time.OffsetDateTime;

public record BinCreatedResponse(
        Long id,
        String serialNumber,
        OffsetDateTime createdAt
) {}