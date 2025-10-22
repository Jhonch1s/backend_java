package com.example.gymtrackerweb.dto;
import java.util.Date;

public class MembresiaPlanView {
    private int id;
    private int idPlan;
    private String idCliente;
    private Date fechaInicio;
    private Date fechaFin;
    private int estadoId;
    private String planNombre;
    private String urlImagen;

    // Getters/Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdPlan() { return idPlan; }
    public void setIdPlan(int idPlan) { this.idPlan = idPlan; }

    public String getIdCliente() { return idCliente; }
    public void setIdCliente(String idCliente) { this.idCliente = idCliente; }

    public Date getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(Date fechaInicio) { this.fechaInicio = fechaInicio; }

    public Date getFechaFin() { return fechaFin; }
    public void setFechaFin(Date fechaFin) { this.fechaFin = fechaFin; }

    public int getEstadoId() { return estadoId; }
    public void setEstadoId(int estadoId) { this.estadoId = estadoId; }

    public String getPlanNombre() { return planNombre; }
    public void setPlanNombre(String planNombre) { this.planNombre = planNombre; }

    public String getUrlImagen() { return urlImagen; }
    public void setUrlImagen(String urlImagen) { this.urlImagen = urlImagen; }
}
