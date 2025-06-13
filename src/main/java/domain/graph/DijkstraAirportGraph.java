package domain.graph;

import domain.*;
import domain.tree.*;
import domain.AdjacencyListGraph;
import domain.GraphException;
import domain.list.ListException;
import domain.queue.QueueException;
import domain.stack.StackException;
import java.util.*;

public class DijkstraAirportGraph extends AdjacencyListGraph {

    public DijkstraAirportGraph(int n) {
        super(n);
    }

    /**
     * Implementación del algoritmo de Dijkstra para encontrar la ruta más corta
     */
    public DijkstraResult dijkstra(Object source, Object destination) throws GraphException, ListException {
        if (isEmpty()) {
            throw new GraphException("Graph is empty");
        }

        int sourceIndex = indexOf(source);
        int destIndex = indexOf(destination);

        if (sourceIndex == -1 || destIndex == -1) {
            throw new GraphException("Source or destination vertex not found");
        }

        // Inicialización
        double[] distances = new double[counter];
        boolean[] visited = new boolean[counter];
        int[] previous = new int[counter];

        Arrays.fill(distances, Double.POSITIVE_INFINITY);
        Arrays.fill(previous, -1);
        distances[sourceIndex] = 0;

        // Algoritmo de Dijkstra
        for (int i = 0; i < counter; i++) {
            int minIndex = getMinDistanceIndex(distances, visited);
            if (minIndex == -1) break;

            visited[minIndex] = true;

            // Actualizar distancias de vértices adyacentes
            updateAdjacentDistances(minIndex, distances, visited, previous);
        }

        // Construir el resultado
        return buildDijkstraResult(sourceIndex, destIndex, distances, previous);
    }

    private int getMinDistanceIndex(double[] distances, boolean[] visited) {
        double minDistance = Double.POSITIVE_INFINITY;
        int minIndex = -1;

        for (int i = 0; i < counter; i++) {
            if (!visited[i] && distances[i] < minDistance) {
                minDistance = distances[i];
                minIndex = i;
            }
        }

        return minIndex;
    }

    private void updateAdjacentDistances(int currentIndex, double[] distances, boolean[] visited, int[] previous) {
        try {
            if (!vertexList[currentIndex].edgesList.isEmpty()) {
                for (int i = 1; i <= vertexList[currentIndex].edgesList.size(); i++) {
                    EdgeWeight edge = (EdgeWeight) vertexList[currentIndex].edgesList.getNode(i).getData();
                    int adjacentIndex = indexOf(edge.getEdge());

                    if (adjacentIndex != -1 && !visited[adjacentIndex]) {
                        double weight = getEdgeWeight(edge);
                        double newDistance = distances[currentIndex] + weight;

                        if (newDistance < distances[adjacentIndex]) {
                            distances[adjacentIndex] = newDistance;
                            previous[adjacentIndex] = currentIndex;
                        }
                    }
                }
            }
        } catch (ListException e) {
            System.err.println("Error updating adjacent distances: " + e.getMessage());
        }
    }

    private double getEdgeWeight(EdgeWeight edge) {
        if (edge.getWeight() == null) {
            return 1.0; // Peso por defecto
        }

        if (edge.getWeight() instanceof Number) {
            return ((Number) edge.getWeight()).doubleValue();
        }

        try {
            return Double.parseDouble(edge.getWeight().toString());
        } catch (NumberFormatException e) {
            return 1.0; // Peso por defecto si no se puede parsear
        }
    }

    private DijkstraResult buildDijkstraResult(int sourceIndex, int destIndex, double[] distances, int[] previous) {
        DijkstraResult result = new DijkstraResult();
        result.setDistance(distances[destIndex]);

        if (distances[destIndex] == Double.POSITIVE_INFINITY) {
            result.setPathExists(false);
            result.setPath(new ArrayList<>());
            return result;
        }

        // Reconstruir el camino
        List<Object> path = new ArrayList<>();
        int current = destIndex;

        while (current != -1) {
            path.add(0, vertexList[current].data); // Insertar al inicio
            current = previous[current];
        }

        result.setPathExists(true);
        result.setPath(path);
        return result;
    }


    /**
     * Genera rutas aleatorias entre aeropuertos
     */
    public void generateRandomRoutes() throws GraphException, ListException {
        if (counter < 2) return;

        Random random = new Random();

        // Para cada aeropuerto, crear conexiones aleatorias
        for (int i = 0; i < counter; i++) {
            int numConnections = random.nextInt(Math.min(5, counter - 1)) + 1; // 1-5 conexiones
            Set<Integer> connected = new HashSet<>();

            for (int j = 0; j < numConnections; j++) {
                int targetIndex;
                do {
                    targetIndex = random.nextInt(counter);
                } while (targetIndex == i || connected.contains(targetIndex));

                connected.add(targetIndex);

                // Generar distancia aleatoria entre 100 y 2000 km
                double distance = 100 + random.nextDouble() * 1900;

                Object source = vertexList[i].data;
                Object destination = vertexList[targetIndex].data;

                // Agregar arista si no existe
                if (!containsEdge(source, destination)) {
                    addEdgeWeight(source, destination, distance);
                }
            }
        }
    }

    /**
     * Obtiene los aeropuertos con más conexiones
     */
    public List<Object> getTopConnectedAirports(int topCount) {
        List<AirportConnection> connections = new ArrayList<>();

        for (int i = 0; i < counter; i++) {
            int connectionCount = 0;
            try {
                if (!vertexList[i].edgesList.isEmpty()) {
                    connectionCount = vertexList[i].edgesList.size();
                }
            } catch (ListException e) {
                // Ignorar error y continuar
            }
            connections.add(new AirportConnection(vertexList[i].data, connectionCount));
        }

        // Ordenar por número de conexiones (descendente)
        connections.sort((a, b) -> Integer.compare(b.getConnectionCount(), a.getConnectionCount()));

        List<Object> result = new ArrayList<>();
        for (int i = 0; i < Math.min(topCount, connections.size()); i++) {
            result.add(connections.get(i).getAirport());
        }

        return result;
    }

    // Clase auxiliar para ordenar aeropuertos por conexiones
    private static class AirportConnection {
        private Object airport;
        private int connectionCount;

        public AirportConnection(Object airport, int connectionCount) {
            this.airport = airport;
            this.connectionCount = connectionCount;
        }

        public Object getAirport() { return airport; }
        public int getConnectionCount() { return connectionCount; }
    }

    /**
     * Clase para encapsular el resultado del algoritmo de Dijkstra
     */
    public static class DijkstraResult {
        private boolean pathExists;
        private double distance;
        private List<Object> path;

        public DijkstraResult() {
            this.pathExists = false;
            this.distance = Double.POSITIVE_INFINITY;
            this.path = new ArrayList<>();
        }

        // Getters y Setters
        public boolean isPathExists() { return pathExists; }
        public void setPathExists(boolean pathExists) { this.pathExists = pathExists; }

        public double getDistance() { return distance; }
        public void setDistance(double distance) { this.distance = distance; }

        public List<Object> getPath() { return path; }
        public void setPath(List<Object> path) { this.path = path; }

        public String getPathAsString() {
            if (!pathExists || path.isEmpty()) {
                return "No path exists";
            }

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < path.size(); i++) {
                sb.append(path.get(i));
                if (i < path.size() - 1) {
                    sb.append(" → ");
                }
            }
            return sb.toString();
        }

        @Override
        public String toString() {
            return String.format("Path: %s | Distance: %.2f km | Exists: %s",
                    getPathAsString(), distance, pathExists);
        }
    }
}