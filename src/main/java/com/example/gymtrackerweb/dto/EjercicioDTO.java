package com.example.gymtrackerweb.dto;

public class EjercicioDTO {
    private int id;
    private String nombre;
    private String dificultad;
    private int grupoMuscularId;
    private String grupoMuscularNombre;

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

    public String getDificultad() {
        return dificultad;
    }

    public void setDificultad(String dificultad) {
        this.dificultad = dificultad;
    }

    public int getGrupoMuscularId() {
        return grupoMuscularId;
    }

    public void setGrupoMuscularId(int grupoMuscularId) {
        this.grupoMuscularId = grupoMuscularId;
    }

    public String getGrupoMuscularNombre() {
        return grupoMuscularNombre;
    }

    public void setGrupoMuscularNombre(String grupoMuscularNombre) {
        this.grupoMuscularNombre = grupoMuscularNombre;
    }
}
