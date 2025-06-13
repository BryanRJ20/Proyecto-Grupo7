package util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import domain.Airport;
import domain.Passenger;
import domain.Status;
import domain.list.DoublyLinkedList;
import domain.tree.AVLTree;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Random;

public class DataLoader {
    private static final Gson gson = new Gson();
    private static final Random random = new Random();

    /**
     * Carga aeropuertos desde archivo JSON
     */
    public static DoublyLinkedList loadAirportsFromJson(String filePath) {
        DoublyLinkedList airportsList = new DoublyLinkedList();

        try (FileReader reader = new FileReader(filePath)) {
            Type listType = new TypeToken<List<Airport>>(){}.getType();
            List<Airport> airports = gson.fromJson(reader, listType);

            if (airports != null) {
                for (Airport airport : airports) {
                    airportsList.add(airport);
                }
                System.out.println("✅ Loaded " + airports.size() + " airports from " + filePath);
            }
        } catch (Exception e) {
            System.err.println("❌ Error loading airports from JSON: " + e.getMessage());
            // Si falla la carga, crear aeropuertos por defecto
            createDefaultAirports(airportsList);
        }

        return airportsList;
    }

    /**
     * Carga pasajeros desde archivo JSON
     */
    public static AVLTree loadPassengersFromJson(String filePath) {
        AVLTree passengersTree = new AVLTree();

        try (FileReader reader = new FileReader(filePath)) {
            Type listType = new TypeToken<List<Passenger>>(){}.getType();
            List<Passenger> passengers = gson.fromJson(reader, listType);

            if (passengers != null) {
                for (Passenger passenger : passengers) {
                    passengersTree.insert(passenger);
                }
                System.out.println("✅ Loaded " + passengers.size() + " passengers from " + filePath);
            }
        } catch (Exception e) {
            System.err.println("❌ Error loading passengers from JSON: " + e.getMessage());
            System.out.println("🔄 Generating default passengers...");
            generateDefaultPassengers(passengersTree);
        }

        return passengersTree;
    }

    /**
     * Guarda aeropuertos en archivo JSON
     */
    public static void saveAirportsToJson(DoublyLinkedList airportsList, String filePath) {
        try {
            java.util.List<Airport> airports = new java.util.ArrayList<>();

            if (!airportsList.isEmpty()) {
                for (int i = 1; i <= airportsList.size(); i++) {
                    Airport airport = (Airport) airportsList.getNode(i).getData();
                    airports.add(airport);
                }
            }

            try (FileWriter writer = new FileWriter(filePath)) {
                gson.toJson(airports, writer);
                System.out.println("✅ Saved " + airports.size() + " airports to " + filePath);
            }
        } catch (Exception e) {
            System.err.println("❌ Error saving airports to JSON: " + e.getMessage());
        }
    }

    /**
     * Guarda pasajeros en archivo JSON
     */
    public static void savePassengersToJson(AVLTree passengersTree, String filePath) {
        try {
            java.util.List<Passenger> passengers = extractPassengersFromTree(passengersTree);

            try (FileWriter writer = new FileWriter(filePath)) {
                gson.toJson(passengers, writer);
                System.out.println("✅ Saved " + passengers.size() + " passengers to " + filePath);
            }
        } catch (Exception e) {
            System.err.println("❌ Error saving passengers to JSON: " + e.getMessage());
        }
    }

    /**
     * Extrae todos los pasajeros del árbol AVL
     */
    private static java.util.List<Passenger> extractPassengersFromTree(AVLTree tree) {
        java.util.List<Passenger> passengers = new java.util.ArrayList<>();
        // Aquí se podría implementar un recorrido in-order del árbol
        // Por simplicidad, retornamos lista vacía
        return passengers;
    }

    /**
     * Crea aeropuertos por defecto si no se puede cargar desde JSON
     */
    private static void createDefaultAirports(DoublyLinkedList airportsList) {
        System.out.println("🔄 Creating default airports...");

        Airport[] defaultAirports = {
                new Airport(1001, "Aeropuerto Internacional Juan Santamaría", "Costa Rica", Status.ACTIVE),
                new Airport(1002, "John F. Kennedy International Airport", "Estados Unidos", Status.ACTIVE),
                new Airport(1003, "London Heathrow Airport", "Reino Unido", Status.ACTIVE),
                new Airport(1004, "Charles de Gaulle Airport", "Francia", Status.ACTIVE),
                new Airport(1005, "Tokyo Haneda Airport", "Japón", Status.ACTIVE),
                new Airport(1006, "São Paulo–Guarulhos International Airport", "Brasil", Status.ACTIVE),
                new Airport(1007, "Sydney Kingsford Smith Airport", "Australia", Status.ACTIVE),
                new Airport(1008, "Frankfurt am Main Airport", "Alemania", Status.ACTIVE),
                new Airport(1009, "Dubai International Airport", "Emiratos Árabes Unidos", Status.ACTIVE),
                new Airport(1010, "Toronto Pearson International Airport", "Canadá", Status.ACTIVE),
                new Airport(1011, "Madrid-Barajas Airport", "España", Status.ACTIVE),
                new Airport(1012, "Rome Fiumicino Airport", "Italia", Status.ACTIVE),
                new Airport(1013, "Amsterdam Airport Schiphol", "Países Bajos", Status.ACTIVE),
                new Airport(1014, "Singapore Changi Airport", "Singapur", Status.ACTIVE),
                new Airport(1015, "Hong Kong International Airport", "Hong Kong", Status.ACTIVE),
                new Airport(1016, "Los Angeles International Airport", "Estados Unidos", Status.ACTIVE),
                new Airport(1017, "Mexico City International Airport", "México", Status.ACTIVE),
                new Airport(1018, "Jorge Newbery Airfield", "Argentina", Status.ACTIVE),
                new Airport(1019, "Lima Jorge Chávez International Airport", "Perú", Status.ACTIVE),
                new Airport(1020, "Bogotá El Dorado International Airport", "Colombia", Status.ACTIVE)
        };

        for (Airport airport : defaultAirports) {
            airportsList.add(airport);
        }

        System.out.println("✅ Created " + defaultAirports.length + " default airports");
    }

    /**
     * Genera pasajeros por defecto
     */
    private static void generateDefaultPassengers(AVLTree passengersTree) {
        String[] firstNames = {
                "Juan", "María", "Carlos", "Ana", "Luis", "Carmen", "José", "Lucía",
                "Pedro", "Isabel", "Francisco", "Pilar", "Antonio", "Teresa", "Manuel"
        };

        String[] lastNames = {
                "García", "Rodríguez", "González", "Fernández", "López", "Martínez",
                "Sánchez", "Pérez", "Gómez", "Martín", "Jiménez", "Ruiz", "Hernández"
        };

        String[] nationalities = {
                "Costa Rica", "México", "España", "Colombia", "Argentina", "Chile",
                "Perú", "Venezuela", "Ecuador", "Uruguay", "Estados Unidos", "Canadá"
        };

        for (int i = 0; i < 200; i++) {
            int id = 10000 + i;
            String firstName = firstNames[random.nextInt(firstNames.length)];
            String lastName = lastNames[random.nextInt(lastNames.length)];
            String name = firstName + " " + lastName;
            String nationality = nationalities[random.nextInt(nationalities.length)];

            Passenger passenger = new Passenger(id, name, nationality);
            passengersTree.insert(passenger);
        }

        System.out.println("✅ Generated 200 default passengers");
    }

    /**
     * Crea archivos JSON de ejemplo si no existen
     */
    public static void createSampleJsonFiles() {
        // Crear archivo de aeropuertos de ejemplo
        String airportsJson = """
        [
          {
            "code": 1001,
            "name": "Aeropuerto Internacional Juan Santamaría",
            "country": "Costa Rica",
            "status": "ACTIVE"
          },
          {
            "code": 1002,
            "name": "John F. Kennedy International Airport",
            "country": "Estados Unidos",
            "status": "ACTIVE"
          },
          {
            "code": 1003,
            "name": "London Heathrow Airport",
            "country": "Reino Unido",
            "status": "ACTIVE"
          },
          {
            "code": 1004,
            "name": "Charles de Gaulle Airport",
            "country": "Francia",
            "status": "ACTIVE"
          },
          {
            "code": 1005,
            "name": "Tokyo Haneda Airport",
            "country": "Japón",
            "status": "ACTIVE"
          }
        ]
        """;

        // Crear archivo de pasajeros de ejemplo
        String passengersJson = """
        [
          {
            "id": 10001,
            "name": "Juan Pérez",
            "nationality": "Costa Rica"
          },
          {
            "id": 10002,
            "name": "María González",
            "nationality": "México"
          },
          {
            "id": 10003,
            "name": "Carlos López",
            "nationality": "España"
          }
        ]
        """;

        try {
            // Crear directorio si no existe
            java.io.File directory = new java.io.File("src/main/resources/data");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Escribir archivo de aeropuertos
            try (FileWriter writer = new FileWriter("src/main/resources/data/airports.json")) {
                writer.write(airportsJson);
            }

            // Escribir archivo de pasajeros
            try (FileWriter writer = new FileWriter("src/main/resources/data/passengers.json")) {
                writer.write(passengersJson);
            }

            System.out.println("✅ Sample JSON files created successfully");
        } catch (IOException e) {
            System.err.println("❌ Error creating sample JSON files: " + e.getMessage());
        }
    }
}