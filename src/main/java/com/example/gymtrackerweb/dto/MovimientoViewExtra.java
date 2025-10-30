package com.example.gymtrackerweb.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MovimientoViewExtra {
    private long idMov;
    private LocalDateTime fechaHora;
    private BigDecimal importe;

    private String staffNombre;
    private String medioPagoNombre;
    private String tipoClienteNombre;
    private String origenNombre;

    private Integer idMembresia;
    private String planNombre;

    private String clienteCi;
    private String clienteNombre;

    public MovimientoViewExtra() {
    }

    public MovimientoViewExtra(long idMov,
                          LocalDateTime fechaHora,
                          BigDecimal importe,
                          String staffNombre,
                          String medioPagoNombre,
                          String tipoClienteNombre,
                          String origenNombre,
                          Integer idMembresia,
                          String clienteNombre,
                          String clienteCi,
                          String planNombre) {
        this.idMov = idMov;
        this.fechaHora = fechaHora;
        this.importe = importe;
        this.staffNombre = staffNombre;
        this.medioPagoNombre = medioPagoNombre;
        this.tipoClienteNombre = tipoClienteNombre;
        this.origenNombre = origenNombre;
        this.idMembresia = idMembresia;
        this.clienteNombre = clienteNombre;
        this.clienteCi = clienteCi;
        this.planNombre = planNombre;
    }

    public long getIdMov() {
        return idMov;
    }

    public void setIdMov(long idMov) {
        this.idMov = idMov;
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

    public String getStaffNombre() {
        return staffNombre;
    }

    public void setStaffNombre(String staffNombre) {
        this.staffNombre = staffNombre;
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

    public String getPlanNombre() {
        return planNombre;
    }

    public void setPlanNombre(String planNombre) {
        this.planNombre = planNombre;
    }

    public String getClienteCi() {
        return clienteCi;
    }

    public void setClienteCi(String clienteCi) {
        this.clienteCi = clienteCi;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }
}