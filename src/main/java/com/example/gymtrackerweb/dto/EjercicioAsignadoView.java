package com.example.gymtrackerweb.dto;

import com.example.gymtrackerweb.model.enums.DiaSemana;
import java.util.List;

public class EjercicioAsignadoView {

    private int idDetalleRutina;
    private int idEjercicio;
    private String nombreEjercicio;
    private String grupoMuscular;
    private int series;
    private int repeticiones;
    private List<DiaSemana> dias;


    public EjercicioAsignadoView() {
    }

    public int getIdDetalleRutina() { return idDetalleRutina; }
    public void setIdDetalleRutina(int idDetalleRutina) { this.idDetalleRutina = idDetalleRutina; }
    public int getIdEjercicio() { return idEjercicio; }
    public void setIdEjercicio(int idEjercicio) { this.idEjercicio = idEjercicio; }
    public String getNombreEjercicio() { return nombreEjercicio; }
    public void setNombreEjercicio(String nombreEjercicio) { this.nombreEjercicio = nombreEjercicio; }
    public String getGrupoMuscular() { return grupoMuscular; }
    public void setGrupoMuscular(String grupoMuscular) { this.grupoMuscular = grupoMuscular; }
    public int getSeries() { return series; }
    public void setSeries(int series) { this.series = series; }
    public int getRepeticiones() { return repeticiones; }
    public void setRepeticiones(int repeticiones) { this.repeticiones = repeticiones; }
    public List<DiaSemana> getDias() { return dias; }
    public void setDias(List<DiaSemana> dias) { this.dias = dias; }
}