package com.smartbin.service;

public interface SpatialStrategy {
    /**
     * Converts coordinates into a searchable string index (Geohash, H3, etc.)
     */
    String generateIndex(double lat, double lon);

    /**
     * Calculates the "as-the-crow-flies" distance between two points.
     */
    double calculateDistance(double lat1, double lon1, double lat2, double lon2);

    /**
     * Determines the rough search area (prefix) based on a search radius.
     */
    String getSearchCriteria(double lat, double lon, double radiusKm);
}