package com.example.gymtrackerweb.model;

import java.sql.Date;

public class RutinaCliente {

    private int id;
    private String idCliente;
    private int idRutina;
    private Date fechaAsignacion;
    private String estado;

    public RutinaCliente(int id, String idCliente, int idRutina, Date fechaAsignacion, String estado) {
        this.id = id;
        this.idCliente = idCliente;
        this.idRutina = idRutina;
        this.fechaAsignacion = fechaAsignacion;
        this.estado = estado;
    }

    public RutinaCliente(String idCliente, int idRutina, Date fechaAsignacion, String estado) {
        this.idCliente = idCliente;
        this.idRutina = idRutina;
        this.fechaAsignacion = fechaAsignacion;
        this.estado = estado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public int getIdRutina() {
        return idRutina;
    }

    public void setIdRutina(int idRutina) {
        this.idRutina = idRutina;
    }

    public Date getFechaAsignacion() {
        return fechaAsignacion;
    }

    public void setFechaAsignacion(Date fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

}
