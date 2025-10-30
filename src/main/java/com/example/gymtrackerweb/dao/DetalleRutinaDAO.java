package com.example.gymtrackerweb.dao;

import com.example.gymtrackerweb.db.databaseConection;
import com.example.gymtrackerweb.model.DetalleRutina;
import com.example.gymtrackerweb.model.enums.DiaSemana; // Importamos el Enum

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException; // Importamos SQLException
import java.sql.Statement; // Importamos Statement para obtener ID generado
import java.util.ArrayList;
import java.util.List;

public class DetalleRutinaDAO {

    // Cambiado para devolver el ID generado
    public int agregarDetalle(DetalleRutina d) {
        String sql = "INSERT INTO detalle_rutina (id_ejercicio, id_rutina, series, repeticiones) VALUES (?, ?, ?, ?)";
        int generatedId = -1; // Valor por defecto si falla
        Connection conexion = databaseConection.getInstancia().getConnection();
        try (
             // Pedimos que nos devuelva las claves generadas
             PreparedStatement sentencia = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            sentencia.setInt(1, d.getId_ejercicio()); // Usar getters/setters estándar
            sentencia.setInt(2, d.getId_rutina());
            sentencia.setInt(3, d.getSeries());
            sentencia.setInt(4, d.getRepeticiones());

            int filasAfectadas = sentencia.executeUpdate();

            if (filasAfectadas > 0) {
                // Obtenemos el ID generado
                try (ResultSet generatedKeys = sentencia.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        generatedId = generatedKeys.getInt(1);
                        System.out.println("Detalle de rutina agregado correctamente con ID: " + generatedId);
                    } else {
                        System.err.println("Error: No se pudo obtener el ID generado al agregar detalle.");
                    }
                }
            } else {
                System.err.println("Error: No se insertó ninguna fila al agregar detalle.");
            }

        } catch (Exception err) {
            System.err.println("Error al agregar detalle de rutina: " + err.getMessage());
            // Considera relanzar o manejar mejor el error
        }
        return generatedId; // Devolvemos el ID
    }

    public void eliminarDetalle(int id) {
        // Al eliminar un detalle, también deberíamos eliminar sus días asociados
        // (La FK con ON DELETE CASCADE ya lo hace, pero es bueno tenerlo explícito si no)
        // eliminarDiasDeDetalle(id); // Descomentar si no usaste ON DELETE CASCADE

        String sql = "DELETE FROM detalle_rutina WHERE id = ?";
        Connection conexion = databaseConection.getInstancia().getConnection();
        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, id);
            int filasAfectadas = sentencia.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("Detalle de rutina eliminado correctamente (ID: " + id + ").");
            } else {
                System.out.println("No se encontró detalle de rutina para eliminar (ID: " + id + ").");
            }
        } catch (Exception err) {
            System.err.println("Error al eliminar detalle de rutina: " + err.getMessage());
        }
    }

    public void modificarDetalle(DetalleRutina d) {
        String sql = "UPDATE detalle_rutina SET id_ejercicio = ?, id_rutina = ?, series = ?, repeticiones = ? WHERE id = ?";
        Connection conexion = databaseConection.getInstancia().getConnection();
        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setInt(1, d.getId_ejercicio());
            sentencia.setInt(2, d.getId_rutina());
            sentencia.setInt(3, d.getSeries());
            sentencia.setInt(4, d.getRepeticiones());
            sentencia.setInt(5, d.getId());

            int filas = sentencia.executeUpdate();

            if (filas > 0) {
                System.out.println("Detalle de rutina modificado correctamente.");
            } else {
                System.out.println("No se encontró detalle con ID: " + d.getId());
            }
        } catch (Exception err) {
            System.err.println("Error al modificar detalle de rutina: " + err.getMessage());
        }
    }

    // ListarDetalles y ListarDetallesPorRutina (sin cambios necesarios ahora)
    public List<DetalleRutina> listarDetalles() {
        List<DetalleRutina> lista = new ArrayList<>();
        String sql = "SELECT * FROM detalle_rutina";
        Connection conexion = databaseConection.getInstancia().getConnection();
        try (PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery()) {

            while (resultado.next()) {
                DetalleRutina d = new DetalleRutina();
                d.setId(resultado.getInt("id"));
                d.setId_ejercicio(resultado.getInt("id_ejercicio"));
                d.setId_rutina(resultado.getInt("id_rutina"));
                d.setSeries(resultado.getInt("series"));
                d.setRepeticiones(resultado.getInt("repeticiones"));

                lista.add(d);
            }
        } catch (Exception e) {
            System.err.println("Error al listar detalles de rutina: " + e.getMessage());
        }
        return lista;
    }

    public List<DetalleRutina> listarDetallesPorRutina(int idRutina) {
        List<DetalleRutina> lista = new ArrayList<>();
        String sql = "SELECT * FROM detalle_rutina WHERE id_rutina = ?";
        Connection conexion = databaseConection.getInstancia().getConnection();
        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, idRutina); // Se debe setear el parámetro antes de ejecutar

            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    DetalleRutina d = new DetalleRutina();
                    d.setId(resultado.getInt("id"));
                    d.setId_ejercicio(resultado.getInt("id_ejercicio"));
                    d.setId_rutina(resultado.getInt("id_rutina"));
                    d.setSeries(resultado.getInt("series"));
                    d.setRepeticiones(resultado.getInt("repeticiones"));

                    lista.add(d);
                }
            }
        } catch (Exception e) { // Capturar SQLException específicamente si es posible
            System.err.println("Error al listar detalles por rutina: " + e.getMessage());
            // Considera relanzar una excepción personalizada o RuntimeException
            throw new RuntimeException("Error al listar detalles por rutina", e);
        }
        return lista;
    }


    public void agregarDiaADetalle(int idDetalleRutina, DiaSemana dia) {
        // Primero, verificamos si ya existe para evitar duplicados (opcional pero buena idea)
        if (existeDiaParaDetalle(idDetalleRutina, dia)) {
            System.out.println("El día " + dia + " ya está asignado al detalle " + idDetalleRutina + ". No se agrega duplicado.");
            return;
        }

        String sql = "INSERT INTO detalle_rutina_dia (id_detalle_rutina, dia_semana) VALUES (?, ?)";
        Connection conexion = databaseConection.getInstancia().getConnection();
        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, idDetalleRutina);
            sentencia.setString(2, dia.name()); // Convertimos el Enum a String ("LUNES")

            sentencia.executeUpdate();
            System.out.println("Día " + dia + " agregado al detalle " + idDetalleRutina + ".");
        } catch (Exception err) {
            System.err.println("Error al agregar día a detalle de rutina: " + err.getMessage());
        }
    }

    public void eliminarDiasDeDetalle(int idDetalleRutina) {
        String sql = "DELETE FROM detalle_rutina_dia WHERE id_detalle_rutina = ?";
        Connection conexion = databaseConection.getInstancia().getConnection();
        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, idDetalleRutina);
            int filasAfectadas = sentencia.executeUpdate();
            System.out.println(filasAfectadas + " días eliminados para el detalle " + idDetalleRutina + ".");
        } catch (Exception err) {
            System.err.println("Error al eliminar días de detalle de rutina: " + err.getMessage());
        }
    }

    public void eliminarDiaEspecificoDeDetalle(int idDetalleRutina, DiaSemana dia) {
        String sql = "DELETE FROM detalle_rutina_dia WHERE id_detalle_rutina = ? AND dia_semana = ?";
        Connection conexion = databaseConection.getInstancia().getConnection();
        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, idDetalleRutina);
            sentencia.setString(2, dia.name()); // Convertir Enum a String para la consulta
            int filasAfectadas = sentencia.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("Día " + dia + " eliminado para el detalle " + idDetalleRutina + ".");
            } else {
                System.out.println("No se encontró el día " + dia + " para el detalle " + idDetalleRutina + ".");
            }
        } catch (Exception err) {
            System.err.println("Error al eliminar día específico de detalle de rutina: " + err.getMessage());
        }
    }


    public List<DiaSemana> listarDiasPorDetalle(int idDetalleRutina) {
        List<DiaSemana> dias = new ArrayList<>();
        String sql = "SELECT dia_semana FROM detalle_rutina_dia WHERE id_detalle_rutina = ?";
        Connection conexion = databaseConection.getInstancia().getConnection();
        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, idDetalleRutina);
            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    String diaStr = resultado.getString("dia_semana");
                    DiaSemana diaEnum = DiaSemana.fromString(diaStr); // Usamos el helper del Enum
                    if (diaEnum != null) {
                        dias.add(diaEnum);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error al listar días por detalle de rutina: " + e.getMessage());
        }
        return dias;
    }

    private boolean existeDiaParaDetalle(int idDetalleRutina, DiaSemana dia) {
        String sql = "SELECT COUNT(*) FROM detalle_rutina_dia WHERE id_detalle_rutina = ? AND dia_semana = ?";
        Connection conexion = databaseConection.getInstancia().getConnection();
        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, idDetalleRutina);
            sentencia.setString(2, dia.name());

            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    return resultado.getInt(1) > 0;
                }
            }
        } catch (Exception e) {
            System.err.println("Error verificando existencia de día para detalle: " + e.getMessage());
        }
        return false; // Asumir que no existe si hay error
    }

}