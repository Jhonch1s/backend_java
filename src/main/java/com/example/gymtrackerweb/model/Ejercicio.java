package com.example.gymtrackerweb.model;


import com.example.gymtrackerweb.model.enums.Dificultad;

public class Ejercicio {
    private int id;
    private String nombre;
    private Dificultad dificultad;
    private int grupoMuscular;
    private String url;

    public Ejercicio() {
    }

    public Ejercicio(int id){
        this.id = id;
    }

    public Ejercicio(String nombre, int grupoMuscular, Dificultad dificultad, String url) {
        this.nombre = nombre;
        this.dificultad = dificultad;
        this.grupoMuscular = grupoMuscular;
        this.url = url;
    }
    public Ejercicio(int id, String nombre, int grupoMuscular, Dificultad dificultad, String url) {
        this.id = id;
        this.nombre = nombre;
        this.dificultad = dificultad;
        this.grupoMuscular = grupoMuscular;
        this.url = url;
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

    public int getGrupoMuscular() {
        return grupoMuscular;
    }

    public void setGrupoMuscular(int grupoMuscular) {
        this.grupoMuscular = grupoMuscular;
    }

    public Dificultad getDificultad() {
        return dificultad;
    }

    public void setDificultad(Dificultad dificultad) {
        this.dificultad = dificultad;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String toString() {
        return "ID: " + id + " | Ejercicio: " + nombre + " | Grupo Muscular ID: " + grupoMuscular + " | Dificultad: " + dificultad;
    }
}

