package domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Flight {
    private int number;
    private String origin;
    private String destination;
    private LocalDateTime departureTime;
    private int capacity;
    private int occupancy;
    private String status; // "SCHEDULED", "IN_FLIGHT", "COMPLETED", "CANCELLED"

    public Flight(int number, String origin, String destination, LocalDateTime departureTime, int capacity) {
        this.number = number;
        this.origin = origin;
        this.destination = destination;
        this.departureTime = departureTime;
        this.capacity = capacity;
        this.occupancy = 0;
        this.status = "SCHEDULED";
    }

    public Flight() {
        this.occupancy = 0;
        this.status = "SCHEDULED";
    }

    // Getters y Setters
    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getOccupancy() {
        return occupancy;
    }

    public void setOccupancy(int occupancy) {
        this.occupancy = occupancy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Métodos de negocio
    public boolean hasAvailableSeats() {
        return occupancy < capacity;
    }

    public int getAvailableSeats() {
        return capacity - occupancy;
    }

    public boolean addPassenger() {
        if (hasAvailableSeats()) {
            occupancy++;
            return true;
        }
        return false;
    }

    public boolean removePassenger() {
        if (occupancy > 0) {
            occupancy--;
            return true;
        }
        return false;
    }

    public double getOccupancyPercentage() {
        if (capacity == 0) return 0.0;
        return (double) occupancy / capacity * 100.0;
    }

    public String getFormattedDepartureTime() {
        if (departureTime == null) return "Not set";
        return departureTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    @Override
    public String toString() {
        return String.format("Flight %d: %s → %s | %s | %d/%d passengers (%.1f%%) | Status: %s",
                number, origin, destination, getFormattedDepartureTime(),
                occupancy, capacity, getOccupancyPercentage(), status);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Flight flight = (Flight) obj;
        return number == flight.number;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(number);
    }
}