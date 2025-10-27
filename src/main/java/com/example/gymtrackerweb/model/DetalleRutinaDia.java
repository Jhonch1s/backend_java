package com.example.gymtrackerweb.model; // O el paquete DTO que uses

import com.example.gymtrackerweb.model.enums.DiaSemana; // Importa el Enum

public class DetalleRutinaDia {

    private int id;
    private int idDetalleRutina;
    private DiaSemana diaSemana;

    public DetalleRutinaDia() {
    }

    public DetalleRutinaDia(int id, int idDetalleRutina, DiaSemana diaSemana) {
        this.id = id;
        this.idDetalleRutina = idDetalleRutina;
        this.diaSemana = diaSemana;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdDetalleRutina() {
        return idDetalleRutina;
    }

    public void setIdDetalleRutina(int idDetalleRutina) {
        this.idDetalleRutina = idDetalleRutina;
    }

    public DiaSemana getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(DiaSemana diaSemana) {
        this.diaSemana = diaSemana;
    }

    @Override
    public String toString() {
        return "DetalleRutinaDia{" +
                "id=" + id +
                ", idDetalleRutina=" + idDetalleRutina +
                ", diaSemana=" + diaSemana +
                '}';
    }
}