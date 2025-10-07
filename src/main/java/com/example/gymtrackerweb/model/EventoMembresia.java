package com.example.gymtrackerweb.model;

import java.sql.Timestamp;

public class EventoMembresia {
    private int id;
    private int idStaff;
    private int idMembresia;
    private int tipoEventoId;
    private Timestamp fechaEvento;
    private String observaciones;

    public EventoMembresia() {

    }

    public EventoMembresia(int id, int idStaff, int idMembresia, int tipoEventoId, Timestamp fechaEvento, String observaciones) {
        this.id = id;
        this.idStaff = idStaff;
        this.idMembresia = idMembresia;
        this.tipoEventoId = tipoEventoId;
        this.fechaEvento = fechaEvento;
        this.observaciones = observaciones;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdStaff() {
        return idStaff;
    }

    public void setIdStaff(int idStaff) {
        this.idStaff = idStaff;
    }

    public int getIdMembresia() {
        return idMembresia;
    }

    public void setIdMembresia(int idMembresia) {
        this.idMembresia = idMembresia;
    }

    public int getTipoEventoId() {
        return tipoEventoId;
    }

    public void setTipoEventoId(int tipoEventoId) {
        this.tipoEventoId = tipoEventoId;
    }

    public Timestamp getFechaEvento() {
        return fechaEvento;
    }

    public void setFechaEvento(Timestamp fechaEvento) {
        this.fechaEvento = fechaEvento;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
