package com.example.gymtrackerweb.dto;


public class GrupoMuscularDTO {

    private int id;
    private String nombre;

    public GrupoMuscularDTO() {
    }

    // Constructor con campos
    public GrupoMuscularDTO(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return "GrupoMuscular{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                '}';
    }
}