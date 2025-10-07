package com.example.gymtrackerweb.model;

public class Staff {
    private int id;
    private String usuarioLogin;
    private String nombreCompleto;
    private String contrasenia;
    private int rol;
    private int estado;

    public Staff(){

    }

    public Staff(int id,String usuarioLogin, String nombreCompleto, String contrasenia, int rol, int estado) {
        this.id = id;
        this.usuarioLogin = usuarioLogin;
        this.nombreCompleto = nombreCompleto;
        this.contrasenia = contrasenia;
        this.rol = rol;
        this.estado = estado;
    }

    public Staff(int id,String usuarioLogin, String nombreCompleto, int rol, int estado) {
        this.id = id;
        this.usuarioLogin = usuarioLogin;
        this.nombreCompleto = nombreCompleto;
        this.rol = rol;
        this.estado = estado;
    }

    public Staff(String usuarioLogin, String nombreCompleto, int rol, int estado) {
        this.usuarioLogin = usuarioLogin;
        this.nombreCompleto = nombreCompleto;
        this.rol = rol;
        this.estado = estado;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    public int getId() { return id; }
    public String getUsuarioLogin() { return usuarioLogin; }
    public String getNombreCompleto() { return nombreCompleto; }
    public int getRol() { return rol; }
    public int getEstado() { return estado; }

    public void setId(int id) { this.id = id; }
    public void setUsuarioLogin(String usuarioLogin) { this.usuarioLogin = usuarioLogin; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    public void setRol(int rol) { this.rol = rol; }
    public void setEstado(int estado) { this.estado = estado; }

    public void mostrarInformacion() {
        System.out.println("ID: " + id);
        System.out.println("Usuario Login: " + usuarioLogin);
        System.out.println("Nombre Completo: " + nombreCompleto);
        System.out.println("Rol: " + rol);
        System.out.println("Estado: " + estado);
    }
}
