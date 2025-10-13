package com.example.gymtrackerweb.dto;
import com.example.gymtrackerweb.model.enums.Objetivo;

public class RutinaClienteView {
    private int id;
    private String nombre;
    private Objetivo objetivo;
    private String estado;
    private String gruposMusculares;
    private int duracionSemanas;

    public RutinaClienteView(){

    }

    public RutinaClienteView(int id, String nombre, Objetivo objetivo, String estado, String gruposMusculares, int duracionSemanas){
        this.id = id;
        this.nombre = nombre;
        this.objetivo = objetivo;
        this.estado = estado;
        this.gruposMusculares = gruposMusculares;
        this.duracionSemanas = duracionSemanas;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Objetivo getObjetivo() {
        return objetivo;
    }

    public void setObjetivo(Objetivo objetivo) {
        this.objetivo = objetivo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getGruposMusculares() {
        return gruposMusculares;
    }

    public void setGruposMusculares(String gruposMusculares) {
        this.gruposMusculares = gruposMusculares;
    }

    public int getDuracionSemanas() {
        return duracionSemanas;
    }

    public void setDuracionSemanas(int duracionSemanas) {
        this.duracionSemanas = duracionSemanas;
    }
}
