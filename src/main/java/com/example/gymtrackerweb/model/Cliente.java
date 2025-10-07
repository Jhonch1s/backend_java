package com.example.gymtrackerweb.model;
import java.sql.Date; // para manejar el tipo DATE de la BD

public class Cliente {
    private String ci;
    private String email;
    private String nombre;
    private String apellido;
    private String ciudad;
    private String direccion;
    private String tel;
    private String pais;
    private Date fechaIngreso;

    public Cliente() {}

    // Constructor
    public Cliente(String ci, String email, String nombre, String apellido, String ciudad, String direccion, String tel, String pais, Date fechaIngreso) {
        this.ci = ci;
        this.email = email;
        this.nombre = nombre;
        this.apellido = apellido;
        this.ciudad = ciudad;
        this.direccion = direccion;
        this.tel = tel;
        this.pais = pais;
        this.fechaIngreso = fechaIngreso;
    }

    // Getters
    public String getCi() {return ci;}

    public String getEmail() {return email;}

    public String getNombre() {return nombre;}

    public String getApellido() {return apellido;}

    public String getCiudad() {return ciudad;}

    public String getDireccion() {return direccion;}

    public String getTel() {return tel;}

    public String getPais() {return pais;}

    public Date getFechaIngreso() {return fechaIngreso;}

    // Setters
    public void setCi(String ci) {this.ci = ci;}

    public void setEmail(String email) {this.email = email;}

    public void setNombre(String nombre) {this.nombre = nombre;}

    public void setApellido(String apellido) {this.apellido = apellido;}

    public void setCiudad(String ciudad) {this.ciudad = ciudad;}

    public void setDireccion(String direccion) {this.direccion = direccion;}

    public void setTel(String tel) {this.tel = tel;}

    public void setPais(String pais) {this.pais = pais;}

    public void setFechaIngreso(Date fechaIngreso) {this.fechaIngreso = fechaIngreso;}

    @Override
    public String toString() {
        return "Cliente {" +
                "ci='" + ci + '\'' +
                ", email='" + email + '\'' +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", ciudad='" + ciudad + '\'' +
                ", direccion='" + direccion + '\'' +
                ", tel='" + tel + '\'' +
                ", pais='" + pais + '\'' +
                ", fechaIngreso=" + fechaIngreso +
                '}';
    }

}

