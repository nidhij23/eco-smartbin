package com.smartbin.service;

import com.smartbin.dto.BinCreatedResponse;
import com.smartbin.dto.BinRequest;
import com.smartbin.dto.BinResponse;
import com.smartbin.dto.BinSearchRequest;
import com.smartbin.entity.Bin;

import java.util.List;

public interface BinService {
    BinCreatedResponse createNewBin(BinRequest request);
    BinResponse getBinDetails(Long id);
    Boolean deleteBin(Long id);
    BinResponse updateBinData(Long id, BinRequest request);
    List<BinResponse> findNearbyBins(BinSearchRequest criteria);
    List<Bin> getMockBins(double lat, double lng);
}
