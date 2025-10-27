package com.example.gymtrackerweb.dto;

import com.example.gymtrackerweb.model.enums.DiaSemana;

public class EjercicioRutinaView {
    private int idRutina;
    private String nombreRutina;
    private int idEjercicio;
    private String nombreEjercicio;
    private String grupoMuscular;
    private int series;
    private int repeticionesRutina;
    private DiaSemana diaSemana;

    public EjercicioRutinaView(){

    }

    public EjercicioRutinaView(int idRutina, String nombreRutina, int idEjercicio, String nombreEjercicio, String grupoMuscular, int series, int repeticionesRutina, DiaSemana diaSemana) {
        this.idRutina = idRutina;
        this.nombreRutina = nombreRutina;
        this.idEjercicio = idEjercicio;
        this.nombreEjercicio = nombreEjercicio;
        this.grupoMuscular = grupoMuscular;
        this.series = series;
        this.repeticionesRutina = repeticionesRutina;
        this.diaSemana = diaSemana;
    }

    public void setIdRutina(int idRutina) {
        this.idRutina = idRutina;
    }

    public int getIdRutina() {
        return idRutina;
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

    public String getGrupoMuscular() {
        return grupoMuscular;
    }

    public void setGrupoMuscular(String grupoMuscular) {
        this.grupoMuscular = grupoMuscular;
    }

    public int getSeries() {
        return series;
    }

    public void setSeries(int series) {
        this.series = series;
    }

    public int getRepeticionesRutina() {
        return repeticionesRutina;
    }

    public void setRepeticionesRutina(int repeticionesRutina) {
        this.repeticionesRutina = repeticionesRutina;
    }

    public DiaSemana getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(DiaSemana diaSemana) {
        this.diaSemana = diaSemana;
    }
}

