-- Create the database if it doesn't exist
SELECT 'CREATE DATABASE smartbin_service'
    WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'smartbin_service')\gexec

-- Connect to that database
    \c smartbin_service;

-- Enable the spatial engine
CREATE EXTENSION IF NOT EXISTS postgis;