package com.example.gymtrackerweb.dto;
//dto para guardar solo los que conjugan una membresia
public class MembresiaMin {
    private int id;
    private String ciCliente;
    private String clienteNombre;

    public MembresiaMin(int id, String ciCliente, String clienteNombre) {
        this.id = id;
        this.ciCliente = ciCliente;
        this.clienteNombre = clienteNombre;
    }
    public int getId() { return id; }
    public String getCiCliente() { return ciCliente; }
    public String getClienteNombre() { return clienteNombre; }

    @Override public String toString() {
        return id + " - " + ciCliente + " (" + clienteNombre + ")";
    }
}
