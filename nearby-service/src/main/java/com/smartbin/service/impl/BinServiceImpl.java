package com.smartbin.service.impl;

import com.smartbin.dto.BinCreatedResponse;
import com.smartbin.dto.BinRequest;
import com.smartbin.dto.BinResponse;
import com.smartbin.dto.BinSearchRequest;
import com.smartbin.enums.WasteType;
import com.smartbin.entity.Bin;
import com.smartbin.entity.BinId;
import com.smartbin.exception.DuplicateResourceException;
import com.smartbin.exception.ResourceNotFoundException;
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
        // Check for duplicate serial number
        binRepository.findBySerialNumber(request.serialNumber()).ifPresent(bin -> {
            throw new DuplicateResourceException("Bin with serial number '" + request.serialNumber() + "' already exists.");
        });

        String geohash = spatialStrategy.generateGeoIndex(request.latitude(), request.longitude());
        Point location = geometryFactory.createPoint(new Coordinate(request.longitude(), request.latitude()));

        String shardKey = extractShardKeyFromGeoHash(geohash);
//        Long binId = System.currentTimeMillis();
        Long binId = extractLongIdFromGeohash(geohash);

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
        String shardKey = extractShardKeyFromID(id);
        return binRepository.findById(new BinId(shardKey, id))
                .map(bin -> mapToResponse(bin, null))
                .orElseThrow(() -> new ResourceNotFoundException("Bin not found with id: " + id + " and shardKey: " + shardKey));
    }

    @Override
    public Boolean deleteBin(Long id) {
        String shardKey = extractShardKeyFromID(id);
        if (!binRepository.existsById(new BinId(shardKey, id))) {
            throw new ResourceNotFoundException("Bin not found with id: " + id + " and shardKey: " + shardKey);
        }
        binRepository.deleteById(new BinId(shardKey, id));
        return true;
    }

    @Override
    public BinResponse updateBinData(Long id, BinRequest request) {
        String shardKey = extractShardKeyFromID(id);
        return binRepository.findById(new BinId(shardKey, id))
                .map(bin -> {
                    bin.setSerialNumber(request.serialNumber());
                    bin.setModel(request.model());
                    bin.setWasteType(request.wasteType().name());
                    bin.setTotalCapacity(request.totalCapacity());
                    bin.setAddress(request.address());
                    if (bin.getLocation().getY() != request.latitude() || bin.getLocation().getX() != request.longitude()) {
                        String geohash = spatialStrategy.generateGeoIndex(request.latitude(), request.longitude());
                        Point location = geometryFactory.createPoint(new Coordinate(request.longitude(), request.latitude()));
                        bin.setGeohash(geohash);
                        bin.setLocation(location);
                    }
                    return binRepository.saveAndFlush(bin);
                })
                .map(bin -> mapToResponse(bin, null))
                .orElseThrow(() -> new ResourceNotFoundException("Bin not found with id: " + id + " and shardKey: " + shardKey));
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

    private String extractShardKeyFromGeoHash(String geohash){
        return geohash.length() >= 4 ? geohash.substring(0, 4) : geohash;
    }

    private String extractShardKeyFromID(Long id) {
        String geohash = extractGeoHashFromID(id);
        return extractShardKeyFromGeoHash(geohash);
    }

    private Long extractLongIdFromGeohash(String geohash) {
        return Long.parseLong(geohash, 36);
    }

    private String extractGeoHashFromID(Long id) {
        return Long.toString(id, 36);
    }

    @Override
    public List<Bin> getMockBins(double lat, double lng) {
        return List.of();
    }
}
