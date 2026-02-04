
-- 1. Create the Parent Table
CREATE TABLE bins (
                      id BIGINT NOT NULL,
                      shard_key VARCHAR(4) NOT NULL,
                      serial_number VARCHAR(50) UNIQUE NOT NULL,
                      model VARCHAR(100),
                      waste_type VARCHAR(30) NOT NULL, -- e.g., 'PLASTIC', 'PAPER', 'ORGANIC'
                      total_capacity DOUBLE PRECISION NOT NULL,
                      geohash VARCHAR(12) NOT NULL,
                      location GEOGRAPHY(Point, 4326) NOT NULL,
                      address VARCHAR(200) NOT NULL ,
                      created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                      last_updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                          PRIMARY KEY (shard_key, id)
);

-- 2. Spatial Index
CREATE INDEX idx_bins_location ON bins USING GIST (location);
-- Index for filtering by waste type
CREATE INDEX idx_bins_waste_type ON bins(waste_type);
CREATE INDEX idx_bins_geohash ON bins (geohash);

-- 3. Create the Child Table (Status)
CREATE TABLE bin_status (
                            bin_id BIGINT PRIMARY KEY,
                            bin_shard_key VARCHAR(4) NOT NULL,
                            fill_level INT DEFAULT 0 CHECK (fill_level BETWEEN 0 AND 100),
                            battery_level INT DEFAULT 100 CHECK (battery_level BETWEEN 0 AND 100),
                            status VARCHAR(20) DEFAULT 'OPERATIONAL',
                            last_reported_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

                            CONSTRAINT fk_bin_status_bins
                                FOREIGN KEY (bin_shard_key, bin_id)
                                    REFERENCES
                                        bins (shard_key, id)
                                    ON DELETE CASCADE
);

-- 4. Index for "Empty" filter
CREATE INDEX idx_bin_status_fill_level ON bin_status(fill_level);