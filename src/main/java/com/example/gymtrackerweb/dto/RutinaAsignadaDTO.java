package com.example.gymtrackerweb.dto;

import java.time.LocalDate;

// DTO para UNA rutina asignada (con su fecha)
public class RutinaAsignadaDTO {
    private String rutinaNombre;
    private LocalDate fechaAsignacion;
    private String estadoRutina;

    public RutinaAsignadaDTO(String rutinaNombre, java.sql.Date fechaAsignacion, String estadoRutina) {
        this.rutinaNombre = rutinaNombre;
        this.fechaAsignacion = (fechaAsignacion != null) ? fechaAsignacion.toLocalDate() : null;
        this.estadoRutina = estadoRutina;
    }

    // Getters
    public String getRutinaNombre() { return rutinaNombre; }
    public LocalDate getFechaAsignacion() { return fechaAsignacion; }
    public String getEstadoRutina() { return estadoRutina; }

    public void setRutinaNombre(String rutinaNombre) {
        this.rutinaNombre = rutinaNombre;
    }

    public void setFechaAsignacion(LocalDate fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }

    public void setEstadoRutina(String estadoRutina) {
        this.estadoRutina = estadoRutina;
    }
}

