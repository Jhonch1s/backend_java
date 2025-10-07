package com.example.gymtrackerweb.model;

import java.sql.Date;

public class Membresia {
    private int id;
    private int idPlan;
    private String idCliente;
    private Date fechaInicio;
    private Date fechaFin;
    private int estadoId;

    public Membresia() {

    }

    public Membresia(int id, int idPlan, String idCliente, Date fechaInicio, Date fechaFin, int estadoId) {
        this.id = id;
        this.idPlan = idPlan;
        this.idCliente = idCliente;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.estadoId = estadoId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdPlan() {
        return idPlan;
    }

    public void setIdPlan(int idPlan) {
        this.idPlan = idPlan;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public int getEstadoId() {
        return estadoId;
    }

    public void setEstadoId(int estadoId) {
        this.estadoId = estadoId;
    }
}
