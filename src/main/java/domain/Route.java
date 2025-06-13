package domain;

import domain.list.SinglyLinkedList;

public class Route {
    private int originAirport;
    private SinglyLinkedList destinationList; // Lista de destinos con distancias

    public Route(int originAirport) {
        this.originAirport = originAirport;
        this.destinationList = new SinglyLinkedList();
    }

    public Route() {
        this.destinationList = new SinglyLinkedList();
    }

    // Getters y Setters
    public int getOriginAirport() {
        return originAirport;
    }

    public void setOriginAirport(int originAirport) {
        this.originAirport = originAirport;
    }

    public SinglyLinkedList getDestinationList() {
        return destinationList;
    }

    public void setDestinationList(SinglyLinkedList destinationList) {
        this.destinationList = destinationList;
    }

    // Método para agregar destino con distancia
    public void addDestination(RouteDestination destination) {
        destinationList.add(destination);
    }

    // Método para agregar destino con parámetros
    public void addDestination(int destinationAirport, double distance) {
        RouteDestination destination = new RouteDestination(destinationAirport, distance);
        destinationList.add(destination);
    }

    // Verificar si existe ruta a un destino específico
    public boolean hasRouteToDestination(int destinationAirport) {
        try {
            for (int i = 1; i <= destinationList.size(); i++) {
                RouteDestination dest = (RouteDestination) destinationList.getNode(i).getData();
                if (dest.getDestinationAirport() == destinationAirport) {
                    return true;
                }
            }
        } catch (Exception e) {
            // Lista vacía o error
        }
        return false;
    }

    // Obtener distancia a un destino específico
    public double getDistanceToDestination(int destinationAirport) {
        try {
            for (int i = 1; i <= destinationList.size(); i++) {
                RouteDestination dest = (RouteDestination) destinationList.getNode(i).getData();
                if (dest.getDestinationAirport() == destinationAirport) {
                    return dest.getDistance();
                }
            }
        } catch (Exception e) {
            // Lista vacía o error
        }
        return -1; // No existe ruta
    }

    // Obtener número de destinos
    public int getDestinationCount() {
        try {
            return destinationList.size();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Route from Airport ").append(originAirport).append(":\n");

        try {
            if (destinationList.isEmpty()) {
                sb.append("  No destinations");
            } else {
                for (int i = 1; i <= destinationList.size(); i++) {
                    RouteDestination dest = (RouteDestination) destinationList.getNode(i).getData();
                    sb.append("  → Airport ").append(dest.getDestinationAirport())
                            .append(" (").append(dest.getDistance()).append(" km)\n");
                }
            }
        } catch (Exception e) {
            sb.append("  Error reading destinations");
        }

        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Route route = (Route) obj;
        return originAirport == route.originAirport;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(originAirport);
    }

    // Clase interna para representar un destino con distancia
    public static class RouteDestination {
        private int destinationAirport;
        private double distance;

        public RouteDestination(int destinationAirport, double distance) {
            this.destinationAirport = destinationAirport;
            this.distance = distance;
        }

        public int getDestinationAirport() {
            return destinationAirport;
        }

        public void setDestinationAirport(int destinationAirport) {
            this.destinationAirport = destinationAirport;
        }

        public double getDistance() {
            return distance;
        }

        public void setDistance(double distance) {
            this.distance = distance;
        }

        @Override
        public String toString() {
            return "Destination: Airport " + destinationAirport + " (" + distance + " km)";
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            RouteDestination that = (RouteDestination) obj;
            return destinationAirport == that.destinationAirport;
        }

        @Override
        public int hashCode() {
            return Integer.hashCode(destinationAirport);
        }
    }
}