package com.example.gymtrackerweb.dao;

import com.example.gymtrackerweb.db.databaseConection;
import com.example.gymtrackerweb.model.Rutina;
import com.example.gymtrackerweb.model.enums.Objetivo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RutinaDAO {
    public void agregarRutina(Rutina r) {
        String sql = "INSERT INTO rutina (nombre, objetivo, duracion_semanas) VALUES (?, ?, ?)";
        try{
            Connection conexion = databaseConection.getInstancia().getConnection();
            PreparedStatement sentencia = conexion.prepareStatement(sql);
            sentencia.setString(1, r.getNombre());
            sentencia.setString(2, r.getObjetivo().name());
            sentencia.setInt(3, r.getDuracionSemanas());

            sentencia.execute();
        }catch(Exception err){
            System.out.println("Error: "+err.getMessage());
        }
    }

    public void eliminarRutina(Rutina r) {
        String sql = "DELETE FROM rutina WHERE id = ?";
        try{
            Connection conexion = databaseConection.getInstancia().getConnection();
            PreparedStatement sentencia = conexion.prepareStatement(sql);
            sentencia.setInt(1, r.getId());
            sentencia.execute();
        }catch(Exception err){
            System.out.println("Error: "+err.getMessage());
        }
    }

    public void modificarRutina(Rutina r) {
        String sql = "UPDATE rutina SET nombre = ?, objetivo = ?, duracion_semanas = ? WHERE id = ?";
        try{
            Connection conexion = databaseConection.getInstancia().getConnection();
            PreparedStatement sentencia = conexion.prepareStatement(sql);
            sentencia.setString(1, r.getNombre());
            sentencia.setString(2, r.getObjetivo().name());
            sentencia.setInt(3, r.getDuracionSemanas());
            sentencia.setInt(4, r.getId());

            sentencia.executeUpdate();
        }catch(Exception err){
            System.out.println("Error: "+err.getMessage());
        }
    }

    public List<Rutina> listarRutinas() {
        List<Rutina> lista = new ArrayList<>();
        String sql = "SELECT * FROM rutina";
        Connection conexion = databaseConection.getInstancia().getConnection();
        try (
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery()) {

            while (resultado.next()) {
                Rutina r = new Rutina();
                r.setId(resultado.getInt("id"));
                r.setNombre(resultado.getString("nombre"));
                String obj = resultado.getString("objetivo");
                r.setObjetivo(Objetivo.valueOf(obj.toUpperCase()));
                r.setDuracionSemanas(resultado.getInt("duracion_semanas"));
                lista.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public List<Rutina> listarRutinasPorObjetivo(Objetivo objetivo) {
        List<Rutina> listaRutinas = new ArrayList<>();
        String sql = "SELECT * FROM rutina WHERE objetivo = ?";
        Connection conexion = databaseConection.getInstancia().getConnection();
        try (
             PreparedStatement sentencia = conexion.prepareStatement(sql)){
             sentencia.setString(1, String.valueOf(objetivo));
             ResultSet resultado = sentencia.executeQuery();
            while (resultado.next()) {
                Rutina r = new Rutina();
                r.setId(resultado.getInt("id"));
                r.setNombre(resultado.getString("nombre"));
                String obj = resultado.getString("objetivo");
                r.setObjetivo(Objetivo.valueOf(obj.toUpperCase()));
                r.setDuracionSemanas(resultado.getInt("duracion_semanas"));
                listaRutinas.add(r);
            }
        }catch (Exception err){
            System.out.println("Error: "+err.getMessage());
        }
        return listaRutinas;
    }

    public boolean existePorNombre(String nombre) {
        final String sql = "SELECT 1 FROM rutina WHERE LOWER(nombre) = LOWER(?)";
        Connection cn = databaseConection.getInstancia().getConnection();
        try (PreparedStatement sentencia = cn.prepareStatement(sql)) {
            sentencia.setString(1, nombre);
            try (ResultSet rs = sentencia.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            System.out.println("Error al verificar existencia de rutina: " + e.getMessage());
        }
        return false;
    }

    public Rutina buscarPorId(int id) {
        final String sql = "SELECT id, nombre, objetivo, duracion_semanas FROM rutina WHERE id = ?";
        Connection cn = databaseConection.getInstancia().getConnection();
        try (PreparedStatement sentencia = cn.prepareStatement(sql)) {
            sentencia.setInt(1, id);
            try (ResultSet rs = sentencia.executeQuery()) {
                if (rs.next()) {
                    Rutina r = new Rutina();
                    r.setId(rs.getInt("id"));
                    r.setNombre(rs.getString("nombre"));
                    r.setObjetivo(Objetivo.valueOf(rs.getString("objetivo").toUpperCase()));
                    r.setDuracionSemanas(rs.getInt("duracion_semanas"));
                    return r;
                }
            }
        } catch (Exception e) {
            System.out.println("Error al buscar rutina por id: " + e.getMessage());
        }
        return null;
    }


}
