package com.example.gymtrackerweb.dto;

public class ProgresoDetalleView {
    private String fecha;
    private int pesoUsado;
    private int repeticiones;
    private Integer diferenciaPeso;

    public ProgresoDetalleView(String fecha, int pesoUsado, int repeticiones, Integer diferenciaPeso) {
        this.fecha = fecha;
        this.pesoUsado = pesoUsado;
        this.repeticiones = repeticiones;
        this.diferenciaPeso = diferenciaPeso;
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

    public int getDiferenciaPeso() {
        return diferenciaPeso;
    }

    public void setDiferenciaPeso(Integer diferenciaPeso) {
        this.diferenciaPeso = diferenciaPeso;
    }
}
