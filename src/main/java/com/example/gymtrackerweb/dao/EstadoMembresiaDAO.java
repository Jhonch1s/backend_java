package com.example.gymtrackerweb.dao;


import com.example.gymtrackerweb.db.databaseConection;
import com.example.gymtrackerweb.model.EstadoMembresia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EstadoMembresiaDAO {
    public void agregarEstadoMembresia(EstadoMembresia c) {
        String sql = "INSERT INTO estado_membresia (id, nombre) " +
                "VALUES (?, ?)";
        try{
            Connection conexion = databaseConection.getInstancia().getConnection();
            PreparedStatement sentencia = conexion.prepareStatement(sql);
            sentencia.setInt(1, c.getId());
            sentencia.setString(2, c.getNombre());
            sentencia.execute();
            System.out.println("Estado de Membresía cargado correctamente.");
        }catch(Exception e){
            System.out.println("Error: "+e.getMessage());
        }
    }

    public void eliminarEstadoMembresia(int id) {
        String sql = "DELETE FROM estado_membresia WHERE id = ?";
        try{
            Connection conexion = databaseConection.getInstancia().getConnection();
            PreparedStatement sentencia = conexion.prepareStatement(sql);
            sentencia.setInt(1, id);
            sentencia.execute();
            System.out.println("Estado de Membresía eliminado correctamente.");
        }catch(Exception e){
            System.out.println("Error: "+e.getMessage());
        }
    }
    public void modificarEstadoMembresia(EstadoMembresia c) {
        String sql = "UPDATE estado_membresia SET nombre = ? WHERE id = ?";
        try {
            Connection conexion = databaseConection.getInstancia().getConnection();
            PreparedStatement sentencia = conexion.prepareStatement(sql);

            sentencia.setString(1, c.getNombre());
            sentencia.setInt(2, c.getId());

            int filas = sentencia.executeUpdate();

            if (filas > 0) {
                System.out.println("Estado de Membresía modificado correctamente.");
            } else {
                System.out.println("No se encontró estado de membresía con ID: " + c.getId());
            }

        } catch (Exception e) {
            System.out.println("Error al modificar estado de membresía: " + e.getMessage());
        }
    }
    public void listarEstadoMembresia(){
        String sql = "SELECT * FROM estado_membresia";
        try{
            Connection conexion = databaseConection.getInstancia().getConnection();
            PreparedStatement sentencia = conexion.prepareStatement(sql);

            ResultSet resultado = sentencia.executeQuery();
            while (resultado.next()) {
                int id = resultado.getInt("id");
                String nombre = resultado.getString("nombre");

                System.out.println("ID: " + id);
                System.out.println("Nombre: " + nombre);
                System.out.println("-------------------------------");
            }

            resultado.close();
            sentencia.close();
        }catch(Exception e){
            System.out.println("Error: "+e.getMessage());
        }
    }


    private EstadoMembresia mapestadoMembresia(ResultSet rs) throws SQLException {
        EstadoMembresia c = new EstadoMembresia();
        c.setId(rs.getInt("id"));
        c.setNombre(rs.getString("nombre"));
        return c;
    }

}
