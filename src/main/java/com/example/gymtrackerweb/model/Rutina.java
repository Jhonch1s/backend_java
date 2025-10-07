package com.example.gymtrackerweb.model;
import com.example.gymtrackerweb.model.enums.Objetivo;

public class Rutina {
    private int id;
    private Objetivo objetivo;
    private String nombre;
    private int duracionSemanas;

    public Rutina (){

    }

    public Rutina(int id) {
        this.id = id;
    }

    public Rutina(int id, String nombre, Objetivo objetivo, int duracionSemanas) {
        this.id = id;
        this.objetivo = objetivo;
        this.nombre = nombre;
        this.duracionSemanas = duracionSemanas;
    }

    public Rutina(String nombre, Objetivo objetivo, int duracionSemanas) {
        this.objetivo = objetivo;
        this.nombre = nombre;
        this.duracionSemanas = duracionSemanas;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Objetivo getObjetivo() {
        return objetivo;
    }

    public void setObjetivo(Objetivo objetivo) {
        this.objetivo = objetivo;
    }

    public int getDuracionSemanas() {
        return duracionSemanas;
    }

    public void setDuracionSemanas(int duracionSemanas) {
        this.duracionSemanas = duracionSemanas;
    }

    public String toString() {
        return "Rutina: " + nombre + " | Objetivo " + objetivo + " | DuracionSemanas " + duracionSemanas;
    }
    public String toStringID() {
        return "ID " + id + " | Rutina " + nombre + " | Objetivo " + objetivo + " | DuracionSemanas " + duracionSemanas;
    }
}
