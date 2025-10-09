package com.example.gymtrackerweb.model;
public class UsuarioLogin {
    private int idLogin;
    private Integer idLoginStaff;       // Puede ser null
    private String idLoginCliente;      // Puede ser null
    private String password;

    public UsuarioLogin(int idLogin, Integer idLoginStaff, String idLoginCliente, String password) {
        this.idLogin = idLogin;
        this.idLoginStaff = idLoginStaff;
        this.idLoginCliente = idLoginCliente;
        this.password = password;
    }

    public UsuarioLogin(Integer idLoginStaff, String idLoginCliente, String password) {
        this.idLoginStaff = idLoginStaff;
        this.idLoginCliente = idLoginCliente;
        this.password = password;
    }

    public int getIdLogin() {
        return idLogin;
    }

    public void setIdLogin(int idLogin) {
        this.idLogin = idLogin;
    }

    public Integer getIdLoginStaff() {
        return idLoginStaff;
    }

    public void setIdLoginStaff(Integer idLoginStaff) {
        this.idLoginStaff = idLoginStaff;
    }

    public String getIdLoginCliente() {
        return idLoginCliente;
    }

    public void setIdLoginCliente(String idLoginCliente) {
        this.idLoginCliente = idLoginCliente;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}