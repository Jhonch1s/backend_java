package com.example.gymtrackerweb.dto;

public class ProgresoDetalleView {
    private String fecha;
    private int pesoUsado;
    private int repeticiones;

    public ProgresoDetalleView(String fecha, int pesoUsado, int repeticiones) {
        this.fecha = fecha;
        this.pesoUsado = pesoUsado;
        this.repeticiones = repeticiones;
    }

    public String getFecha() {
        return fecha;
    }

    public int getPesoUsado() {
        return pesoUsado;
    }

    public int getRepeticiones() {
        return repeticiones;
    }
}
