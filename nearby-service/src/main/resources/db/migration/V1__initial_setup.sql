
-- 1. Create the Parent Table (Partitioned)
CREATE TABLE bins (
                      id BIGINT NOT NULL,
                      shard_key VARCHAR(4) NOT NULL,
                      serial_number VARCHAR(50) NOT NULL, -- Unique constraint must include partition key, so we drop global UNIQUE for now or use composite
                      model VARCHAR(100),
                      waste_type VARCHAR(30) NOT NULL,
                      total_capacity DOUBLE PRECISION NOT NULL,
                      geohash VARCHAR(12) NOT NULL,
                      location GEOGRAPHY(Point, 4326) NOT NULL,
                      address VARCHAR(200) NOT NULL ,
                      created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                      last_updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                      PRIMARY KEY (shard_key, id),
                      UNIQUE (shard_key, serial_number) -- Unique constraint must include the partition key
) PARTITION BY HASH (shard_key);

-- 2. Create Partitions (Let's create 4 partitions for now)
CREATE TABLE bins_p0 PARTITION OF bins FOR VALUES WITH (MODULUS 4, REMAINDER 0);
CREATE TABLE bins_p1 PARTITION OF bins FOR VALUES WITH (MODULUS 4, REMAINDER 1);
CREATE TABLE bins_p2 PARTITION OF bins FOR VALUES WITH (MODULUS 4, REMAINDER 2);
CREATE TABLE bins_p3 PARTITION OF bins FOR VALUES WITH (MODULUS 4, REMAINDER 3);

-- 3. Spatial Index (Must be created on the parent table, Postgres propagates it)
CREATE INDEX idx_bins_location ON bins USING GIST (location);
-- Index for filtering by waste type
CREATE INDEX idx_bins_waste_type ON bins(waste_type);
CREATE INDEX idx_bins_geohash ON bins (geohash);

-- 4. Create the Child Table (Status)
-- Note: Foreign keys to partitioned tables work, but the referenced columns must be a unique constraint on the parent.
-- We have PRIMARY KEY (shard_key, id), so we can reference that.
CREATE TABLE bin_status (
                            bin_id BIGINT,
                            bin_shard_key VARCHAR(4),
                            fill_level INT DEFAULT 0 CHECK (fill_level BETWEEN 0 AND 100),
                            battery_level INT DEFAULT 100 CHECK (battery_level BETWEEN 0 AND 100),
                            status VARCHAR(20) DEFAULT 'OPERATIONAL',
                            last_reported_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

                            PRIMARY KEY (bin_shard_key, bin_id), -- Child also needs composite PK to align with parent usually, or just for lookup

                            CONSTRAINT fk_bin_status_bins
                                FOREIGN KEY (bin_shard_key, bin_id)
                                    REFERENCES bins (shard_key, id)
                                    ON DELETE CASCADE
);

-- 5. Index for "Empty" filter
CREATE INDEX idx_bin_status_fill_level ON bin_status(fill_level);
