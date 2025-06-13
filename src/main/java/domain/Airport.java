package domain;

import com.google.gson.annotations.Expose;
import domain.list.SinglyLinkedList;

public class Airport {

    public int code;
    public String name;
    public String country;
    public Status status;

    @Expose
    private SinglyLinkedList departuresBoard;

    //Constructor
    public Airport(int code, String name, String country, Status status) {
        this.code = code;
        this.name = name;
        this.country = country;
        this.status = status;
        this.departuresBoard = new SinglyLinkedList(); //incializa
    }

    public Airport() {} // Constructor por defecto obligatorio para Gson


    // -- Getters y Setters --

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public SinglyLinkedList getDeparturesBoard() {
        return departuresBoard;
    }

    public void setDeparturesBoard(SinglyLinkedList departuresBoard) {
        this.departuresBoard = departuresBoard;
    }

    // -- Para activar o desactivar aeropuertos --
    public void activate() {
        this.status = Status.ACTIVE;
    }

    public void deactivate() {
        this.status = Status.INACTIVE;
    }

    public boolean isActive() {
        return this.status == Status.ACTIVE;
    }

}//END CLASS
