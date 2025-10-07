package com.example.gymtrackerweb.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MovimientoDetalle {
    private Long idMov;
    private String staffNombre;
    private LocalDateTime fechaHora;
    private BigDecimal importe;
    private String medioPagoNombre;
    private String tipoClienteNombre;
    private String origenNombre;
    private Integer idMembresia;    // lo dejo por si no hay “nombre” en membresía
    private String idCliente;
    private String clienteNombre;

    public MovimientoDetalle(){}

    public MovimientoDetalle(Long idMov, String staffNombre, LocalDateTime fechaHora,
                             BigDecimal importe, String medioPagoNombre, String tipoClienteNombre,
                             String origenNombre, Integer idMembresia, String idCliente, String clienteNombre) {
        this.idMov = idMov;
        this.staffNombre = staffNombre;
        this.fechaHora = fechaHora;
        this.importe = importe;
        this.medioPagoNombre = medioPagoNombre;
        this.tipoClienteNombre = tipoClienteNombre;
        this.origenNombre = origenNombre;
        this.idMembresia = idMembresia;
        this.idCliente = idCliente;
        this.clienteNombre = clienteNombre;
    }


    public Long getIdMov() {
        return idMov;
    }

    public void setIdMov(Long idMov) {
        this.idMov = idMov;
    }

    public String getStaffNombre() {
        return staffNombre;
    }

    public void setStaffNombre(String staffNombre) {
        this.staffNombre = staffNombre;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    public void setImporte(BigDecimal importe) {
        this.importe = importe;
    }

    public String getMedioPagoNombre() {
        return medioPagoNombre;
    }

    public void setMedioPagoNombre(String medioPagoNombre) {
        this.medioPagoNombre = medioPagoNombre;
    }

    public String getTipoClienteNombre() {
        return tipoClienteNombre;
    }

    public void setTipoClienteNombre(String tipoClienteNombre) {
        this.tipoClienteNombre = tipoClienteNombre;
    }

    public String getOrigenNombre() {
        return origenNombre;
    }

    public void setOrigenNombre(String origenNombre) {
        this.origenNombre = origenNombre;
    }

    public Integer getIdMembresia() {
        return idMembresia;
    }

    public void setIdMembresia(Integer idMembresia) {
        this.idMembresia = idMembresia;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }

    @Override
    public String toString() {
        return "MovimientoDetalle{" +
                "idMov=" + idMov +
                ", staffNombre='" + staffNombre + '\'' +
                ", fechaHora=" + fechaHora +
                ", importe=" + importe +
                ", medioPagoNombre='" + medioPagoNombre + '\'' +
                ", tipoClienteNombre='" + tipoClienteNombre + '\'' +
                ", origenNombre='" + origenNombre + '\'' +
                ", idMembresia=" + idMembresia +
                ", idCliente='" + idCliente + '\'' +
                ", clienteNombre='" + clienteNombre + '\'' +
                '}';
    }
}

