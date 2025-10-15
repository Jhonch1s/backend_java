package com.example.gymtrackerweb.dao;


import com.example.gymtrackerweb.db.databaseConection;
import com.example.gymtrackerweb.dto.ProgresoCard;
import com.example.gymtrackerweb.model.ProgresoEjercicio;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProgresoEjercicioDAO {
    public void agregarProgresoEjercicio(ProgresoEjercicio p){
        String sql = "INSERT INTO progreso_ejercicio(id_cliente, id_ejercicio, fecha, peso_usado, repeticiones) VALUES (?, ?, ?, ?, ?)";
        try {
            Connection conexion = databaseConection.getInstancia().getConnection();
            PreparedStatement sentencia = conexion.prepareStatement(sql);
            sentencia.setInt(1, p.getIdCliente());
            sentencia.setInt(2, p.getIdEjercicio());
            sentencia.setDate(3, p.getFecha());
            sentencia.setInt(4, p.getPesoUsado());
            sentencia.setInt(5, p.getRepeticiones());

            sentencia.execute();
            System.out.println("Nuevo progreso ejercicio creado.");
        }catch(Exception e){
            System.err.println("Error" + e.getMessage());
        }
    }

    public void eliminarProgresoEjercicio(ProgresoEjercicio p){
        String sql = "DELETE FROM progreso_ejercicio WHERE id = ?";
        try{
            Connection conexion = databaseConection.getInstancia().getConnection();
            PreparedStatement sentencia = conexion.prepareStatement(sql);
            sentencia.setInt(1, p.getId());
            sentencia.execute();
            System.out.println("Progreso ejercicio eliminado correctamente.");
        }catch(Exception err){
            System.out.println("Error: "+err.getMessage());
        }
    }

    public void actualizarProgresoEjercicio(ProgresoEjercicio p){
        String sql = "UPDATE progreso_ejercicio SET id_cliente = ?, id_ejercicio = ?, fecha = ?, peso_usado = ?, repeticiones = ? WHERE id = ?";
        try{
            Connection conexion = databaseConection.getInstancia().getConnection();
            PreparedStatement sentencia = conexion.prepareStatement(sql);
            sentencia.setInt(1, p.getIdCliente());
            sentencia.setInt(2, p.getIdEjercicio());
            sentencia.setDate(3, p.getFecha());
            sentencia.setInt(4, p.getPesoUsado());
            sentencia.setInt(5, p.getRepeticiones());
            sentencia.setInt(6, p.getId());

            sentencia.executeUpdate();
            System.out.println("Progreso ejercicio modificado correctamente.");
        }catch(Exception err){
            System.out.println("Error: "+err.getMessage());
        }
    }

    public List<ProgresoEjercicio> listarProgresoEjercicioDeUsuario(ProgresoEjercicio p){
        List<ProgresoEjercicio> progresoLista = new ArrayList<>();
        String sql = "SELECT * FROM progreso_ejercicio WHERE id_cliente = ?";
        Connection conexion = databaseConection.getInstancia().getConnection();
        try{
            PreparedStatement sentencia = conexion.prepareStatement(sql);
            sentencia.setInt(1, p.getIdCliente());
            ResultSet resultado = sentencia.executeQuery();
            while(resultado.next()){
                ProgresoEjercicio progreso = new ProgresoEjercicio();
                //Get int id? Deberia ser id_progreso
                progreso.setId(resultado.getInt("id_progreso"));
                //Doble serId deberia de IdCliente
                progreso.setIdCliente(resultado.getInt("id_cliente"));
                progreso.setIdEjercicio(resultado.getInt("id_ejercicio"));
                progreso.setFecha(resultado.getDate("fecha"));
                progreso.setPesoUsado(resultado.getInt("peso_usado"));
                progreso.setRepeticiones(resultado.getInt("repeticiones"));

                progresoLista.add(progreso);
            }
        }catch(Exception err){
            System.out.println("Error: "+err.getMessage());
        }
        return progresoLista;
    }

    public BigDecimal obtenerKgLevantadosTotal(String idCliente) throws Exception {
        String sql = """
            SELECT COALESCE(SUM(peso_usado * COALESCE(repeticiones, 1)), 0) AS total
            FROM progreso_ejercicio
            WHERE TRIM(id_cliente) = TRIM(?)
        """;
        Connection conexion = databaseConection.getInstancia().getConnection();
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, idCliente == null ? null : idCliente.trim());

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                BigDecimal total = rs.getBigDecimal("total");
                return (total != null) ? total : BigDecimal.ZERO;
            }
        }
    }


    public BigDecimal obtenerKgLevantadosEnMes(String idCliente, int anio, int mes1a12) throws Exception {
        String sql = """
        SELECT COALESCE(SUM(peso_usado * COALESCE(repeticiones, 1)), 0) AS total
        FROM progreso_ejercicio
        WHERE TRIM(id_cliente) = TRIM(?)
          AND fecha >= ?
          AND fecha < ?
    """;
        LocalDate desde = LocalDate.of(anio, mes1a12, 1);
        LocalDate hasta = desde.plusMonths(1);

        Connection conexion = databaseConection.getInstancia().getConnection();
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, idCliente == null ? null : idCliente.trim());
            ps.setDate(2, java.sql.Date.valueOf(desde));
            ps.setDate(3, java.sql.Date.valueOf(hasta));

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                BigDecimal total = rs.getBigDecimal("total");
                return (total != null) ? total : BigDecimal.ZERO;
            }
        }
    }

    public List<ProgresoCard> ultimos3Progresos(String idCliente) throws SQLException {
        final String sql = """
            WITH pe AS (
              SELECT p.id_cliente, p.id_ejercicio, p.fecha, p.peso_usado,
                     ROW_NUMBER() OVER (PARTITION BY p.id_cliente, p.id_ejercicio
                                        ORDER BY p.fecha DESC) AS rn
              FROM progreso_ejercicio p
              WHERE p.id_cliente = ?
            ),
            curr AS (
              SELECT id_ejercicio, fecha AS fecha_ult, peso_usado AS ultimo
              FROM pe WHERE rn = 1
            ),
            prev AS (
              SELECT id_ejercicio, peso_usado AS previo
              FROM pe WHERE rn = 2
            )
            SELECT e.id AS id_ejercicio, e.nombre AS ejercicio,
                   c.fecha_ult, c.ultimo,
                   (c.ultimo - COALESCE(pv.previo, c.ultimo)) AS dif_kg
            FROM curr c
            JOIN ejercicio e  ON e.id = c.id_ejercicio
            LEFT JOIN prev pv ON pv.id_ejercicio = c.id_ejercicio
            ORDER BY c.fecha_ult DESC
            LIMIT 3
            """;

        Connection conn = databaseConection.getInstancia().getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                List<ProgresoCard> out = new ArrayList<>();
                while (rs.next()) {
                    Date d = rs.getDate("fecha_ult");
                    LocalDate f = d != null ? d.toLocalDate() : null;
                    out.add(new ProgresoCard(
                            rs.getInt("id_ejercicio"),
                            rs.getString("ejercicio"),
                            f,
                            rs.getDouble("ultimo"),
                            rs.getDouble("dif_kg")
                    ));
                }
                return out;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
