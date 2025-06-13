package controller;

import domain.*;
import domain.graph.DijkstraAirportGraph;
import domain.list.CircularDoublyLinkedList;
import domain.list.DoublyLinkedList;
import domain.list.ListException;
import domain.tree.AVLTree;
import java.time.LocalDateTime;
import java.util.*;

public class FlightController {
    private CircularDoublyLinkedList flightsList;
    private DoublyLinkedList airportsList;
    private AVLTree passengersTree;
    private DijkstraAirportGraph routeGraph;
    private Random random;
    private int flightCounter;

    public FlightController() {
        this.flightsList = new CircularDoublyLinkedList();
        this.airportsList = new DoublyLinkedList();
        this.passengersTree = new AVLTree();
        this.routeGraph = new DijkstraAirportGraph(50); // Máximo 50 aeropuertos
        this.random = new Random();
        this.flightCounter = 1000; // Empezar numeración de vuelos desde 1000
    }

    // Getters y Setters
    public CircularDoublyLinkedList getFlightsList() {
        return flightsList;
    }

    public void setFlightsList(CircularDoublyLinkedList flightsList) {
        this.flightsList = flightsList;
    }

    public DoublyLinkedList getAirportsList() {
        return airportsList;
    }

    public void setAirportsList(DoublyLinkedList airportsList) {
        this.airportsList = airportsList;
        loadAirportsToGraph();
    }

    public AVLTree getPassengersTree() {
        return passengersTree;
    }

    public void setPassengersTree(AVLTree passengersTree) {
        this.passengersTree = passengersTree;
    }

    public DijkstraAirportGraph getRouteGraph() {
        return routeGraph;
    }

    /**
     * Carga los aeropuertos al grafo de rutas
     */
    private void loadAirportsToGraph() {
        try {
            if (airportsList.isEmpty()) return;

            for (int i = 1; i <= airportsList.size(); i++) {
                Airport airport = (Airport) airportsList.getNode(i).getData();
                routeGraph.addVertex(airport.getCode());
            }
        } catch (Exception e) {
            System.err.println("Error loading airports to graph: " + e.getMessage());
        }
    }

    /**
     * Genera rutas aleatorias entre aeropuertos
     */
    public void generateRandomRoutes() {
        try {
            routeGraph.generateRandomRoutes();
            System.out.println("✅ Random routes generated successfully");
        } catch (Exception e) {
            System.err.println("❌ Error generating random routes: " + e.getMessage());
        }
    }

    /**
     * Obtiene los 5 aeropuertos con más conexiones
     */
    public List<Airport> getTopConnectedAirports() {
        List<Airport> topAirports = new ArrayList<>();

        try {
            List<Object> topCodes = routeGraph.getTopConnectedAirports(5);

            for (Object code : topCodes) {
                Airport airport = findAirportByCode((Integer) code);
                if (airport != null) {
                    topAirports.add(airport);
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting top connected airports: " + e.getMessage());
        }

        return topAirports;
    }

    /**
     * Busca un aeropuerto por código
     */
    private Airport findAirportByCode(int code) {
        try {
            if (airportsList.isEmpty()) return null;

            for (int i = 1; i <= airportsList.size(); i++) {
                Airport airport = (Airport) airportsList.getNode(i).getData();
                if (airport.getCode() == code) {
                    return airport;
                }
            }
        } catch (Exception e) {
            System.err.println("Error finding airport by code: " + e.getMessage());
        }
        return null;
    }

    /**
     * Genera vuelos aleatorios desde los aeropuertos con más conexiones
     */
    public void generateRandomFlights(int numberOfFlights) {
        try {
            List<Airport> topAirports = getTopConnectedAirports();
            if (topAirports.isEmpty()) {
                System.out.println("⚠️ No airports available for flight generation");
                return;
            }

            System.out.println("🚀 Generating " + numberOfFlights + " random flights...");

            for (int i = 0; i < numberOfFlights; i++) {
                generateSingleRandomFlight(topAirports);
            }

            System.out.println("✅ Generated " + numberOfFlights + " flights successfully");
        } catch (Exception e) {
            System.err.println("❌ Error generating random flights: " + e.getMessage());
        }
    }

    /**
     * Genera un vuelo aleatorio individual
     */
    private void generateSingleRandomFlight(List<Airport> topAirports) {
        try {
            // Seleccionar aeropuerto origen aleatorio
            Airport origin = topAirports.get(random.nextInt(topAirports.size()));

            // Seleccionar aeropuerto destino aleatorio (diferente al origen)
            Airport destination;
            do {
                destination = getRandomAirport();
            } while (destination == null || destination.getCode() == origin.getCode());

            // Generar hora de salida aleatoria (próximas 48 horas)
            LocalDateTime departureTime = LocalDateTime.now()
                    .plusHours(random.nextInt(48))
                    .plusMinutes(random.nextInt(60));

            // Generar capacidad aleatoria
            int[] capacities = {100, 150, 200};
            int capacity = capacities[random.nextInt(capacities.length)];

            // Crear vuelo
            Flight flight = new Flight(
                    ++flightCounter,
                    origin.getName(),
                    destination.getName(),
                    departureTime,
                    capacity
            );

            // Generar ocupación aleatoria (0-80% de la capacidad)
            int maxOccupancy = (int) (capacity * 0.8);
            int occupancy = random.nextInt(maxOccupancy + 1);
            flight.setOccupancy(occupancy);

            // Agregar vuelo a la lista
            flightsList.add(flight);

            System.out.println("✈️ Generated: " + flight);
        } catch (Exception e) {
            System.err.println("Error generating single flight: " + e.getMessage());
        }
    }

    /**
     * Obtiene un aeropuerto aleatorio
     */
    private Airport getRandomAirport() {
        try {
            if (airportsList.isEmpty()) return null;

            int randomIndex = random.nextInt(airportsList.size()) + 1;
            return (Airport) airportsList.getNode(randomIndex).getData();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Genera pasajeros aleatorios
     */
    public void generateRandomPassengers(int numberOfPassengers) {
        String[] names = {
                "Juan Pérez", "María González", "Carlos López", "Ana Martínez", "Luis Rodríguez",
                "Carmen Hernández", "José García", "Lucía Fernández", "Pedro Sánchez", "Isabel Ruiz",
                "Francisco Díaz", "Pilar Moreno", "Antonio Muñoz", "Teresa Álvarez", "Manuel Romero",
                "Dolores Navarro", "Jesús Torres", "Rosario Domínguez", "Alejandro Vázquez", "Mercedes Ramos"
        };

        String[] nationalities = {
                "Costa Rica", "México", "España", "Colombia", "Argentina", "Chile", "Perú",
                "Venezuela", "Ecuador", "Uruguay", "Paraguay", "Bolivia", "Panamá", "Guatemala", "Honduras"
        };

        System.out.println("👥 Generating " + numberOfPassengers + " random passengers...");

        for (int i = 0; i < numberOfPassengers; i++) {
            int id = 10000 + i; // IDs desde 10000
            String name = names[random.nextInt(names.length)];
            String nationality = nationalities[random.nextInt(nationalities.length)];

            Passenger passenger = new Passenger(id, name, nationality);
            passengersTree.insert(passenger);

            if (i % 100 == 0) { // Mostrar progreso cada 100 pasajeros
                System.out.println("Generated " + (i + 1) + " passengers...");
            }
        }

        System.out.println("✅ Generated " + numberOfPassengers + " passengers successfully");
    }

    /**
     * Asigna pasajeros aleatorios a vuelos
     */
    public void assignRandomPassengersToFlights() {
        try {
            if (flightsList.isEmpty()) {
                System.out.println("⚠️ No flights available for passenger assignment");
                return;
            }

            System.out.println("🎫 Assigning passengers to flights...");

            for (int i = 1; i <= flightsList.size(); i++) {
                Flight flight = (Flight) flightsList.getNode(i).getData();
                assignPassengersToFlight(flight);
            }

            System.out.println("✅ Passengers assigned to flights successfully");
        } catch (Exception e) {
            System.err.println("❌ Error assigning passengers to flights: " + e.getMessage());
        }
    }

    /**
     * Asigna pasajeros a un vuelo específico
     */
    private void assignPassengersToFlight(Flight flight) {
        int currentOccupancy = flight.getOccupancy();

        for (int i = 0; i < currentOccupancy; i++) {
            // Generar ID de pasajero aleatorio
            int passengerId = 10000 + random.nextInt(1000);
            Passenger passenger = passengersTree.search(passengerId);

            if (passenger != null) {
                passenger.addFlightToHistory(flight);
            }
        }
    }

    /**
     * Calcula la ruta más corta entre dos aeropuertos
     */
    public DijkstraAirportGraph.DijkstraResult calculateShortestRoute(int originCode, int destinationCode) {
        try {
            return routeGraph.dijkstra(originCode, destinationCode);
        } catch (Exception e) {
            System.err.println("Error calculating shortest route: " + e.getMessage());
            return new DijkstraAirportGraph.DijkstraResult();
        }
    }

    /**
     * Muestra estadísticas de vuelos
     */
    public void showFlightStatistics() {
        try {
            System.out.println("\n=== ESTADÍSTICAS DE VUELOS ===");
            System.out.println("Total de vuelos: " + flightsList.size());

            if (flightsList.isEmpty()) return;

            int totalCapacity = 0;
            int totalOccupancy = 0;

            for (int i = 1; i <= flightsList.size(); i++) {
                Flight flight = (Flight) flightsList.getNode(i).getData();
                totalCapacity += flight.getCapacity();
                totalOccupancy += flight.getOccupancy();
            }

            double averageOccupancy = (double) totalOccupancy / totalCapacity * 100;

            System.out.println("Capacidad total: " + totalCapacity + " asientos");
            System.out.println("Ocupación total: " + totalOccupancy + " pasajeros");
            System.out.println("Ocupación promedio: " + String.format("%.2f", averageOccupancy) + "%");
            System.out.println("Total de pasajeros: " + passengersTree.size());

        } catch (Exception e) {
            System.err.println("Error showing flight statistics: " + e.getMessage());
        }
    }

    /**
     * Muestra la red de aeropuertos por consola
     */
    public void showAirportNetwork() {
        System.out.println("\n=== RED DE AEROPUERTOS ===");
        System.out.println("Grafo de rutas:");
        System.out.println(routeGraph.toString());

        System.out.println("\nAeropuertos con más conexiones:");
        List<Airport> topAirports = getTopConnectedAirports();
        for (int i = 0; i < topAirports.size(); i++) {
            Airport airport = topAirports.get(i);
            System.out.println((i + 1) + ". " + airport.getName() + " (" + airport.getCode() + ")");
        }
    }

    /**
     * Valida la ruta más corta entre dos aeropuertos específicos
     */
    public void validateShortestRoute(int origin, int destination) {
        System.out.println("\n=== VALIDACIÓN DE RUTA MÁS CORTA ===");
        System.out.println("Origen: " + origin + " → Destino: " + destination);

        DijkstraAirportGraph.DijkstraResult result = calculateShortestRoute(origin, destination);
        System.out.println("Resultado: " + result);

        if (result.isPathExists()) {
            System.out.println("✅ Ruta encontrada exitosamente");
        } else {
            System.out.println("❌ No existe ruta entre los aeropuertos especificados");
        }
    }
}