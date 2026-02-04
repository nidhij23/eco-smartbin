package com.smartbin.service.impl;

import com.smartbin.service.SpatialStrategy;

public class QuadtreeStrategy implements SpatialStrategy {
    @Override
    public String generateIndex(double lat, double lon) {
        return "";
    }

    @Override
    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        return 0;
    }

    @Override
    public String getSearchCriteria(double lat, double lon, double radiusKm) {
        return "";
    }
}
