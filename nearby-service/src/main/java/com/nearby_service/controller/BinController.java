package com.nearby_service.controller;

import com.nearby_service.model.Bin;
import com.nearby_service.services.BinService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bins")
@CrossOrigin(origins = "http://localhost:5173") // Crucial for React connection
public class BinController {

    private final BinService binService;

    public BinController(BinService binService) {
        this.binService = binService;
    }

    @GetMapping("/nearby")
    public List<Bin> getNearbyBins(@RequestParam double lat, @RequestParam double lng) {
        return binService.getMockBins(lat, lng);
    }
}
