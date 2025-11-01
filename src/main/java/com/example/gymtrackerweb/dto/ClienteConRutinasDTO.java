package com.example.gymtrackerweb.dto;

import java.util.ArrayList;
import java.util.List;

public class ClienteConRutinasDTO {
    private String clienteCi;
    private String clienteNombre;
    private String clienteApellido;
    private List<RutinaAsignadaDTO> rutinas;

    public ClienteConRutinasDTO(String clienteCi, String clienteNombre, String clienteApellido) {
        this.clienteCi = clienteCi;
        this.clienteNombre = clienteNombre;
        this.clienteApellido = clienteApellido;
        this.rutinas = new ArrayList<>();
    }

    // Getters
    public String getClienteCi() { return clienteCi; }
    public String getClienteNombre() { return clienteNombre; }
    public String getClienteApellido() { return clienteApellido; }
    public List<RutinaAsignadaDTO> getRutinas() { return rutinas; }

    public void addRutina(RutinaAsignadaDTO rutina) {
        this.rutinas.add(rutina);
    }
}
