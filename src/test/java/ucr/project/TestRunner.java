package test;

import domain.*;
import domain.graph.DijkstraAirportGraph;
import domain.tree.AVLTree;
import domain.list.DoublyLinkedList;
import controller.FlightController;

/**
 * Clase para probar componentes individuales rápidamente
 */
public class TestRunner {

    public static void main(String[] args) {
        System.out.println("🧪 EJECUTANDO PRUEBAS RÁPIDAS");
        System.out.println("=============================");

        // Prueba 1: Árbol AVL
        testAVLTree();

        // Prueba 2: Grafo con Dijkstra
        testDijkstraGraph();

        // Prueba 3: FlightController
        testFlightController();

        System.out.println("\n✅ TODAS LAS PRUEBAS COMPLETADAS");
    }

    private static void testAVLTree() {
        System.out.println("\n🌳 PRUEBA: Árbol AVL de Pasajeros");

        AVLTree tree = new AVLTree();

        // Insertar pasajeros
        tree.insert(new Passenger(1001, "Juan Pérez", "Costa Rica"));
        tree.insert(new Passenger(1003, "María González", "México"));
        tree.insert(new Passenger(1002, "Carlos López", "España"));

        System.out.println("✅ Insertados 3 pasajeros");
        System.out.println("📊 Tamaño del árbol: " + tree.size());

        // Buscar pasajero
        Passenger found = tree.search(1002);
        if (found != null) {
            System.out.println("✅ Búsqueda exitosa: " + found.getName());
        } else {
            System.out.println("❌ Pasajero no encontrado");
        }
    }

    private static void testDijkstraGraph() {
        System.out.println("\n🗺️ PRUEBA: Grafo con Dijkstra");

        try {
            DijkstraAirportGraph graph = new DijkstraAirportGraph(5);

            // Agregar aeropuertos
            graph.addVertex(1001);
            graph.addVertex(1002);
            graph.addVertex(1003);

            System.out.println("✅ Agregados 3 aeropuertos al grafo");

            // Agregar rutas con pesos
            graph.addEdgeWeight(1001, 1002, 1500.0); // 1500 km
            graph.addEdgeWeight(1002, 1003, 800.0);  // 800 km
            graph.addEdgeWeight(1001, 1003, 2000.0); // 2000 km

            System.out.println("✅ Agregadas rutas con distancias");

            // Probar Dijkstra
            var result = graph.dijkstra(1001, 1003);
            System.out.println("📊 Ruta más corta: " + result);

            if (result.isPathExists()) {
                System.out.println("✅ Dijkstra funcionando correctamente");
            } else {
                System.out.println("❌ Error en Dijkstra");
            }

        } catch (Exception e) {
            System.out.println("❌ Error en prueba de grafo: " + e.getMessage());
        }
    }

    private static void testFlightController() {
        System.out.println("\n✈️ PRUEBA: FlightController");

        try {
            FlightController controller = new FlightController();

            // Crear lista de aeropuertos
            DoublyLinkedList airports = new DoublyLinkedList();
            airports.add(new Airport(1001, "Aeropuerto A", "Costa Rica", Status.ACTIVE));
            airports.add(new Airport(1002, "Aeropuerto B", "México", Status.ACTIVE));
            airports.add(new Airport(1003, "Aeropuerto C", "España", Status.ACTIVE));

            controller.setAirportsList(airports);
            System.out.println("✅ Aeropuertos configurados en FlightController");

            // Generar pasajeros
            controller.generateRandomPassengers(50);
            System.out.println("✅ Generados 50 pasajeros aleatorios");

            // Generar rutas
            controller.generateRandomRoutes();
            System.out.println("✅ Rutas aleatorias generadas");

            // Generar vuelos
            controller.generateRandomFlights(5);
            System.out.println("✅ Generados 5 vuelos aleatorios");

            // Mostrar estadísticas
            controller.showFlightStatistics();

        } catch (Exception e) {
            System.out.println("❌ Error en prueba de FlightController: " + e.getMessage());
        }
    }
}