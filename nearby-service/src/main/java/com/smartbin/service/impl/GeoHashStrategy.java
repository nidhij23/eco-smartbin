package com.smartbin.service.impl;

import com.smartbin.service.SpatialStrategy;
import ch.hsr.geohash.GeoHash;
import org.springframework.stereotype.Component;

@Component
public class GeoHashStrategy  implements SpatialStrategy {
    @Override
    public String generateIndex(double lat, double lon) {
        return GeoHash.withCharacterPrecision(lat, lon, 12).toBase32();
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
