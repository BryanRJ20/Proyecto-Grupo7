package domain;

import domain.list.SinglyLinkedList;

public class Passenger {
    private int id;
    private String name;
    private String nationality;
    private SinglyLinkedList flightHistory;

    public Passenger(int id, String name, String nationality) {
        this.id = id;
        this.name = name;
        this.nationality = nationality;
        this.flightHistory = new SinglyLinkedList();
    }

    public Passenger() {
        this.flightHistory = new SinglyLinkedList();
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public SinglyLinkedList getFlightHistory() {
        return flightHistory;
    }

    public void setFlightHistory(SinglyLinkedList flightHistory) {
        this.flightHistory = flightHistory;
    }

    public void addFlightToHistory(Flight flight) {
        this.flightHistory.add(flight);
    }

    @Override
    public String toString() {
        return "Passenger{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", nationality='" + nationality + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Passenger passenger = (Passenger) obj;
        return id == passenger.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}