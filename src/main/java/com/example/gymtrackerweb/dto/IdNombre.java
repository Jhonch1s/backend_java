package com.example.gymtrackerweb.dto;

public class IdNombre {
    private int id;
    private String nombre;

    public IdNombre(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }
    public int getId() { return id; }
    public String getNombre() { return nombre; }

    @Override public String toString() { return id + " - " + nombre; }
}
