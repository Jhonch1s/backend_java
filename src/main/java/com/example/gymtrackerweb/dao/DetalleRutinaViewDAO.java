package com.example.gymtrackerweb.dao;


import com.example.gymtrackerweb.db.databaseConection;
import com.example.gymtrackerweb.model.DetalleRutinaView;
import com.example.gymtrackerweb.model.enums.Dificultad;
import com.example.gymtrackerweb.model.enums.Objetivo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DetalleRutinaViewDAO {

    public List<DetalleRutinaView> listarDetallesPorRutina(int id) {
        List<DetalleRutinaView> lista = new ArrayList<>();
        String sql = "SELECT detalle_rutina.id as id, id_ejercicio, ejercicio.nombre as nombre_ejercicio, ejercicio.dificultad as dificultad_ejercicio, grupo_muscular.nombre as grupo_muscular_ejercicio, ejercicio.url as url_ejercicio, id_rutina, rutina.objetivo as objetivo_rutina, rutina.nombre as nombre_rutina, rutina.duracion_semanas as duracion_rutina, series, repeticiones FROM detalle_rutina, ejercicio, rutina, grupo_muscular WHERE detalle_rutina.id_rutina = rutina.id AND detalle_rutina.id_ejercicio = ejercicio.id AND ejercicio.grupo_muscular_id = grupo_muscular.id AND id_rutina = ?";
        try (Connection conexion = databaseConection.getInstancia().getConnection()) {
            PreparedStatement sentencia = conexion.prepareStatement(sql);
            sentencia.setInt(1, id);
            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    DetalleRutinaView d = new DetalleRutinaView();
                    d.setId(resultado.getInt(1));
                    d.setId_ejercicio(resultado.getInt(2));
                    d.setNombre_ejercicio(resultado.getString(3));
                    d.setDificultad_ejercicio(Dificultad.valueOf(resultado.getString(4)));
                    d.setGrupo_muscular_ejercicio(resultado.getString(5));
                    d.setUrl_ejercicio(resultado.getString(6));
                    d.setId_rutina(resultado.getInt(7));
                    d.setObjetivo_rutina(Objetivo.valueOf(resultado.getString(8)));
                    d.setNombre_rutina(resultado.getString(9));
                    d.setDuracion_rutina(resultado.getInt(10));
                    d.setSeries(resultado.getInt(11));
                    d.setRepeticiones(resultado.getInt(12));
                    lista.add(d);
                }
            } catch (Exception e) {
                System.out.println("Error al listar detalles de rutina: " + e.getMessage());
            }
            return lista;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

