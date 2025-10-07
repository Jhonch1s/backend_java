package com.example.gymtrackerweb.model;
import java.time.LocalDateTime;
import java.math.BigDecimal;
public class Movimiento {
    private Long idMov;
    private Integer idStaff;
    private LocalDateTime fechaHora;
    private BigDecimal importe;
    private Byte medioPagoID;
    private Byte tipoClienteID;
    private Byte origenId;
    private Integer idMembresia;
    private String idCliente;

    public Movimiento(){}

    public Movimiento (Long idMov, Integer idStaff, LocalDateTime fechaHora, BigDecimal importe,
                       Byte medioPagoID, Byte tipoClienteID, Byte origenId, Integer idMembresia,
                       String idCliente) {
        this.idMov = idMov;
        this.idStaff = idStaff;
        this.fechaHora = fechaHora;
        this.importe = importe;
        this.medioPagoID = medioPagoID;
        this.tipoClienteID = tipoClienteID;
        this.origenId = origenId;
        this.idMembresia = idMembresia;
        this.idCliente = idCliente;
    }

    public Movimiento (Integer idStaff, LocalDateTime fechaHora, BigDecimal importe,
                       Byte medioPagoID, Byte tipoClienteID, Byte origenId, Integer idMembresia,
                       String idCliente) {
        this.idStaff = idStaff;
        this.fechaHora = fechaHora;
        this.importe = importe;
        this.medioPagoID = medioPagoID;
        this.tipoClienteID = tipoClienteID;
        this.origenId = origenId;
        this.idMembresia = idMembresia;
        this.idCliente = idCliente;
    }
    //Getters and setters + to string hecho con generador de IntelliJ

    public Long getIdMov() {
        return idMov;
    }

    public void setIdMov(Long idMov) {
        this.idMov = idMov;
    }

    public Integer getIdStaff() {
        return idStaff;
    }

    public void setIdStaff(Integer idStaff) {
        this.idStaff = idStaff;
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

    public Byte getMedioPagoID() {
        return medioPagoID;
    }

    public void setMedioPagoID(Byte medioPagoID) {
        this.medioPagoID = medioPagoID;
    }

    public Byte getTipoClienteID() {
        return tipoClienteID;
    }

    public void setTipoClienteID(Byte tipoClienteID) {
        this.tipoClienteID = tipoClienteID;
    }

    public Byte getOrigenId() {
        return origenId;
    }

    public void setOrigenId(Byte origenId) {
        this.origenId = origenId;
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

    @Override
    public String toString() {
        return "Movimiento{" +
                "idMov=" + idMov +
                ", idStaff=" + idStaff +
                ", fechaHora=" + fechaHora +
                ", importe=" + importe +
                ", medioPagoID=" + medioPagoID +
                ", tipoClienteID=" + tipoClienteID +
                ", origenId=" + origenId +
                ", idMembresia=" + idMembresia +
                ", idCliente='" + idCliente + '\'' +
                '}';
    }
}
