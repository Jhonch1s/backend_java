package com.example.gymtrackerweb.dto;

import java.time.LocalDate;

public class RutinaCard {
    private final int idRutina;
    private final String nombre;
    private final String gruposTop3;
    private final String estado;
    private final LocalDate fechaAsignacion;

    public RutinaCard(int idRutina, String nombre, String gruposTop3, String estado, LocalDate fechaAsignacion) {
        this.idRutina = idRutina;
        this.nombre = nombre;
        this.gruposTop3 = gruposTop3;
        this.estado = estado;
        this.fechaAsignacion = fechaAsignacion;
    }
    public int getIdRutina() { return idRutina; }
    public String getNombre() { return nombre; }
    public String getGruposTop3() { return gruposTop3; }
    public String getEstado() { return estado; }
    public LocalDate getFechaAsignacion() { return fechaAsignacion; }
}
