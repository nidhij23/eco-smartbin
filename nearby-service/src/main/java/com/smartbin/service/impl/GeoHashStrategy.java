package com.smartbin.service.impl;

import com.smartbin.service.SpatialStrategy;
import ch.hsr.geohash.GeoHash;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class GeoHashStrategy  implements SpatialStrategy {
    @Override
    public String generateGeoIndex(double lat, double lon) {
        return GeoHash.withCharacterPrecision(lat, lon, 12).toBase32();
    }

    @Override
    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371; // Earth's radius in kilometers

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c; // Result in Kilometers
    }

    @Override
    public String getSearchCriteria(double lat, double lon, double radiusKm) {
        // 1. Generate the full-precision geohash for the center point
        // You can use a library like 'ch.hsr.geohash' or your own bit-logic
        String fullHash = GeoHash.withBitPrecision(lat, lon, 60).toBase32();

        // 2. Determine precision based on radius
        int precision;
        if (radiusKm <= 0.6) precision = 6;
        else if (radiusKm <= 2.4) precision = 5;
        else if (radiusKm <= 20) precision = 4;
        else precision = 3;

        // 3. Return the prefix (The "Search Box")
        return fullHash.substring(0, precision);
    }
}
