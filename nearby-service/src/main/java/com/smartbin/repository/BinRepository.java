package com.smartbin.repository;

import com.smartbin.entity.Bin;
import com.smartbin.entity.BinId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BinRepository extends JpaRepository<Bin, BinId> {

    // This is the "Magic" query for our spatial search
    // It generates: SELECT * FROM bins WHERE geohash LIKE 'tdr2n%'
    List<Bin> findByGeohashStartingWith(String prefix);

    // Find by serial number (useful for status updates from hardware)
    Optional<Bin> findBySerialNumber(String serialNumber);
}
