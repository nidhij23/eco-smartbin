package com.smartbin.service.impl;

import com.smartbin.dto.BinCreatedResponse;
import com.smartbin.dto.BinRequest;
import com.smartbin.dto.BinResponse;
import com.smartbin.dto.BinSearchRequest;
import com.smartbin.enums.WasteType;
import com.smartbin.entity.Bin;
import com.smartbin.entity.BinId;
import com.smartbin.repository.BinRepository;
import com.smartbin.service.BinService;
import com.smartbin.service.SpatialStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BinServiceImpl implements BinService {
    private final BinRepository binRepository;
    private final SpatialStrategy spatialStrategy;
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    @Override
    @Transactional
    public BinCreatedResponse createNewBin(BinRequest request) {
        String geohash = spatialStrategy.generateIndex(request.latitude(), request.longitude());
        Point location = geometryFactory.createPoint(new Coordinate(request.longitude(), request.latitude()));

        // Shard key logic (e.g., first 4 chars of geohash)
        String shardKey = extractShardKeyFromGeohash(geohash);
        Long binId = generateLongIdFromGeohash(geohash);

        Bin bin = Bin.builder()
                .shardKey(shardKey)
                .id(binId)
                .serialNumber(request.serialNumber())
                .model(request.model())
                .wasteType(request.wasteType().name())
                .totalCapacity(request.totalCapacity())
                .geohash(geohash)
                .location(location)
                .address(request.address())
                .build();

        Bin saved = binRepository.saveAndFlush(bin);

        return new BinCreatedResponse(saved.getId(), saved.getSerialNumber(), saved.getCreatedAt());
    }

    @Override
    public BinResponse getBinDetails(Long id) {
        String shardKey= extractShardKeyFromID(id);
        return binRepository.findById(new BinId(shardKey, id))
                .map(bin -> mapToResponse(bin, null))
                .orElseThrow(() -> new RuntimeException("Bin not found"));
    }

    @Override
    public Boolean deleteBin(Long id) {
        String shardKey=extractShardKeyFromID(id);
        if (binRepository.existsById(new BinId(shardKey, id))) {
            binRepository.deleteById(new BinId(shardKey, id));
            return true;
        }
        return false;
    }

    @Override
    public BinResponse updateBinData(Long id, BinRequest request) {
        String shardKey=extractShardKeyFromID(id);
        return binRepository.findById(new BinId(shardKey, id))
                .map(bin -> {
                    bin.setSerialNumber(request.serialNumber());
                    bin.setModel(request.model());
                    bin.setWasteType(request.wasteType().name());
                    bin.setTotalCapacity(request.totalCapacity());
                    bin.setAddress(request.address());
                    // Update location and geohash if coordinates changed
                    if (bin.getLocation().getY() != request.latitude() || bin.getLocation().getX() != request.longitude()) {
                        String geohash = spatialStrategy.generateIndex(request.latitude(), request.longitude());
                        Point location = geometryFactory.createPoint(new Coordinate(request.longitude(), request.latitude()));
                        bin.setGeohash(geohash);
                        bin.setLocation(location);
                        // Note: changing location might change shardKey if it's derived from geohash.
                        // If shardKey is part of PK, we can't easily change it without deleting and re-inserting.
                        // For now, assuming shardKey remains same or we don't support moving bins across shards in this update method.
                    }
                    return binRepository.save(bin);
                })
                .map(bin -> mapToResponse(bin, null))
                .orElseThrow(() -> new RuntimeException("Bin not found"));
    }

    @Override
    public List<BinResponse> findNearbyBins(BinSearchRequest criteria) {
        String prefix = spatialStrategy.getSearchCriteria(criteria.lat(), criteria.lon(), criteria.radiusInKm());

        return binRepository.findByGeohashStartingWith(prefix).stream()
                .map(bin -> {
                    double dist = spatialStrategy.calculateDistance(criteria.lat(), criteria.lon(), bin.getLocation().getY(), bin.getLocation().getX());
                    return mapToResponse(bin, dist);
                })
                .filter(res -> res.distanceInMeters() <= criteria.radiusInKm() * 1000)
                .sorted(Comparator.comparing(BinResponse::distanceInMeters))
                .toList();
    }

    private BinResponse mapToResponse(Bin bin, Double distance) {
        return new BinResponse(
                bin.getId(),
                bin.getSerialNumber(),
                bin.getAddress(),
                WasteType.valueOf(bin.getWasteType()),
                bin.getLocation().getY(),
                bin.getLocation().getX(),
                bin.getGeohash(),
                distance,
                bin.getLastUpdatedAt()
        );
    }

    private Long generateLongIdFromGeohash(String geohash) {
        return Long.parseLong(geohash, 36);
    }

    private String extractShardKeyFromGeohash(String geohash){
        return geohash.length() >= 4 ? geohash.substring(0, 4) : geohash;
    }

    private String extractShardKeyFromID(Long id){
        String geohash=extractGeoHashFromID(id);
        return extractShardKeyFromGeohash(geohash);
    }

    private String extractGeoHashFromID(Long id){
        return Long.toString(id, 36);
    }

    @Override
    public List<Bin> getMockBins(double lat, double lng) {
        return List.of();
    }
}
