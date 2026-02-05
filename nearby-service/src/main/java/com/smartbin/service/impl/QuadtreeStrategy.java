package com.smartbin.service.impl;

import com.smartbin.service.SpatialStrategy;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
public class QuadtreeStrategy implements SpatialStrategy {

    // Level of detail for the Quadtree. Level 23 is roughly ~1 meter precision at the equator.
    // Level 15 is roughly ~1km blocks.
    private static final int DEFAULT_LEVEL = 15;
    private static final double EARTH_RADIUS_KM = 6371.0;


    @Override
    public String generateGeoIndex(double lat, double lon) {
        return latLonToQuadKey(lat, lon, DEFAULT_LEVEL);
    }

    @Override
    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Haversine formula for distance
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }

    @Override
    public String getSearchCriteria(double lat, double lon, double radiusKm) {
        // For Quadtree, finding a single prefix for a radius is tricky because
        // a radius might cross multiple quadtree tiles.
        // A simple approximation is to find the tile that covers the radius.
        // As we zoom out (lower levels), tiles get bigger.
        
        // This is a simplified implementation. A robust one would return a list of keys.
        // For this interface which returns a single String, we'll return the parent tile
        // that roughly covers the area.
        
        // Rough estimation: Level 15 ~ 1km. Each level up doubles the size.
        // We can dynamically adjust the level based on radius.
        int level = DEFAULT_LEVEL;
        // Very rough heuristic:
        if (radiusKm > 5) level = 13;
        if (radiusKm > 20) level = 11;
        
        return latLonToQuadKey(lat, lon, level);
    }

    // --- Helper Methods for Quadkey Generation (Bing Maps Tile System Logic) ---

    private String latLonToQuadKey(double lat, double lon, int level) {
        long pixelX = latLonToPixelX(lon, level);
        long pixelY = latLonToPixelY(lat, level);
        long tileX = pixelX / 256;
        long tileY = pixelY / 256;
        return tileXYToQuadKey(tileX, tileY, level);
    }

    private long latLonToPixelX(double lon, int level) {
        double x = (lon + 180) / 360;
        long mapSize = 256L << level;
        return (long) clip(x * mapSize, 0, mapSize - 1);
    }

    private long latLonToPixelY(double lat, int level) {
        double sinLatitude = Math.sin(lat * Math.PI / 180);
        double y = 0.5 - Math.log((1 + sinLatitude) / (1 - sinLatitude)) / (4 * Math.PI);
        long mapSize = 256L << level;
        return (long) clip(y * mapSize, 0, mapSize - 1);
    }

    private double clip(double n, double minValue, double maxValue) {
        return Math.min(Math.max(n, minValue), maxValue);
    }

    private String tileXYToQuadKey(long tileX, long tileY, int level) {
        StringBuilder quadKey = new StringBuilder();
        for (int i = level; i > 0; i--) {
            char digit = '0';
            int mask = 1 << (i - 1);
            if ((tileX & mask) != 0) {
                digit++;
            }
            if ((tileY & mask) != 0) {
                digit++;
                digit++;
            }
            quadKey.append(digit);
        }
        return quadKey.toString();
    }
}
