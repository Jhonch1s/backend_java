package com.example.gymtrackerweb.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MovimientoView {
    private long idMov;
    private LocalDateTime fechaHora;
    private BigDecimal importe;
    private String staffNombre;
    private String medioPagoNombre;
    private String tipoClienteNombre;
    private String origenNombre;
    private Integer idMembresia;     // puede ser null
    private String clienteNombre;     // "Nombre Apellido", puede ser null

    public MovimientoView() {}

    public MovimientoView(long idMov, LocalDateTime fechaHora, BigDecimal importe,
                          String staffNombre, String medioPagoNombre, String tipoClienteNombre,
                          String origenNombre, Integer idMembresia, String clienteNombre) {
        this.idMov = idMov;
        this.fechaHora = fechaHora;
        this.importe = importe;
        this.staffNombre = staffNombre;
        this.medioPagoNombre = medioPagoNombre;
        this.tipoClienteNombre = tipoClienteNombre;
        this.origenNombre = origenNombre;
        this.idMembresia = idMembresia;
        this.clienteNombre = clienteNombre;
    }

    public long getIdMov() { return idMov; }
    public void setIdMov(long idMov) { this.idMov = idMov; }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }

    public BigDecimal getImporte() { return importe; }
    public void setImporte(BigDecimal importe) { this.importe = importe; }

    public String getStaffNombre() { return staffNombre; }
    public void setStaffNombre(String staffNombre) { this.staffNombre = staffNombre; }

    public String getMedioPagoNombre() { return medioPagoNombre; }
    public void setMedioPagoNombre(String medioPagoNombre) { this.medioPagoNombre = medioPagoNombre; }

    public String getTipoClienteNombre() { return tipoClienteNombre; }
    public void setTipoClienteNombre(String tipoClienteNombre) { this.tipoClienteNombre = tipoClienteNombre; }

    public String getOrigenNombre() { return origenNombre; }
    public void setOrigenNombre(String origenNombre) { this.origenNombre = origenNombre; }

    public Integer getIdMembresia() { return idMembresia; }
    public void setIdMembresia(Integer idMembresia) { this.idMembresia = idMembresia; }

    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }

    @Override
    public String toString() {
        return "MovimientoView{" +
                "idMov=" + idMov +
                ", fechaHora=" + fechaHora +
                ", importe=" + importe +
                ", staff='" + staffNombre + '\'' +
                ", medioPago='" + medioPagoNombre + '\'' +
                ", tipoCliente='" + tipoClienteNombre + '\'' +
                ", origen='" + origenNombre + '\'' +
                ", membresia=" + idMembresia +
                ", cliente='" + clienteNombre + '\'' +
                '}';
    }
}
