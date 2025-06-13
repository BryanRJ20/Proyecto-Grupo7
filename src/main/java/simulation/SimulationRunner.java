package simulation;

import controller.FlightController;
import domain.Airport;
import domain.graph.DijkstraAirportGraph;
import domain.list.DoublyLinkedList;
import domain.tree.AVLTree;
import util.DataLoader;

import java.util.Scanner;

/**
 * Clase principal para ejecutar la simulación de red aérea por consola
 * Cumple con los requerimientos del primer entregable
 */
public class SimulationRunner {
    private FlightController flightController;
    private Scanner scanner;

    public SimulationRunner() {
        this.flightController = new FlightController();
        this.scanner = new Scanner(System.in);
    }

    /**
     * Método principal de simulación
     */
    public void runSimulation() {
        System.out.println("🚀 INICIANDO SIMULACIÓN DE RED AÉREA");
        System.out.println("=====================================");

        // 1. Cargar aeropuertos desde JSON
        loadAirportsFromJson();

        // 2. Generar rutas aleatorias
        generateRoutes();

        // 3. Implementar y validar Dijkstra
        validateDijkstraAlgorithm();

        // 4. Generar pasajeros aleatorios
        generatePassengers();

        // 5. Crear vuelos aleatorios
        generateFlights();

        // 6. Mostrar red aérea por consola
        showAirportNetwork();

        // 7. Mostrar menú interactivo
        showInteractiveMenu();

        System.out.println("🎯 SIMULACIÓN COMPLETADA");
    }

    /**
     * Carga aeropuertos desde archivo JSON
     */
    private void loadAirportsFromJson() {
        System.out.println("\n📂 CARGANDO AEROPUERTOS DESDE JSON...");

        // Intentar cargar desde el archivo existente
        DoublyLinkedList airports = DataLoader.loadAirportsFromJson("src/main/resources/ucr/project/airports.json");

        if (airports.isEmpty()) {
            // Si no existe, crear archivos de ejemplo
            DataLoader.createSampleJsonFiles();
            airports = DataLoader.loadAirportsFromJson("src/main/resources/data/airports.json");
        }

        flightController.setAirportsList(airports);

        try {
            System.out.println("✅ Aeropuertos cargados: " + airports.size());

            // Mostrar algunos aeropuertos
            System.out.println("\n📋 Aeropuertos disponibles:");
            for (int i = 1; i <= Math.min(airports.size(), 5); i++) {
                Airport airport = (Airport) airports.getNode(i).getData();
                System.out.println("  • " + airport.getName() + " (" + airport.getCode() + ") - " + airport.getCountry());
            }
            if (airports.size() > 5) {
                System.out.println("  ... y " + (airports.size() - 5) + " aeropuertos más");
            }
        } catch (Exception e) {
            System.err.println("❌ Error mostrando aeropuertos: " + e.getMessage());
        }
    }

    /**
     * Genera rutas aleatorias entre aeropuertos
     */
    private void generateRoutes() {
        System.out.println("\n🗺️ GENERANDO RUTAS ALEATORIAS...");
        flightController.generateRandomRoutes();
        System.out.println("✅ Rutas generadas exitosamente");
    }

    /**
     * Valida el algoritmo de Dijkstra
     */
    private void validateDijkstraAlgorithm() {
        System.out.println("\n🧮 VALIDANDO ALGORITMO DE DIJKSTRA...");

        try {
            DoublyLinkedList airports = flightController.getAirportsList();
            if (airports.size() >= 2) {
                // Obtener dos aeropuertos para probar
                Airport airport1 = (Airport) airports.getNode(1).getData();
                Airport airport2 = (Airport) airports.getNode(2).getData();

                System.out.println("🔍 Calculando ruta más corta entre:");
                System.out.println("   Origen: " + airport1.getName() + " (" + airport1.getCode() + ")");
                System.out.println("   Destino: " + airport2.getName() + " (" + airport2.getCode() + ")");

                DijkstraAirportGraph.DijkstraResult result = flightController.calculateShortestRoute(
                        airport1.getCode(), airport2.getCode()
                );

                System.out.println("📊 Resultado: " + result);

                if (result.isPathExists()) {
                    System.out.println("✅ Algoritmo de Dijkstra funcionando correctamente");
                } else {
                    System.out.println("⚠️ No existe ruta directa entre los aeropuertos seleccionados");
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Error validando Dijkstra: " + e.getMessage());
        }
    }

    /**
     * Genera pasajeros aleatorios y los registra en el árbol AVL
     */
    private void generatePassengers() {
        System.out.println("\n👥 GENERANDO PASAJEROS ALEATORIOS...");

        // Intentar cargar pasajeros desde JSON
        AVLTree passengers = DataLoader.loadPassengersFromJson("src/main/resources/data/passengers.json");

        // Asignar los pasajeros cargados al FlightController
        flightController.setPassengersTree(passengers);

        // Si no hay suficientes pasajeros, generar más
        if (passengers.size() < 500) {
            int needed = 500 - passengers.size(); // Calcular cuántos faltan
            flightController.generateRandomPassengers(needed);
        }

        System.out.println("✅ Pasajeros disponibles: " + flightController.getPassengersTree().size());
    }

    /**
     * Crea vuelos aleatorios desde aeropuertos con más conexiones
     */
    private void generateFlights() {
        System.out.println("\n✈️ CREANDO VUELOS ALEATORIOS...");

        flightController.generateRandomFlights(20);
        flightController.assignRandomPassengersToFlights();

        System.out.println("✅ Vuelos creados y pasajeros asignados");
    }

    /**
     * Muestra la red de aeropuertos por consola
     */
    private void showAirportNetwork() {
        System.out.println("\n🌐 RED DE AEROPUERTOS (SIMULACIÓN)");
        System.out.println("==================================");

        flightController.showAirportNetwork();
        flightController.showFlightStatistics();
    }

    /**
     * Muestra menú interactivo para pruebas adicionales
     */
    private void showInteractiveMenu() {
        System.out.println("\n🎮 MENÚ INTERACTIVO DE PRUEBAS");
        System.out.println("==============================");

        boolean running = true;

        while (running) {
            System.out.println("\nSeleccione una opción:");
            System.out.println("1. Mostrar estadísticas de vuelos");
            System.out.println("2. Calcular ruta más corta entre aeropuertos");
            System.out.println("3. Mostrar aeropuertos con más conexiones");
            System.out.println("4. Buscar pasajero por ID");
            System.out.println("5. Generar más vuelos");
            System.out.println("6. Mostrar red de aeropuertos");
            System.out.println("0. Salir");
            System.out.print("➤ Opción: ");

            try {
                int option = scanner.nextInt();
                scanner.nextLine(); // Consumir salto de línea

                switch (option) {
                    case 1:
                        flightController.showFlightStatistics();
                        break;
                    case 2:
                        calculateCustomRoute();
                        break;
                    case 3:
                        showTopAirports();
                        break;
                    case 4:
                        searchPassenger();
                        break;
                    case 5:
                        generateAdditionalFlights();
                        break;
                    case 6:
                        flightController.showAirportNetwork();
                        break;
                    case 0:
                        running = false;
                        break;
                    default:
                        System.out.println("⚠️ Opción no válida");
                }
            } catch (Exception e) {
                System.err.println("❌ Error: " + e.getMessage());
                scanner.nextLine(); // Limpiar buffer
            }
        }
    }

    /**
     * Calcula ruta personalizada entre dos aeropuertos
     */
    private void calculateCustomRoute() {
        System.out.println("\n🧮 CÁLCULO DE RUTA PERSONALIZADA");
        System.out.print("Ingrese código del aeropuerto origen: ");
        int origin = scanner.nextInt();
        System.out.print("Ingrese código del aeropuerto destino: ");
        int destination = scanner.nextInt();

        flightController.validateShortestRoute(origin, destination);
    }

    /**
     * Muestra aeropuertos con más conexiones
     */
    private void showTopAirports() {
        System.out.println("\n🏆 AEROPUERTOS CON MÁS CONEXIONES");
        var topAirports = flightController.getTopConnectedAirports();
        for (int i = 0; i < topAirports.size(); i++) {
            Airport airport = topAirports.get(i);
            System.out.println((i + 1) + ". " + airport.getName() + " (" + airport.getCode() + ")");
        }
    }

    /**
     * Busca un pasajero por ID
     */
    private void searchPassenger() {
        System.out.println("\n🔍 BÚSQUEDA DE PASAJERO");
        System.out.print("Ingrese ID del pasajero: ");
        int passengerId = scanner.nextInt();

        var passenger = flightController.getPassengersTree().search(passengerId);
        if (passenger != null) {
            System.out.println("✅ Pasajero encontrado: " + passenger);
        } else {
            System.out.println("❌ Pasajero no encontrado");
        }
    }

    /**
     * Genera vuelos adicionales
     */
    private void generateAdditionalFlights() {
        System.out.println("\n✈️ GENERACIÓN DE VUELOS ADICIONALES");
        System.out.print("¿Cuántos vuelos desea generar? ");
        int numFlights = scanner.nextInt();

        flightController.generateRandomFlights(numFlights);
        flightController.assignRandomPassengersToFlights();
        System.out.println("✅ Vuelos adicionales generados");
    }

    /**
     * Método principal para ejecutar la simulación
     */
    public static void main(String[] args) {
        SimulationRunner simulation = new SimulationRunner();
        simulation.runSimulation();
    }
}