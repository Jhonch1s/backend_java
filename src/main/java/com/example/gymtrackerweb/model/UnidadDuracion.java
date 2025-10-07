package com.example.gymtrackerweb.model;

public class UnidadDuracion {
    private byte id;
    private String nombre;
    private String abreviatura;// Abreviatura (ej: "d", "mes")

    public UnidadDuracion() {
    }
    public UnidadDuracion(byte id, String nombre, String abreviatura) {
        this.id = id;
        this.nombre = nombre;
        this.abreviatura = abreviatura;
    }

    // --- Getters y Setters ---
    public byte getId() {
        return id;
    }

    public void setId(byte id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    // --- toString ---
    @Override
    public String toString() {
        return String.format("UnidadDuracion{id=%d, nombre='%s'}",
                id, nombre, abreviatura);
    }
}
