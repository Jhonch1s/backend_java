package com.example.gymtrackerweb.dto;

public class EjercicioAsignadoDTO {
    private EjercicioDTO ejercicio; // Reutilizamos el DTO anterior
    private int series;
    private int repeticiones;
    private String diaSemana;

    public EjercicioDTO getEjercicio() {
        return ejercicio;
    }

    public void setEjercicio(EjercicioDTO ejercicio) {
        this.ejercicio = ejercicio;
    }

    public int getSeries() {
        return series;
    }

    public void setSeries(int series) {
        this.series = series;
    }

    public String getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(String diaSemana) {
        this.diaSemana = diaSemana;
    }

    public int getRepeticiones() {
        return repeticiones;
    }

    public void setRepeticiones(int repeticiones) {
        this.repeticiones = repeticiones;
    }
}
