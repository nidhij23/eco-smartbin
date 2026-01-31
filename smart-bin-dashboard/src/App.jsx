import BinMap from './BinMap';

function App() {
    return (
        <div className="h-screen w-screen overflow-hidden">
            <div className="absolute top-4 left-12 z-[1000] bg-white p-4 rounded-lg shadow-md">
                <h1 className="text-xl font-bold text-green-700">Eco-Bin Live Map</h1>
                <p className="text-xs text-gray-500">Static (Blue) | Mobile (Green)</p>
            </div>
            <BinMap />
        </div>
    )
}

export default App;