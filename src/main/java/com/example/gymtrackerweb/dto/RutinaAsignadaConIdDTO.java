package com.example.gymtrackerweb.dto;
import java.time.LocalDate;
import java.sql.Date;

public class RutinaAsignadaConIdDTO {
    private int asignacionId;
    private int rutinaId;
    private String rutinaNombre;
    private LocalDate fechaAsignacion;
    private String estadoRutina;

    public RutinaAsignadaConIdDTO(int asignacionId, int rutinaId, String n, java.sql.Date f, String e) {
        this.asignacionId = asignacionId;
        this.rutinaId = rutinaId;
        this.rutinaNombre = n;
        this.fechaAsignacion = (f != null) ? f.toLocalDate() : null;
        this.estadoRutina = e;
    }

    public int getAsignacionId() { return asignacionId; }
    public int getRutinaId() { return rutinaId; }
    public String getRutinaNombre() { return rutinaNombre; }
    public LocalDate getFechaAsignacion() { return fechaAsignacion; }
    public String getEstadoRutina() { return estadoRutina; }

    public void setAsignacionId(int asignacionId) {
        this.asignacionId = asignacionId;
    }
    public void setRutinaId(int rutinaId) {
        this.rutinaId = rutinaId;
    }
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