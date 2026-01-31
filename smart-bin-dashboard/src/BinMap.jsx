import { useEffect, useState } from 'react';
import { MapContainer, TileLayer, Marker, Popup, useMap } from 'react-leaflet';
import L from 'leaflet';


const staticIcon = new L.Icon({
    iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-blue.png',
    shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.7/images/marker-shadow.png',
    iconSize: [25, 41],
    iconAnchor: [12, 41]
});

const mobileIcon = new L.Icon({
    iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-green.png',
    shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.7/images/marker-shadow.png',
    iconSize: [25, 41],
    iconAnchor: [12, 41]
});

// Setup icons to avoid the "missing marker" bug in Leaflet
// const staticIcon = new L.Icon({
//     iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-blue.png',
//     iconSize: [25, 41], iconAnchor: [12, 41], popupAnchor: [1, -34]
// });
//
// const mobileIcon = new L.Icon({
//     iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-green.png',
//     iconSize: [25, 41], iconAnchor: [12, 41], popupAnchor: [1, -34]
// });

// This helper component centers the map when the user's location is found
function RecenterMap({ coords }) {
    const map = useMap();
    useEffect(() => { map.setView(coords); }, [coords]);
    return null;
}

function MapRefresher() {
    const map = useMap();
    useEffect(() => {
        // This forces Leaflet to recount the pixels of its container
        setTimeout(() => {
            map.invalidateSize();
        }, 100);
    }, [map]);
    return null;
}

const fetchBins = async () => {
    try {
        // This is the call! It sends user latitude/longitude to Java
        const res = await fetch(`http://localhost:8080/api/bins/nearby?lat=${userLoc[0]}&lng=${userLoc[1]}`);
        const data = await res.json();
        setBins(data); // This updates the map markers
    } catch (e) {
        console.error("Java API not running yet - markers won't show.");
    }
};

export default function BinMap() {
    const [bins, setBins] = useState([]);
    const [userLoc, setUserLoc] = useState([28.6139, 77.2090]); // Default to Delhi coordinates

    useEffect(() => {
        // Faking a backend response for testing
        const mockBins = [
            {
                id: 1,
                name: "Central Park Bin",
                lat: 28.6139,
                lng: 77.2090,
                type: 'STATIC',
                fillLevel: 85
            },
            {
                id: 2,
                name: "Mobile Collection Truck",
                lat: 28.6200,
                lng: 77.2150,
                type: 'MOBILE',
                fillLevel: 30
            }
        ];

        setBins(mockBins);
    }, []); // Runs once on load


    // useEffect(() => {
    //     // 1. Get User Location
    //     navigator.geolocation.getCurrentPosition(
    //         (pos) => setUserLoc([pos.coords.latitude, pos.coords.longitude]),
    //         (err) => console.log("Location denied, using default.")
    //     );
    //
    //     // 2. Fetch Bins from Java Backend
    //     const fetchBins = async () => {
    //         try {
    //             const res = await fetch(`http://localhost:8080/api/bins/nearby?lat=${userLoc[0]}&lng=${userLoc[1]}`);
    //             const data = await res.json();
    //             setBins(data);
    //         } catch (e) { console.error("Java API not running yet?"); }
    //     };
    //
    //     fetchBins();
    //     const timer = setInterval(fetchBins, 5000); // Update every 5s for mobile bins
    //     return () => clearInterval(timer);
    // }, [userLoc]);

    return (
        <div className="h-full w-full">
            <MapContainer center={userLoc} zoom={14} className="h-full w-full">
                <TileLayer url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png" />
                <MapRefresher />
                <RecenterMap coords={userLoc} />

                {bins.map(bin => (
                    <Marker
                        key={bin.id}
                        position={[bin.lat, bin.lng]}
                        icon={bin.type === 'MOBILE' ? mobileIcon : staticIcon}
                    >
                        <Popup>
                            <div className="text-sm">
                                <p className="font-bold">{bin.name}</p>
                                <p>Fill: {bin.fillLevel}%</p>
                                <span className="text-xs text-gray-500">{bin.type}</span>
                            </div>
                        </Popup>
                    </Marker>
                ))}
            </MapContainer>
        </div>
    );
}