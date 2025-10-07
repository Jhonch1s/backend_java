package com.example.gymtrackerweb.model;
import java.math.BigDecimal;

public class Plan {
    private int id;
    private String nombre;
    private BigDecimal valor;
    private short duracionTotal;
    private byte duracionUnidadId;
    private String urlImagen;
    private boolean estado;
    private String duracionUnidadNombre;

    // Constructor vacío
    public Plan() {}

    // Constructor completo
    public Plan(int id, String nombre, BigDecimal valor, short duracionTotal,
                byte duracionUnidadId, String urlImagen, boolean estado) {
        this.id = id;
        this.nombre = nombre;
        this.valor = valor;
        this.duracionTotal = duracionTotal;
        this.duracionUnidadId = duracionUnidadId;
        this.urlImagen = urlImagen;
        this.estado = estado;
    }
    //Constructor sin id
    public Plan( String nombre, BigDecimal valor, short duracionTotal,
                byte duracionUnidadId, String urlImagen, boolean estado) {
        this.id = 0;
        this.nombre = nombre;
        this.valor = valor;
        this.duracionTotal = duracionTotal;
        this.duracionUnidadId = duracionUnidadId;
        this.urlImagen = urlImagen;
        this.estado = estado;
    }

    // Getters
    public int getId() {return id;}

    public String getNombre() {return nombre;}

    public BigDecimal getValor() {return valor;}

    public short getDuracionTotal() {return duracionTotal;}

    public byte getDuracionUnidadId() {return duracionUnidadId;}

    public String getUrlImagen() {return urlImagen;}

    public boolean isEstado() {return estado;}

    // Setters
    public void setId(int id) {this.id = id;}

    public void setNombre(String nombre) {this.nombre = nombre;}

    public void setValor(BigDecimal valor) {this.valor = valor;}

    public void setDuracionTotal(short duracionTotal) {this.duracionTotal = duracionTotal;}

    public void setDuracionUnidadId(byte duracionUnidadId) {this.duracionUnidadId = duracionUnidadId;   }

    public void setUrlImagen(String urlImagen) {this.urlImagen = urlImagen;}

    public void setEstado(boolean estado) {this.estado = estado;}

    public String getDuracionUnidadNombre() {return duracionUnidadNombre;}
    public void setDuracionUnidadNombre(String duracionUnidadNombre) {this.duracionUnidadNombre = duracionUnidadNombre;}

    public String toString() {
        return "Plan {" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", valor=" + valor +
                ", duracionTotal=" + duracionTotal +
                ", duracionUnidadId=" + duracionUnidadId +
                ", urlImagen='" + urlImagen + '\'' +
                ", estado=" + (estado ? "Activo" : "Inactivo") +
                '}';
    }
}

