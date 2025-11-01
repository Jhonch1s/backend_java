package com.example.gymtrackerweb.dto;

import java.time.LocalDate;

// DTO para la lista principal de "Asignar a Clientes"
public class ClienteRutinaDTO {
    private String clienteCi;
    private String clienteNombre;
    private String clienteApellido;
    private String rutinaNombre;
    private LocalDate fechaAsignacion;
    private String estadoRutina;

    // Getters y Setters para todos los campos...

    // (Constructor simple para el DAO)
    public ClienteRutinaDTO(String clienteCi, String clienteNombre, String clienteApellido,
                            String rutinaNombre, java.sql.Date fechaAsignacion, String estadoRutina) {
        this.clienteCi = clienteCi;
        this.clienteNombre = clienteNombre;
        this.clienteApellido = clienteApellido;
        this.rutinaNombre = rutinaNombre;
        this.fechaAsignacion = (fechaAsignacion != null) ? fechaAsignacion.toLocalDate() : null;
        this.estadoRutina = estadoRutina;
    }

    // Getters y Setters (¡importante para que el JSP los lea!)

    public String getClienteCi() { return clienteCi; }
    public void setClienteCi(String clienteCi) { this.clienteCi = clienteCi; }
    // ... crea el resto de getters y setters ...
    public String getClienteNombre() { return clienteNombre; }
    public String getClienteApellido() { return clienteApellido; }
    public String getRutinaNombre() { return rutinaNombre; }
    public LocalDate getFechaAsignacion() { return fechaAsignacion; }
    public String getEstadoRutina() { return estadoRutina; }
}
