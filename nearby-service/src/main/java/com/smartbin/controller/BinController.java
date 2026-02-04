package com.smartbin.controller;

import com.smartbin.dto.BinCreatedResponse;
import com.smartbin.entity.Bin;
import com.smartbin.dto.BinRequest;
import com.smartbin.dto.BinResponse;
import com.smartbin.dto.BinSearchRequest;
import com.smartbin.service.BinService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import jakarta.validation.Valid;
@RestController
@RequestMapping("/api/v1/bins")
@CrossOrigin(origins = "http://localhost:5173") // Crucial for React connection
public class BinController {

    private final BinService binService;

    public BinController(BinService binService) {
        this.binService = binService;
    }

    /**
     * 1. Create a new Bin
     * POST /api/v1/bins
     */
    @PostMapping
    public ResponseEntity<BinCreatedResponse> createBin(@Valid @RequestBody BinRequest request){
        BinCreatedResponse createdBin=binService.createNewBin(request);
        return new ResponseEntity<>(createdBin, HttpStatus.CREATED);
    }

    /**
     * 2. Get Bin Details by ID
     * GET /api/v1/bins/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<BinResponse> getBinById(@PathVariable Long id){
        return ResponseEntity.ok(binService.getBinDetails(id));
    }

    /**
     *
     * 3. Update Bin Details by ID
     * PUT /api/v1/bins/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<BinResponse> updateBin(
            @PathVariable Long id,
            @Valid @RequestBody BinRequest request){
        return ResponseEntity.ok(binService.updateBinData(id, request));
    }

    /**
     *
     * 4. Delete Bin by ID
     * DELETE /api/v1/bins/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteBin(@PathVariable Long id) {
        return ResponseEntity.ok(binService.deleteBin(id));
    }
    /**
     *
     * 5. Fetch nearby Bins
     * GET /api/v1/bins/search
     */
    @GetMapping("/search")
    public ResponseEntity<List<BinResponse>> searchNearByBins(
            @Valid BinSearchRequest filter) {
        List<BinResponse> bins = binService.findNearbyBins(filter);
        return ResponseEntity.ok(bins);
    }

    @GetMapping("/nearby")
    public List<Bin> getNearbyBins(@RequestParam double lat, @RequestParam double lng) {
        return binService.getMockBins(lat, lng);
    }
}
