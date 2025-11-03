package com.example.gymtrackerweb.dao;

import com.example.gymtrackerweb.db.databaseConection;
import com.example.gymtrackerweb.dto.*;
import com.example.gymtrackerweb.model.Rutina;
import com.example.gymtrackerweb.model.enums.Objetivo;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RutinaDAO {
    public int agregarRutina(Rutina r) {
        String sql = "INSERT INTO rutina (nombre, objetivo, duracion_semanas) VALUES (?, ?, ?)";
        int generatedId = -1;
        Connection conexion = databaseConection.getInstancia().getConnection();
        try (PreparedStatement sentencia = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            sentencia.setString(1, r.getNombre());
            sentencia.setString(2, r.getObjetivo().name());
            if (r.getDuracionSemanas() > 0) {
                sentencia.setInt(3, r.getDuracionSemanas());
            } else {
                sentencia.setNull(3, java.sql.Types.TINYINT);
            }

            int filasAfectadas = sentencia.executeUpdate();

            if (filasAfectadas > 0) {
                try (ResultSet generatedKeys = sentencia.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        generatedId = generatedKeys.getInt(1);
                    }
                }
            }
        } catch(Exception err){
            System.err.println("Error al agregar rutina: " + err.getMessage());
        }
        return generatedId;
    }

    public boolean eliminarRutina(int id) {
        String sql = "DELETE FROM rutina WHERE id = ?";
        Connection conexion = databaseConection.getInstancia().getConnection();
        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, id);

            int filasAfectadas = sentencia.executeUpdate();

            return filasAfectadas > 0;

        } catch (Exception err) {
            System.out.println("Error al eliminar rutina: " + err.getMessage());
            return false;
        }
    }

    public void modificarRutina(Rutina r) {
        String sql = "UPDATE rutina SET nombre = ?, objetivo = ?, duracion_semanas = ? WHERE id = ?";
        Connection conexion = databaseConection.getInstancia().getConnection();
        try{
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

    public List<RutinaCard> ultimas3PorCliente(String idCliente) throws SQLException {
        final String sql = """
            SELECT
              r.id AS id_rutina,
              r.nombre AS nombre,
              rc.estado AS estado,
              rc.fecha_asignacion AS fecha_asignacion,
              CASE
                WHEN COUNT(DISTINCT gm.nombre) > 2 THEN
                  CONCAT(
                    SUBSTRING_INDEX(
                      GROUP_CONCAT(DISTINCT gm.nombre ORDER BY gm.nombre SEPARATOR ', '),
                      ', ', 2
                    ),
                    ', ...'
                  )
                ELSE
                  GROUP_CONCAT(DISTINCT gm.nombre ORDER BY gm.nombre SEPARATOR ', ')
              END AS grupos_top3
            FROM rutina_cliente rc
            JOIN rutina r               ON r.id = rc.id_rutina
            LEFT JOIN detalle_rutina dr ON dr.id_rutina = r.id
            LEFT JOIN ejercicio e        ON e.id = dr.id_ejercicio
            LEFT JOIN grupo_muscular gm  ON gm.id = e.grupo_muscular_id
            WHERE rc.id_cliente = ?
            GROUP BY rc.id, r.id, r.nombre, rc.estado, rc.fecha_asignacion
            ORDER BY rc.fecha_asignacion DESC, rc.id DESC
            LIMIT 3
            """;
        Connection conn = databaseConection.getInstancia().getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                List<RutinaCard> out = new ArrayList<>();
                while (rs.next()) {
                    String grupos = rs.getString("grupos_top3");
                    if (grupos == null || grupos.isBlank()) grupos = "—";
                    LocalDate f = null;
                    Date d = rs.getDate("fecha_asignacion");
                    if (d != null) f = d.toLocalDate();
                    out.add(new RutinaCard(
                            rs.getInt("id_rutina"),
                            rs.getString("nombre"),
                            grupos,
                            rs.getString("estado"),
                            f
                    ));
                }
                return out;
            }
        }
    }

    public List<EjercicioAsignadoDTO> listarEjerciciosAsignados(int rutinaId) {
        List<EjercicioAsignadoDTO> asignados = new ArrayList<>();
        String sql = "SELECT " +
                "  dr.series, dr.repeticiones, drd.dia_semana, " +
                "  e.id as ej_id, e.nombre as ej_nombre, e.dificultad as ej_dificultad, " +
                "  e.grupo_muscular_id as ej_grupo_id, gm.nombre as ej_grupo_nombre " +
                "FROM detalle_rutina dr " +
                "JOIN detalle_rutina_dia drd ON dr.id = drd.id_detalle_rutina " +
                "JOIN ejercicio e ON dr.id_ejercicio = e.id " +
                "LEFT JOIN grupo_muscular gm ON e.grupo_muscular_id = gm.id " +
                "WHERE dr.id_rutina = ? " +
                "ORDER BY drd.dia_semana";
        Connection conn = databaseConection.getInstancia().getConnection();
        try (
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, rutinaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    EjercicioAsignadoDTO dto = new EjercicioAsignadoDTO();
                    dto.setSeries(rs.getInt("series"));
                    dto.setRepeticiones(rs.getInt("repeticiones"));
                    dto.setDiaSemana(rs.getString("dia_semana"));

                    EjercicioDTO ejDto = new EjercicioDTO();
                    ejDto.setId(rs.getInt("ej_id"));
                    ejDto.setNombre(rs.getString("ej_nombre"));
                    ejDto.setDificultad(rs.getString("ej_dificultad"));
                    ejDto.setGrupoMuscularId(rs.getInt("ej_grupo_id"));
                    ejDto.setGrupoMuscularNombre(rs.getString("ej_grupo_nombre"));

                    dto.setEjercicio(ejDto);
                    asignados.add(dto);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al listar ejercicios asignados: " + e.getMessage());
        }
        return asignados;
    }

    public void guardarRutinaCompleta(GuardarRutinaPayloadDTO payload) throws SQLException {
        Connection conn = null;
        PreparedStatement psDeleteDias = null;
        PreparedStatement psDeleteDetalles = null;
        PreparedStatement psInsertDetalle = null;
        PreparedStatement psInsertDia = null;

        String sqlDeleteDias = "DELETE FROM detalle_rutina_dia WHERE id_detalle_rutina IN " +
                "(SELECT id FROM detalle_rutina WHERE id_rutina = ?)";
        String sqlDeleteDetalles = "DELETE FROM detalle_rutina WHERE id_rutina = ?";
        String sqlInsertDetalle = "INSERT INTO detalle_rutina (id_rutina, id_ejercicio, series, repeticiones) VALUES (?, ?, ?, ?)";
        String sqlInsertDia = "INSERT INTO detalle_rutina_dia (id_detalle_rutina, dia_semana) VALUES (?, ?)";
        conn = databaseConection.getInstancia().getConnection();
        try {
            conn.setAutoCommit(false);

            psDeleteDias = conn.prepareStatement(sqlDeleteDias);
            psDeleteDias.setInt(1, payload.getRutinaId());
            psDeleteDias.executeUpdate();

            psDeleteDetalles = conn.prepareStatement(sqlDeleteDetalles);
            psDeleteDetalles.setInt(1, payload.getRutinaId());
            psDeleteDetalles.executeUpdate();

            psInsertDetalle = conn.prepareStatement(sqlInsertDetalle, Statement.RETURN_GENERATED_KEYS);
            psInsertDia = conn.prepareStatement(sqlInsertDia);

            for (Map.Entry<String, List<GuardarRutinaPayloadDTO.EjercicioGuardadoDTO>> entry : payload.getDias().entrySet()) {
                String dia = entry.getKey();
                List<GuardarRutinaPayloadDTO.EjercicioGuardadoDTO> ejercicios = entry.getValue();

                for (GuardarRutinaPayloadDTO.EjercicioGuardadoDTO ej : ejercicios) {

                    psInsertDetalle.setInt(1, payload.getRutinaId());
                    psInsertDetalle.setInt(2, ej.getEjercicioId());
                    psInsertDetalle.setInt(3, ej.getSeries());
                    psInsertDetalle.setInt(4, ej.getRepeticiones());
                    psInsertDetalle.executeUpdate();

                    int nuevoDetalleId = -1;
                    try (ResultSet generatedKeys = psInsertDetalle.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            nuevoDetalleId = generatedKeys.getInt(1);
                        } else {
                            throw new SQLException("No se pudo obtener el ID de detalle_rutina.");
                        }
                    }

                    psInsertDia.setInt(1, nuevoDetalleId);
                    psInsertDia.setString(2, dia);
                    psInsertDia.executeUpdate();
                }
            }

            conn.commit();

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Error en rollback: " + ex.getMessage());
                }
            }
            System.err.println("Error al guardar rutina completa: " + e.getMessage());
            throw e;
        } finally {
            if (psDeleteDias != null) psDeleteDias.close();
            if (psDeleteDetalles != null) psDeleteDetalles.close();
            if (psInsertDetalle != null) psInsertDetalle.close();
            if (psInsertDia != null) psInsertDia.close();
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }

    }

    public void asignarNuevaRutinaCliente(Connection conn, String clienteId, int rutinaId, LocalDate fechaAsignacion) throws SQLException {

        String sqlInsertNew = "INSERT INTO rutina_cliente (id_cliente, id_rutina, fecha_asignacion, estado) VALUES (?, ?, ?, 'activa')";

            try (PreparedStatement psInsert = conn.prepareStatement(sqlInsertNew)) {
                psInsert.setString(1, clienteId);
                psInsert.setInt(2, rutinaId);
                psInsert.setDate(3, java.sql.Date.valueOf(fechaAsignacion));
                psInsert.executeUpdate();
            } catch (SQLException e) {
            System.err.println("Error al asignar rutina: " + e.getMessage());
            throw e;
        }
    }

    public List<RutinaAsignadaConIdDTO> listarRutinasActivasPorCliente(String clienteId) {
        List<RutinaAsignadaConIdDTO> asignadas = new ArrayList<>();

        String sql = "SELECT rc.id as asignacion_id, r.id as rutina_id, r.nombre as rutina_nombre, rc.fecha_asignacion, rc.estado " +
                "FROM rutina_cliente rc " +
                "JOIN rutina r ON rc.id_rutina = r.id " +
                "WHERE rc.id_cliente = ? AND rc.estado = 'activa' " +
                "ORDER BY rc.fecha_asignacion DESC";

        Connection conn = databaseConection.getInstancia().getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, clienteId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {

                    asignadas.add(new RutinaAsignadaConIdDTO(
                            rs.getInt("asignacion_id"),
                            rs.getInt("rutina_id"),
                            rs.getString("rutina_nombre"),
                            rs.getDate("fecha_asignacion"),
                            rs.getString("estado")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al listar rutinas activas por cliente: " + e.getMessage());
        }
        return asignadas;
    }

    public boolean eliminarAsignacion(int asignacionId) {
        String sql = "DELETE FROM rutina_cliente WHERE id = ?";
        Connection conn = databaseConection.getInstancia().getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, asignacionId);
            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar asignación: " + e.getMessage());
            return false;
        }
    }

    public void eliminarAsignacion(Connection conn, int asignacionId) throws SQLException {
        String sql = "DELETE FROM rutina_cliente WHERE id = ?";
        // Usamos la conexión que nos pasa el servlet
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, asignacionId);
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas == 0) {
                System.err.println("Advertencia: No se encontró la asignacionId " + asignacionId + " para eliminar.");
            }
        } catch (SQLException e) {
            System.err.println("Error al eliminar asignación (dentro de TX): " + e.getMessage());
            throw e;
        }
    }



}
