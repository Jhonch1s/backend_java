package com.example.gymtrackerweb.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.*;
import java.util.Objects;
import java.util.Optional;

public class RegistroGym {

    private Integer idRegistro;         // PK autoincrement
    private String ciCliente;           // FK a cliente.ci
    private LocalDate fecha;            // Generada desde 'entrada' en la BD (columna STORED)
    private LocalDateTime entrada;      // NOT NULL
    private LocalDateTime salida;       // Puede ser NULL si la sesión no fue cerrada

    // ===== Constructores =====
    public RegistroGym() {}

    public RegistroGym(Integer idRegistro, String ciCliente, LocalDate fecha,
                       LocalDateTime entrada, LocalDateTime salida) {
        this.idRegistro = idRegistro;
        this.ciCliente = ciCliente;
        this.fecha = fecha;
        this.entrada = entrada;
        this.salida = salida;
    }

    // crear un registro sin id ni fecha, se deriva de entrada
    public static RegistroGym nuevo(String ciCliente, LocalDateTime entrada) {
        RegistroGym r = new RegistroGym();
        r.setCiCliente(ciCliente);
        r.setEntrada(entrada);
        r.setFecha(entrada.toLocalDate());
        return r;
    }

    // getters y setters
    public Integer getIdRegistro() { return idRegistro; }
    public void setIdRegistro(Integer idRegistro) { this.idRegistro = idRegistro; }

    public String getCiCliente() { return ciCliente; }
    public void setCiCliente(String ciCliente) { this.ciCliente = ciCliente; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public LocalDateTime getEntrada() { return entrada; }
    public void setEntrada(LocalDateTime entrada) {
        this.entrada = entrada;
        if (entrada != null) this.fecha = entrada.toLocalDate();
    }

    public LocalDateTime getSalida() { return salida; }
    public void setSalida(LocalDateTime salida) { this.salida = salida; }

    // Utilidades

    // minutos de la sesión si tiene salida, vacío si la no se cerró.
    public Optional<Long> duracionMinutos() {
        if (entrada != null && salida != null) {
            return Optional.of(Duration.between(entrada, salida).toMinutes());
        }
        return Optional.empty();
    }

    //ver si la sesión tiene salida
    public boolean cerrada() { return salida != null; }

    // mapeo
    public static RegistroGym fromResultSet(ResultSet rs) throws SQLException {
        RegistroGym r = new RegistroGym();
        r.setIdRegistro(rs.getInt("id_registro"));
        if (rs.wasNull()) r.setIdRegistro(null);

        r.setCiCliente(rs.getString("ci_cliente"));

        LocalDate f = null;
        try {
            f = rs.getObject("fecha", LocalDate.class);
        } catch (Throwable ignored) {
            java.sql.Date d = rs.getDate("fecha");
            if (d != null) f = d.toLocalDate();
        }
        r.setFecha(f);

        LocalDateTime ent = null;
        try {
            ent = rs.getObject("entrada", LocalDateTime.class);
        } catch (Throwable ignored) {
            java.sql.Timestamp t = rs.getTimestamp("entrada");
            if (t != null) ent = t.toLocalDateTime();
        }
        r.setEntrada(ent);

        LocalDateTime sal = null;
        try {
            sal = rs.getObject("salida", LocalDateTime.class);
        } catch (Throwable ignored) {
            java.sql.Timestamp t = rs.getTimestamp("salida");
            if (t != null) sal = t.toLocalDateTime();
        }
        r.setSalida(sal);

        return r;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RegistroGym)) return false;
        RegistroGym that = (RegistroGym) o;
        if (this.idRegistro != null && that.idRegistro != null) {
            return Objects.equals(this.idRegistro, that.idRegistro);
        }
        return Objects.equals(ciCliente, that.ciCliente)
                && Objects.equals(entrada, that.entrada);
    }

    @Override
    public int hashCode() {
        return (idRegistro != null)
                ? Objects.hash(idRegistro)
                : Objects.hash(ciCliente, entrada);
    }

    @Override
    public String toString() {
        return "RegistroGym{" +
                "id=" + idRegistro +
                ", ci='" + ciCliente + '\'' +
                ", fecha=" + fecha +
                ", entrada=" + entrada +
                ", salida=" + salida +
                '}';
    }
}
