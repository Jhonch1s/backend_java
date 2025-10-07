package com.example.gymtrackerweb.dao;


import com.example.gymtrackerweb.db.databaseConection;
import com.example.gymtrackerweb.model.ProgresoEjercicio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

}
