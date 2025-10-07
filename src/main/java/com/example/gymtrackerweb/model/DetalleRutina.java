package com.example.gymtrackerweb.model;

public class DetalleRutina {
    private int id;
    private int id_ejercicio;
    private int id_rutina;
    private int series;
    private int repeticiones;

    public DetalleRutina(){

    }

    public DetalleRutina(int id, int id_ejercicio, int id_rutina, int series, int repeticiones){
        this.id = id;
        this.id_ejercicio = id_ejercicio;
        this.id_rutina = id_rutina;
        this.series = series;
        this.repeticiones = repeticiones;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_ejercicio() {
        return id_ejercicio;
    }

    public void setId_ejercicio(int id_ejercicio) {
        this.id_ejercicio = id_ejercicio;
    }

    public int getId_rutina() {
        return id_rutina;
    }

    public void setId_rutina(int id_rutina) {
        this.id_rutina = id_rutina;
    }

    public int getSeries() {
        return series;
    }

    public void setSeries(int series) {
        this.series = series;
    }

    public int getRepeticiones() {
        return repeticiones;
    }

    public void setRepeticiones(int repeticiones) {
        this.repeticiones = repeticiones;
    }
}
