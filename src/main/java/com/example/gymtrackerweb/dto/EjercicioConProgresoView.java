package com.example.gymtrackerweb.dto;

import java.math.BigDecimal;
import java.sql.Date;

public class EjercicioConProgresoView {
    private String nombreRutina;
    private int idEjercicio;
    private String nombreEjercicio;
    private BigDecimal pesoUsado;
    private int repeticiones;
    private Date fechaUltimoRegistro;

    public EjercicioConProgresoView(){

    }

    public EjercicioConProgresoView(String nombreRutina, int idEjercicio, String nombreEjercicio, BigDecimal pesoUsado, int repeticiones, Date fechaUltimoRegistro){
        this.nombreRutina = nombreRutina;
        this.idEjercicio = idEjercicio;
        this.nombreEjercicio = nombreEjercicio;
        this.pesoUsado = pesoUsado;
        this.repeticiones = repeticiones;
        this.fechaUltimoRegistro = fechaUltimoRegistro;
    }

    public String getNombreRutina() {
        return nombreRutina;
    }

    public void setNombreRutina(String nombreRutina) {
        this.nombreRutina = nombreRutina;
    }

    public int getIdEjercicio() {
        return idEjercicio;
    }

    public void setIdEjercicio(int idEjercicio) {
        this.idEjercicio = idEjercicio;
    }

    public String getNombreEjercicio() {
        return nombreEjercicio;
    }

    public void setNombreEjercicio(String nombreEjercicio) {
        this.nombreEjercicio = nombreEjercicio;
    }

    public BigDecimal getPesoUsado() {
        return pesoUsado;
    }

    public void setPesoUsado(BigDecimal pesoUsado) {
        this.pesoUsado = pesoUsado;
    }

    public int getRepeticiones() {
        return repeticiones;
    }

    public void setRepeticiones(int repeticiones) {
        this.repeticiones = repeticiones;
    }

    public Date getFechaUltimoRegistro() {
        return fechaUltimoRegistro;
    }

    public void setFechaUltimoRegistro(Date fechaUltimoRegistro) {
        this.fechaUltimoRegistro = fechaUltimoRegistro;
    }
}

