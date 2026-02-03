package com.nearby_service.services;

import com.nearby_service.model.Bin;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BinService {

    public List<Bin> getMockBins(double lat, double lng) {
        // We create markers relative to the user's current position
        return List.of(
                new Bin(1L, "Main Square Bin", lat + 0.002, lng + 0.001, "STATIC", 85),
                new Bin(2L, "Mobile Truck #42", lat - 0.003, lng + 0.004, "MOBILE", 15),
                new Bin(3L, "Park Entrance", lat + 0.005, lng - 0.002, "STATIC", 40)
        );
    }
}