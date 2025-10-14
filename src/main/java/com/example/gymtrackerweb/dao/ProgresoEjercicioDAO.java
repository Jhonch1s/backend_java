package com.example.gymtrackerweb.dao;


import com.example.gymtrackerweb.db.databaseConection;
import com.example.gymtrackerweb.dto.EjercicioConProgresoView;
import com.example.gymtrackerweb.model.ProgresoEjercicio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

            sentencia.executeUpdate();
            System.out.println("Progreso ejercicio modificado correctamente.");
        }catch(Exception err){
            System.out.println("Error: "+err.getMessage());
        }
    }

    public List<ProgresoEjercicio> listarProgresoEjercicioDeUsuario(ProgresoEjercicio p){
        List<ProgresoEjercicio> progresoLista = new ArrayList<>();
        String sql = "SELECT * FROM progreso_ejercicio WHERE id_cliente = ?";
        try{
            Connection conexion = databaseConection.getInstancia().getConnection();
            PreparedStatement sentencia = conexion.prepareStatement(sql);
            sentencia.setInt(1, p.getIdCliente());
            ResultSet resultado = sentencia.executeQuery();
            while(resultado.next()){
                ProgresoEjercicio progreso = new ProgresoEjercicio();
                progreso.setId(resultado.getInt("id"));
                progreso.setId(resultado.getInt("id_cliente"));
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

    public List<EjercicioConProgresoView> listarEjerciciosConProgreso(String clienteId){
        List<EjercicioConProgresoView> progresoLista = new ArrayList<>();
        String sql = """
            SELECT 
                e.id AS id_ejercicio,
                e.nombre AS nombre_ejercicio,
                pe.peso_usado,
                pe.repeticiones,
                pe.fecha AS fecha_ultimo_registro
            FROM rutina_cliente rc
            INNER JOIN rutina r ON rc.id_rutina = r.id
            INNER JOIN detalle_rutina dr ON r.id = dr.id_rutina
            INNER JOIN ejercicio e ON dr.id_ejercicio = e.id
            LEFT JOIN progreso_ejercicio pe ON pe.id_progreso = (
                SELECT pe2.id_progreso
                FROM progreso_ejercicio pe2
                WHERE pe2.id_ejercicio = e.id
                  AND pe2.id_cliente = rc.id_cliente
                ORDER BY pe2.fecha DESC
                LIMIT 1
            )
            WHERE rc.id_cliente = ?
              AND rc.estado = 'ACTIVA'
        """;
        Connection conexion = databaseConection.getInstancia().getConnection();
        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, clienteId);
            ResultSet resultado = sentencia.executeQuery();
            while(resultado.next()){
                EjercicioConProgresoView view = new EjercicioConProgresoView();

                view.setIdEjercicio(resultado.getInt("id_ejercicio"));
                view.setNombreEjercicio(resultado.getString("nombre_ejercicio"));
                view.setPesoUsado(resultado.getBigDecimal("peso_usado")); // o Double según tu modelo
                view.setRepeticiones(resultado.getInt("repeticiones"));
                view.setFechaUltimoRegistro(resultado.getDate("fecha_ultimo_registro"));

                progresoLista.add(view);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return progresoLista;
    }

    public List<ProgresoEjercicio> obtenerRegistrosDetallados(int idEjercicio, String idCliente) {
        List<ProgresoEjercicio> lista = new ArrayList<>();
        String sql = "SELECT fecha, peso_usado, repeticiones " +
                "FROM progreso_ejercicio " +
                "WHERE id_ejercicio = ? AND id_cliente = ? " +
                "ORDER BY fecha DESC";
        Connection con = databaseConection.getInstancia().getConnection();
        try(PreparedStatement ps = con.prepareStatement(sql)){
            ps.setInt(1, idEjercicio);
            ps.setString(2, idCliente);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ProgresoEjercicio r = new ProgresoEjercicio();
                r.setFecha(rs.getDate("fecha"));
                r.setPesoUsado(rs.getInt("peso_usado"));
                r.setRepeticiones(rs.getInt("repeticiones"));
                lista.add(r);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return lista;
    }

    public List<ProgresoEjercicio> obtenerPRs(int idEjercicio, String idCliente) {
        List<ProgresoEjercicio> lista = new ArrayList<>();
        String sql = "SELECT fecha, peso_usado, repeticiones " +
                "FROM progreso_ejercicio " +
                "WHERE id_ejercicio = ? AND id_cliente = ? " +
                "ORDER BY peso_usado DESC " +
                "LIMIT 3";
        Connection con = databaseConection.getInstancia().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idEjercicio);
            ps.setString(2, idCliente);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ProgresoEjercicio r = new ProgresoEjercicio();
                r.setFecha(rs.getDate("fecha"));
                r.setPesoUsado(rs.getInt("peso_usado"));
                r.setRepeticiones(rs.getInt("repeticiones"));
                lista.add(r);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public int obtenerIdPorNombre(String nombre) {
        String sql = "SELECT id FROM ejercicio WHERE nombre = ?";
        try (Connection con = databaseConection.getInstancia().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }




}
