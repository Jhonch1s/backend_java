package com.example.gymtrackerweb.dao;


import com.example.gymtrackerweb.db.databaseConection;
import com.example.gymtrackerweb.dto.EjercicioDTO;
import com.example.gymtrackerweb.dto.EjercicioRutinaView;
import com.example.gymtrackerweb.model.Ejercicio;
import com.example.gymtrackerweb.model.enums.Dificultad;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EjercicioDAO {
    public void agregarEjercicio(Ejercicio e) {
        String sql = "INSERT INTO ejercicio (nombre, grupo_muscular_id, dificultad, url) VALUES (?,?,?,?)";
        Connection conexion = databaseConection.getInstancia().getConnection();
        try{
            PreparedStatement sentencia = conexion.prepareStatement(sql);
            sentencia.setString(1, e.getNombre());
            sentencia.setInt(2, e.getGrupoMuscular());
            sentencia.setString(3, e.getDificultad().name());
            sentencia.setString(4, e.getUrl());

            sentencia.execute();
        }catch(Exception err){
            System.out.println("Error: "+err.getMessage());
        }
    }

    public void eliminarEjercicio(Ejercicio e) {
        String sql = "DELETE FROM ejercicio WHERE id = ?";
        Connection conexion = databaseConection.getInstancia().getConnection();
        try{
            PreparedStatement sentencia = conexion.prepareStatement(sql);
            sentencia.setInt(1, e.getId());
            sentencia.execute();
        }catch(Exception err){
            System.out.println("Error: "+err.getMessage());
        }
    }

    public void modificarEjercicio(Ejercicio e) {
        String sql = "UPDATE ejercicio SET nombre = ?, grupo_muscular_id = ?, dificultad = ?, url = ? WHERE id = ?";
        Connection conexion = databaseConection.getInstancia().getConnection();
        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, e.getNombre());
            sentencia.setInt(2, e.getGrupoMuscular());
            sentencia.setString(3, e.getDificultad().name());
            sentencia.setString(4, e.getUrl());
            sentencia.setInt(5, e.getId());

            sentencia.executeUpdate();
        } catch (Exception err) {
            System.out.println("Error: " + err.getMessage());
        }
    }

    public List<Ejercicio> listarEjercicios() {
        List<Ejercicio> lista = new ArrayList<>();
        String sql = "SELECT e.id, e.nombre, e.grupo_muscular_id, g.nombre AS grupo_muscular, e.dificultad " +
                "FROM ejercicio e INNER JOIN grupo_muscular g ON e.grupo_muscular_id = g.id";
        Connection conexion = databaseConection.getInstancia().getConnection();
        try (PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery()) {
            while (resultado.next()) {
                Ejercicio e = new Ejercicio();
                e.setId(resultado.getInt("id"));
                e.setNombre(resultado.getString("nombre"));
                e.setGrupoMuscular(resultado.getInt("grupo_muscular_id"));

                String nombreGrupo = resultado.getString("grupo_muscular");
                String dif = resultado.getString("dificultad");
                e.setDificultad(Dificultad.valueOf(dif.toUpperCase()));
                lista.add(e);
            }
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }
        return lista;
    }

    public List<Ejercicio> listarEjerciciosPorGrupoMuscular(String nombreGrupoMuscular) {
        List<Ejercicio> lista = new ArrayList<>();
        String sql = "SELECT e.id, e.nombre, e.grupo_muscular_id, g.nombre AS grupo_muscular, e.dificultad " +
                "FROM ejercicio e INNER JOIN grupo_muscular g ON e.grupo_muscular_id = g.id WHERE g.nombre = ?";
        Connection conexion = databaseConection.getInstancia().getConnection();
        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, nombreGrupoMuscular);
            ResultSet resultado = sentencia.executeQuery();
            while (resultado.next()) {
                Ejercicio e = new Ejercicio();
                e.setId(resultado.getInt("id"));
                e.setNombre(resultado.getString("nombre"));
                e.setGrupoMuscular(resultado.getInt("grupo_muscular_id"));
                String dif = resultado.getString("dificultad");
                e.setDificultad(Dificultad.valueOf(dif.toUpperCase()));
                lista.add(e);
            }
        } catch (Exception err) {
            System.out.println("Error al listar ejercicios: " + err.getMessage());
        }
        return lista;
    }

    public void listarGruposMusculares() {
        String sql = "SELECT * FROM grupo_muscular";
        Connection con = databaseConection.getInstancia().getConnection();
        try (PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("Grupos musculares disponibles:");
            while (rs.next()) {
                System.out.println(rs.getInt("id") + " - " + rs.getString("nombre"));
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public Ejercicio buscarPorId(int id) {
        final String sql = "SELECT id, nombre, grupo_muscular_id, dificultad, url FROM ejercicio WHERE id = ?";
        Connection cn = databaseConection.getInstancia().getConnection();
        try {
            PreparedStatement sentencia = cn.prepareStatement(sql);
            sentencia.setInt(1, id);
            try (ResultSet rs = sentencia.executeQuery()) {
                if (rs.next()) {
                    Ejercicio e = new Ejercicio();
                    e.setId(rs.getInt("id"));
                    e.setNombre(rs.getString("nombre"));
                    e.setGrupoMuscular(rs.getInt("grupo_muscular_id"));
                    e.setDificultad(Dificultad.valueOf(rs.getString("dificultad")));
                    e.setUrl(rs.getString("url"));
                    return e;
                }
            }
        } catch (Exception e) {
            System.out.println("Error al buscar ejercicio por id: " + e.getMessage());
        }
        return null;
    }

    public boolean existePorNombre(String nombre) {
        final String sql = "SELECT 1 FROM ejercicio WHERE LOWER(nombre) = LOWER(?)";
        Connection cn = databaseConection.getInstancia().getConnection();
        try (PreparedStatement sentencia = cn.prepareStatement(sql)) {
            sentencia.setString(1, nombre);
            try (ResultSet rs = sentencia.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            System.out.println("Error al verificar existencia de ejercicio: " + e.getMessage());
        }
        return false;
    }

    public List<EjercicioRutinaView> listarTodosEjerciciosDetallados() {
        List<EjercicioRutinaView> ejercicios = new ArrayList<>();
        String sql = """
        SELECT
            e.id AS id_ejercicio,
            e.nombre AS nombre_ejercicio,
            gm.nombre AS nombre_grupo_muscular
        FROM ejercicio e
        LEFT JOIN grupo_muscular gm ON e.grupo_muscular_id = gm.id
        ORDER BY e.nombre
    """;

        Connection conexion = databaseConection.getInstancia().getConnection();
        try (PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery()) {

            while (resultado.next()) {
                EjercicioRutinaView view = new EjercicioRutinaView();
                view.setIdEjercicio(resultado.getInt("id_ejercicio"));
                view.setNombreEjercicio(resultado.getString("nombre_ejercicio"));
                view.setGrupoMuscular(resultado.getString("nombre_grupo_muscular"));
                // Los otros campos (series, reps, dia) quedan en null/0
                ejercicios.add(view);
            }
        } catch (Exception e) {
            System.err.println("Error al listar todos los ejercicios detallados: " + e.getMessage());
            throw new RuntimeException("Error al listar todos los ejercicios detallados", e);
        }
        return ejercicios;
    }

    public List<EjercicioDTO> listarTodosDTO() {
        List<EjercicioDTO> ejercicios = new ArrayList<>();
        String sql = "SELECT e.id, e.nombre, e.dificultad, e.grupo_muscular_id, gm.nombre as grupo_nombre " +
                "FROM ejercicio e " +
                "LEFT JOIN grupo_muscular gm ON e.grupo_muscular_id = gm.id " +
                "ORDER BY e.nombre";
        Connection conn = databaseConection.getInstancia().getConnection();

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                EjercicioDTO dto = new EjercicioDTO();
                dto.setId(rs.getInt("id"));
                dto.setNombre(rs.getString("nombre"));
                dto.setDificultad(rs.getString("dificultad"));
                dto.setGrupoMuscularId(rs.getInt("grupo_muscular_id"));
                dto.setGrupoMuscularNombre(rs.getString("grupo_nombre"));
                ejercicios.add(dto);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar EjercicioDTOs: " + e.getMessage());
        }
        return ejercicios;
    }




}
