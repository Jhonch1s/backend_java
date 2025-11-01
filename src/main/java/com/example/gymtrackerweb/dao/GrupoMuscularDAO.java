package com.example.gymtrackerweb.dao;

import com.example.gymtrackerweb.db.databaseConection;
import com.example.gymtrackerweb.model.GrupoMuscular;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GrupoMuscularDAO {

    public List<GrupoMuscular> listarTodos() {
        List<GrupoMuscular> grupos = new ArrayList<>();
        // La consulta SQL se basa en tu archivo GymTracker.sql
        String sql = "SELECT id, nombre FROM grupo_muscular ORDER BY nombre";
        Connection conn = databaseConection.getInstancia().getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                GrupoMuscular gm = new GrupoMuscular();
                gm.setId(rs.getInt("id"));
                gm.setNombre(rs.getString("nombre"));
                grupos.add(gm);
            }
        } catch (SQLException e) {
            // Usamos System.err para logs de error
            System.err.println("Error al listar grupos musculares: " + e.getMessage());
            e.printStackTrace(); // Imprime el stack trace para más detalles
        }
        return grupos;
    }

}