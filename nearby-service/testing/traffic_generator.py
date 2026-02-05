import requests
import random
import time
import json
import uuid

# Configuration
BASE_URL = "http://localhost:8080/api/v1/bins"
HEADERS = {"Content-Type": "application/json"}
WASTE_TYPES = ["PLASTIC", "PAPER", "GLASS", "ORGANIC", "GENERAL", "METAL"]
MODELS = ["SmartBin V1", "SmartBin V2", "EcoContainer X", "CityBin Pro"]

# Store created bins to perform operations on them later
# Format: {'id': 123, 'shardKey': '1202', 'serialNumber': 'SN-xyz'}
created_bins = []

def generate_random_coordinate():
    # Generate coordinates roughly around Los Angeles for testing clustering
    lat = 34.05 + random.uniform(-0.1, 0.1)
    lon = -118.24 + random.uniform(-0.1, 0.1)
    return lat, lon

def create_bin():
    lat, lon = generate_random_coordinate()
    serial_number = f"SN-{uuid.uuid4().hex[:8]}"

    payload = {
        "serialNumber": serial_number,
        "model": random.choice(MODELS),
        "address": f"{random.randint(1, 999)} Random St, Test City",
        "wasteType": random.choice(WASTE_TYPES),
        "latitude": lat,
        "longitude": lon,
        "totalCapacity": random.choice([50.0, 100.0, 120.0, 200.0])
    }

    try:
        response = requests.post(BASE_URL, json=payload, headers=HEADERS)
        if response.status_code == 201:
            data = response.json()
            # Extract shardKey from geohash (first 4 chars)
            geohash = data.get('geohash', '')
            shard_key = geohash[:4] if len(geohash) >= 4 else geohash

            bin_info = {
                'id': data['id'],
                'serialNumber': data['serialNumber']
            }
            created_bins.append(bin_info)
            print(f"[CREATE] Success: ID={data['id']}")
        else:
            print(f"[CREATE] Failed: {response.status_code} - {response.text}")
    except Exception as e:
        print(f"[CREATE] Error: {e}")

def get_bin():
    if not created_bins:
        return

    target = random.choice(created_bins)
    url = f"{BASE_URL}/{target['id']}"

    try:
        response = requests.get(url, headers=HEADERS)
        if response.status_code == 200:
            print(f"[GET] Success: ID={target['id']}")
        else:
            print(f"[GET] Failed: {response.status_code} - {response.text}")
            # If 404, maybe remove from list
            if response.status_code == 404:
                created_bins.remove(target)
    except Exception as e:
        print(f"[GET] Error: {e}")

def update_bin():
    if not created_bins:
        return

    target = random.choice(created_bins)
    url = f"{BASE_URL}/{target['id']}"

    # Keep location same to avoid shard change complexity for this simple script
    # But update other fields
    payload = {
        "serialNumber": target['serialNumber'], # Keep serial same
        "model": f"{random.choice(MODELS)} (Updated)",
        "address": "Updated Address 123",
        "wasteType": random.choice(WASTE_TYPES),
        "latitude": 34.05, # Dummy, will be ignored if logic checks existing
        "longitude": -118.24,
        "totalCapacity": 150.0
    }

    # We need to fetch current details first to keep lat/lon if we don't want to move it
    # For simplicity, let's just send a valid request.
    # Note: The server logic might move the bin if we send new coordinates.
    # Let's generate new coordinates to test movement too.
    lat, lon = generate_random_coordinate()
    payload['latitude'] = lat
    payload['longitude'] = lon

    try:
        response = requests.put(url, json=payload, headers=HEADERS)
        if response.status_code == 200:
            print(f"[UPDATE] Success: ID={target['id']}")
        else:
            print(f"[UPDATE] Failed: {response.status_code} - {response.text}")
    except Exception as e:
        print(f"[UPDATE] Error: {e}")

def delete_bin():
    if not created_bins:
        return

    target = random.choice(created_bins)
    url = f"{BASE_URL}/{target['id']}"

    try:
        response = requests.delete(url, headers=HEADERS)
        if response.status_code == 200:
            print(f"[DELETE] Success: ID={target['id']}")
            created_bins.remove(target)
        else:
            print(f"[DELETE] Failed: {response.status_code} - {response.text}")
    except Exception as e:
        print(f"[DELETE] Error: {e}")

def search_nearby():
    lat, lon = generate_random_coordinate()
    radius = random.choice([1, 5, 10, 20])

    params = {
        "lat": lat,
        "lon": lon,
        "radiusInKm": radius,
        "wasteType": random.choice(WASTE_TYPES) # Optional filter if supported
    }

    try:
        response = requests.get(f"{BASE_URL}/search", params=params, headers=HEADERS)
        if response.status_code == 200:
            count = len(response.json())
            print(f"[SEARCH] Success: Found {count} bins within {radius}km")
        else:
            print(f"[SEARCH] Failed: {response.status_code} - {response.text}")
    except Exception as e:
        print(f"[SEARCH] Error: {e}")

def main():
    print("Starting Traffic Generator...")
    print("Press Ctrl+C to stop.")

    # Initial population
    print("Populating initial data...")
    for _ in range(10):
        create_bin()

    while True:
        action = random.choices(
            ['create', 'get', 'update', 'delete', 'search'],
            weights=[30, 40, 10, 5, 15], # Probabilities
            k=1
        )[0]

        if action == 'create':
            create_bin()
        elif action == 'get':
            get_bin()
        elif action == 'delete':
            delete_bin()

        time.sleep(0.5) # Sleep 500ms between requests

if __name__ == "__main__":
    main()
