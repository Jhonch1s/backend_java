package com.example.gymtrackerweb.model;

import java.sql.Date;

public class ProgresoEjercicio {
    private int id;
    private int idCliente;
    private int idEjercicio;
    private Date fecha;
    private int pesoUsado;
    private int repeticiones;

    public ProgresoEjercicio() {

    }

    public ProgresoEjercicio(int id, int idCliente, int idEjercicio, Date fecha, int pesoUsado, int repeticiones) {
        this.id = id;
        this.idCliente = idCliente;
        this.idEjercicio = idEjercicio;
        this.fecha = fecha;
        this.pesoUsado = pesoUsado;
        this.repeticiones = repeticiones;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public int getIdEjercicio() {
        return idEjercicio;
    }

    public void setIdEjercicio(int idEjercicio) {
        this.idEjercicio = idEjercicio;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public int getPesoUsado() {
        return pesoUsado;
    }

    public void setPesoUsado(int pesoUsado) {
        this.pesoUsado = pesoUsado;
    }

    public int getRepeticiones() {
        return repeticiones;
    }

    public void setRepeticiones(int repeticiones) {
        this.repeticiones = repeticiones;
    }
}


