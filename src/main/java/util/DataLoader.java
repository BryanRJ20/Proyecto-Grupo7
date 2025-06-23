package util;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import domain.Airport;
import domain.Flight;
import domain.Passenger;
import domain.Status;
import domain.list.CircularDoublyLinkedList;
import domain.list.DoublyLinkedList;
import domain.tree.AVLTree;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DataLoader {
    private static final Random random = new Random();

    /**
     * Carga aeropuertos desde archivo JSON
     */
    public static DoublyLinkedList loadAirportsFromJson(String filePath) {
        DoublyLinkedList airportsList = new DoublyLinkedList();

        try (FileReader reader = new FileReader(filePath)) {
            Type listType = new TypeToken<List<Airport>>(){}.getType();
            Gson gson = new Gson();
            List<Airport> airports = gson.fromJson(reader, listType);

            if (airports != null) {
                for (Airport airport : airports) {
                    airportsList.add(airport);
                }
                System.out.println("✅ Loaded " + airports.size() + " airports from " + filePath);
            }
        } catch (Exception e) {
            System.err.println("❌ Error loading airports from JSON: " + e.getMessage());
            createDefaultAirports(airportsList);
            saveAirportsToJson(airportsList, filePath);
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
            Gson gson = new Gson();
            List<Passenger> passengers = gson.fromJson(reader, listType);

            if (passengers != null) {
                for (Passenger passenger : passengers) {
                    passengersTree.insert(passenger);
                }
                System.out.println("✅ Loaded " + passengers.size() + " passengers from " + filePath);
            }
        } catch (Exception e) {
            System.err.println("❌ Error loading passengers from JSON: " + e.getMessage());
            generateDefaultPassengers(passengersTree);
            savePassengersToJson(passengersTree, filePath);
        }

        return passengersTree;
    }

    /**
     * Carga vuelos desde archivo JSON
     */
    public static CircularDoublyLinkedList loadFlightsFromJson(String filePath) {
        CircularDoublyLinkedList flightsList = new CircularDoublyLinkedList();

        try (FileReader reader = new FileReader(filePath)) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .create();

            Type listType = new TypeToken<List<Flight>>(){}.getType();
            List<Flight> flights = gson.fromJson(reader, listType);

            if (flights != null) {
                for (Flight flight : flights) {
                    flightsList.add(flight);
                }
                System.out.println("✅ Loaded " + flights.size() + " flights from " + filePath);
            }
        } catch (Exception e) {
            System.err.println("❌ Error loading flights from JSON: " + e.getMessage());
            generateDefaultFlights(flightsList, filePath);
        }

        return flightsList;
    }

    /**
     * Guarda aeropuertos en archivo JSON
     */
    public static void saveAirportsToJson(DoublyLinkedList airportsList, String filePath) {
        try {
            List<Airport> airports = new ArrayList<>();

            if (!airportsList.isEmpty()) {
                for (int i = 1; i <= airportsList.size(); i++) {
                    Airport airport = (Airport) airportsList.getNode(i).getData();
                    airports.add(airport);
                }
            }

            File file = new File(filePath);
            file.getParentFile().mkdirs();

            try (FileWriter writer = new FileWriter(file)) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
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
            List<Passenger> passengers = new ArrayList<>();
            extractPassengersFromTree(passengersTree.getRoot(), passengers);

            File file = new File(filePath);
            file.getParentFile().mkdirs();

            try (FileWriter writer = new FileWriter(file)) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                gson.toJson(passengers, writer);
                System.out.println("✅ Saved " + passengers.size() + " passengers to " + filePath);
            }
        } catch (Exception e) {
            System.err.println("❌ Error saving passengers to JSON: " + e.getMessage());
        }
    }

    /**
     * Extrae pasajeros del árbol AVL mediante recorrido inorder
     */
    private static void extractPassengersFromTree(domain.tree.AVLNode node, List<Passenger> passengers) {
        if (node != null) {
            extractPassengersFromTree(node.left, passengers);
            passengers.add(node.data);
            extractPassengersFromTree(node.right, passengers);
        }
    }

    /**
     * Crea aeropuertos por defecto
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
            int id = 10020 + i;
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
     * Genera vuelos por defecto
     */
    private static void generateDefaultFlights(CircularDoublyLinkedList flightsList, String filePath) {
        List<Flight> flights = new ArrayList<>();

        String[] origins = {"Juan Santamaría", "JFK", "Heathrow", "Charles de Gaulle", "Haneda"};
        String[] destinations = {"Toronto Pearson", "Madrid Barajas", "Frankfurt", "Dubai", "Sydney"};

        for (int i = 0; i < 30; i++) {
            int flightNumber = 1100 + i;
            String origin = origins[random.nextInt(origins.length)];
            String destination = destinations[random.nextInt(destinations.length)];

            LocalDateTime departureTime = LocalDateTime.now()
                    .plusDays(random.nextInt(30))
                    .plusHours(random.nextInt(24));

            int capacity = 150 + random.nextInt(100);
            int occupancy = random.nextInt(capacity);

            Flight flight = new Flight(flightNumber, origin, destination, departureTime, capacity);
            flight.setOccupancy(occupancy);
            flights.add(flight);
            flightsList.add(flight);
        }

        // Guardar en archivo
        try {
            File file = new File(filePath);
            file.getParentFile().mkdirs();

            try (FileWriter writer = new FileWriter(file)) {
                Gson gson = new GsonBuilder()
                        .setPrettyPrinting()
                        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                        .create();
                gson.toJson(flights, writer);
                System.out.println("✅ Generated and saved " + flights.size() + " flights to " + filePath);
            }
        } catch (IOException e) {
            System.err.println("❌ Error saving flights to JSON: " + e.getMessage());
        }
    }

    /**
     * Adaptador para serializar/deserializar LocalDateTime
     */
    private static class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
        @Override
        public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }

        @Override
        public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
    }
}