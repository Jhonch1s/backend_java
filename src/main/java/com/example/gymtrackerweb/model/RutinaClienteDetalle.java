package com.example.gymtrackerweb.model;
import java.sql.Date;

public class RutinaClienteDetalle {
    private Integer id;
    private String idCliente;
    private String clienteNombre;
    private Integer idRutina;
    private String rutinaNombre;
    private Date fechaAsignacion;
    private String estado;

    public RutinaClienteDetalle() {}

    public RutinaClienteDetalle(Integer id, String idCliente, String clienteNombre,
                                Integer idRutina, String rutinaNombre,
                                Date fechaAsignacion, String estado) {
        this.id = id;
        this.idCliente = idCliente;
        this.clienteNombre = clienteNombre;
        this.idRutina = idRutina;
        this.rutinaNombre = rutinaNombre;
        this.fechaAsignacion = fechaAsignacion;
        this.estado = estado;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getIdCliente() { return idCliente; }
    public void setIdCliente(String idCliente) { this.idCliente = idCliente; }

    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }

    public Integer getIdRutina() { return idRutina; }
    public void setIdRutina(Integer idRutina) { this.idRutina = idRutina; }

    public String getRutinaNombre() { return rutinaNombre; }
    public void setRutinaNombre(String rutinaNombre) { this.rutinaNombre = rutinaNombre; }

    public Date getFechaAsignacion() { return fechaAsignacion; }
    public void setFechaAsignacion(Date fechaAsignacion) { this.fechaAsignacion = fechaAsignacion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    @Override
    public String toString() {
        return "RutinaClienteDetalle{" +
                "id=" + id +
                ", idCliente='" + idCliente + '\'' +
                ", clienteNombre='" + clienteNombre + '\'' +
                ", idRutina=" + idRutina +
                ", rutinaNombre='" + rutinaNombre + '\'' +
                ", fechaAsignacion=" + fechaAsignacion +
                ", estado='" + estado + '\'' +
                '}';
    }
}