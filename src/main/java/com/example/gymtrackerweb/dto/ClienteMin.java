package com.example.gymtrackerweb.dto;
//dto para almacenar id y nombre, nada mas
public class ClienteMin {
    private String ci;
    private String nombreCompleto;

    public ClienteMin(String ci, String nombreCompleto) {
        this.ci = ci;
        this.nombreCompleto = nombreCompleto;
    }
    public String getCi() { return ci; }
    public String getNombreCompleto() { return nombreCompleto; }

    @Override public String toString() { return ci + " - " + nombreCompleto; }
}
