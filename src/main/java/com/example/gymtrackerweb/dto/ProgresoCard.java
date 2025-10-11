package com.example.gymtrackerweb.dto;

import java.time.LocalDate;

public class ProgresoCard {
    private final int idEjercicio;
    private final String ejercicio;
    private final LocalDate fechaUlt;
    private final double ultimo; // kg
    private final double difKg;  // puede ser negativo

    public ProgresoCard(int idEjercicio, String ejercicio, LocalDate fechaUlt, double ultimo, double difKg) {
        this.idEjercicio = idEjercicio;
        this.ejercicio = ejercicio;
        this.fechaUlt = fechaUlt;
        this.ultimo = ultimo;
        this.difKg = difKg;
    }
    public int getIdEjercicio() { return idEjercicio; }
    public String getEjercicio() { return ejercicio; }
    public LocalDate getFechaUlt() { return fechaUlt; }
    public double getUltimo() { return ultimo; }
    public double getDifKg() { return difKg; }
    public boolean isPositivo() { return difKg >= 0; }
}
