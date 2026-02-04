FROM postgres:15

# This installs PostGIS directly from the Debian repositories
# which will automatically match your Mac's ARM architecture.
RUN apt-get update && \
    apt-get install -y postgresql-15-postgis-3 && \
    rm -rf /var/lib/apt/lists/*