package com.example.gymtrackerweb.model;

import com.example.gymtrackerweb.model.enums.Dificultad;
import com.example.gymtrackerweb.model.enums.Objetivo;

public class DetalleRutinaView {
    private int id;
    private int id_ejercicio;
    private String nombre_ejercicio;
    private Dificultad dificultad_ejercicio;
    private String grupo_muscular_ejercicio;
    private String url_ejercicio;
    private int id_rutina;
    private Objetivo objetivo_rutina;
    private String nombre_rutina;
    private int duracion_rutina;
    private int series;
    private int repeticiones;

    public DetalleRutinaView() {
    }

    public DetalleRutinaView(int id, int id_ejercicio, String nombre_ejercicio, Dificultad dificultad_ejercicio, String grupo_muscular_ejercicio, String url_ejercicio, int id_rutina, Objetivo objetivo_rutina, String nombre_rutina, int duracion_rutina, int series, int repeticiones) {
        this.id = id;
        this.id_ejercicio = id_ejercicio;
        this.nombre_ejercicio = nombre_ejercicio;
        this.dificultad_ejercicio = dificultad_ejercicio;
        this.grupo_muscular_ejercicio = grupo_muscular_ejercicio;
        this.url_ejercicio = url_ejercicio;
        this.id_rutina = id_rutina;
        this.objetivo_rutina = objetivo_rutina;
        this.nombre_rutina = nombre_rutina;
        this.duracion_rutina = duracion_rutina;
        this.series = series;
        this.repeticiones = repeticiones;
    }

    public int getId_ejercicio() {
        return id_ejercicio;
    }

    public void setId_ejercicio(int id_ejercicio) {
        this.id_ejercicio = id_ejercicio;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre_ejercicio() {
        return nombre_ejercicio;
    }

    public void setNombre_ejercicio(String nombre_ejercicio) {
        this.nombre_ejercicio = nombre_ejercicio;
    }

    public Dificultad getDificultad_ejercicio() {
        return dificultad_ejercicio;
    }

    public void setDificultad_ejercicio(Dificultad dificultad_ejercicio) {
        this.dificultad_ejercicio = dificultad_ejercicio;
    }

    public String getGrupo_muscular_ejercicio() {
        return grupo_muscular_ejercicio;
    }

    public void setGrupo_muscular_ejercicio(String grupo_muscular_ejercicio) {
        this.grupo_muscular_ejercicio = grupo_muscular_ejercicio;
    }

    public String getUrl_ejercicio() {
        return url_ejercicio;
    }

    public void setUrl_ejercicio(String url_ejercicio) {
        this.url_ejercicio = url_ejercicio;
    }

    public int getId_rutina() {
        return id_rutina;
    }

    public void setId_rutina(int id_rutina) {
        this.id_rutina = id_rutina;
    }

    public Objetivo getObjetivo_rutina() {
        return objetivo_rutina;
    }

    public void setObjetivo_rutina(Objetivo objetivo_rutina) {
        this.objetivo_rutina = objetivo_rutina;
    }

    public String getNombre_rutina() {
        return nombre_rutina;
    }

    public void setNombre_rutina(String nombre_rutina) {
        this.nombre_rutina = nombre_rutina;
    }

    public int getDuracion_rutina() {
        return duracion_rutina;
    }

    public void setDuracion_rutina(int duracion_rutina) {
        this.duracion_rutina = duracion_rutina;
    }

    public int getSeries() {
        return series;
    }

    public void setSeries(int series) {
        this.series = series;
    }

    public int getRepeticiones() {
        return repeticiones;
    }

    public void setRepeticiones(int repeticiones) {
        this.repeticiones = repeticiones;
    }
}
