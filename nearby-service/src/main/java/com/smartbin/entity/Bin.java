package com.smartbin.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.locationtech.jts.geom.Point;

import java.time.OffsetDateTime;

@Entity
@Table(name = "bins")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(BinId.class)
public class Bin {
    @Id
    @Column(name = "shard_key", length = 4, nullable = false)
    private String shardKey;

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "serial_number", length = 50, unique = true, nullable = false)
    private String serialNumber;

    @Column(name = "model", length = 100)
    private String model;

    @Column(name = "waste_type", length = 30, nullable = false)
    private String wasteType;

    @Column(name = "total_capacity", nullable = false)
    private Double totalCapacity;

    @Column(name = "geohash", length = 12, nullable = false)
    private String geohash;

    @Column(name = "location", columnDefinition = "GEOGRAPHY(Point, 4326)", nullable = false)
    private Point location;

    @Column(name = "address", length = 200, nullable = false)
    private String address;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "last_updated_at")
    private OffsetDateTime lastUpdatedAt;
}
