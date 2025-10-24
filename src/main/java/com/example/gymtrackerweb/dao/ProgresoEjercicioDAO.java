package com.example.gymtrackerweb.dao;


import com.example.gymtrackerweb.db.databaseConection;
import com.example.gymtrackerweb.dto.EjercicioConProgresoView;
import com.example.gymtrackerweb.dto.EjercicioMin;
import com.example.gymtrackerweb.dto.EjercicioMiniKpis;
import com.example.gymtrackerweb.dto.ProgresoCard;
import com.example.gymtrackerweb.model.ProgresoEjercicio;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProgresoEjercicioDAO {
    public static class ProgresoDato {
        private final Date fecha;
        private final BigDecimal peso;
        private final int repeticiones;

        public ProgresoDato(Date fecha, BigDecimal peso, int repeticiones) {
            this.fecha = fecha;
            this.peso = peso;
            this.repeticiones = repeticiones;
        }

        public Date getFecha() { return fecha; }
        public BigDecimal getPeso() { return peso; }
        public int getRepeticiones() { return repeticiones; }
    }
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
        String sql = "DELETE FROM progreso_ejercicio WHERE id_progreso = ?";
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
        String sql = "UPDATE progreso_ejercicio SET id_cliente = ?, id_ejercicio = ?, fecha = ?, peso_usado = ?, repeticiones = ? WHERE id_progreso = ?";
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
                progreso.setId(resultado.getInt("id_progreso"));
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

    public List<EjercicioConProgresoView> listarEjerciciosConProgreso(String clienteId, int rutinaID){
        List<EjercicioConProgresoView> progresoLista = new ArrayList<>();
        String sql = """
            SELECT\s
                r.id AS id_rutina,
                r.nombre AS nombre_rutina,
                e.id AS id_ejercicio,
                gm.nombre AS nombre_grupo_muscular,
                e.nombre AS nombre_ejercicio,
                pe.peso_usado,
                pe.repeticiones,
                pe.fecha AS fecha_ultimo_registro
            FROM rutina_cliente rc
            INNER JOIN rutina r ON rc.id_rutina = r.id
            INNER JOIN detalle_rutina dr ON r.id = dr.id_rutina
            INNER JOIN ejercicio e ON dr.id_ejercicio = e.id
            INNER JOIN grupo_muscular gm ON e.grupo_muscular_id = gm.id
            LEFT JOIN progreso_ejercicio pe ON pe.id_progreso = (
                SELECT pe2.id_progreso
                FROM progreso_ejercicio pe2
                WHERE pe2.id_ejercicio = e.id
                  AND pe2.id_cliente = rc.id_cliente
                ORDER BY pe2.fecha DESC
                LIMIT 1
            )
            WHERE rc.id_cliente = ?
              AND r.id = ?
        """;
        Connection conexion = databaseConection.getInstancia().getConnection();
        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, clienteId);
            sentencia.setInt(2, rutinaID);
            ResultSet resultado = sentencia.executeQuery();
            while(resultado.next()){
                EjercicioConProgresoView view = new EjercicioConProgresoView();
                view.setIdRutina(resultado.getInt("id_rutina"));
                view.setNombreRutina(resultado.getString("nombre_rutina"));
                view.setIdEjercicio(resultado.getInt("id_ejercicio"));
                view.setNombreEjercicio(resultado.getString("nombre_ejercicio"));
                view.setGrupoMuscular(resultado.getString("nombre_grupo_muscular"));
                view.setPesoUsado(resultado.getBigDecimal("peso_usado"));
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

    public List<ProgresoEjercicio> listarProgresoEjercicioDeUsuarioOrdenadoFecha(ProgresoEjercicio p,boolean Ascendiente){
        List<ProgresoEjercicio> progresoLista = new ArrayList<>();
        String orden = Ascendiente ? "ASC" : "DESC";
        String sql = "SELECT * FROM progreso_ejercicio WHERE id_cliente = ? ORDER BY fecha " + orden;
        Connection conexion = databaseConection.getInstancia().getConnection();
        try{
            PreparedStatement sentencia = conexion.prepareStatement(sql);
            sentencia.setInt(1, p.getIdCliente());
            ResultSet resultado = sentencia.executeQuery();
            while(resultado.next()){
                ProgresoEjercicio progreso = new ProgresoEjercicio();
                progreso.setId(resultado.getInt("id_progreso"));
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

    public List<ProgresoEjercicio> listarProgresoPorEjercicioYFecha(int idCliente, int idEjercicio, boolean ascendente) {
        List<ProgresoEjercicio> progresoLista = new ArrayList<>();
        String orden = ascendente ? "ASC" : "DESC";
        String sql = "SELECT * FROM progreso_ejercicio WHERE id_cliente = ? AND id_ejercicio = ? ORDER BY fecha " + orden;

        try (Connection conexion = databaseConection.getInstancia().getConnection();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, idCliente);
            sentencia.setInt(2, idEjercicio);

            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    ProgresoEjercicio progreso = new ProgresoEjercicio();
                    progreso.setId(resultado.getInt("id_progreso"));
                    progreso.setIdCliente(resultado.getInt("id_cliente"));
                    progreso.setIdEjercicio(resultado.getInt("id_ejercicio"));
                    progreso.setFecha(resultado.getDate("fecha"));
                    progreso.setPesoUsado(resultado.getInt("peso_usado"));
                    progreso.setRepeticiones(resultado.getInt("repeticiones"));
                    progresoLista.add(progreso);
                }
            }
        } catch (Exception err) {
            System.out.println("Error: " + err.getMessage());
        }
        return progresoLista;
    }
    //de: jhon, para listar ejercicios en select html
    public List<EjercicioMin> listarEjerciciosPorCliente(String idCliente) throws SQLException {
        String sql = """
        SELECT DISTINCT e.id, e.nombre
        FROM progreso_ejercicio p
        JOIN ejercicio e ON e.id = p.id_ejercicio
        WHERE p.id_cliente = ?
        ORDER BY e.nombre ASC
    """;
        Connection conn = databaseConection.getInstancia().getConnection();
        try (
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                List<EjercicioMin> out = new ArrayList<>();
                while (rs.next()) {
                    out.add(new EjercicioMin(rs.getInt("id"), rs.getString("nombre")));
                }
                return out;
            }
        }
    }

    //este SQL es duro.... por no hacerlo en front use IA porque está bravo
    // Fórmula e1RM (Epley): e1rm = peso * (1 + reps/30.0)
    public EjercicioMiniKpis miniKpis(String idCliente, int idEj) throws SQLException {
        final String sql = """
      WITH base AS (
        SELECT
          fecha,
          peso_usado AS kg,
          repeticiones AS reps,
          (peso_usado * (1 + repeticiones/30.0)) AS e1rm,
          (peso_usado * repeticiones) AS vol
        FROM progreso_ejercicio
        WHERE id_cliente = ? AND id_ejercicio = ?
      ),
      sema AS (
        SELECT YEARWEEK(fecha, 3) AS yw, AVG(e1rm) AS e1rm_avg
        FROM base
        GROUP BY YEARWEEK(fecha, 3)
      ),
      ult4 AS ( SELECT e1rm_avg FROM sema ORDER BY yw DESC LIMIT 4 ),
      prev4 AS ( SELECT e1rm_avg FROM sema ORDER BY yw DESC LIMIT 4 OFFSET 4 )
      SELECT
        (SELECT MAX(e1rm) FROM base)                                        AS best_e1rm,
        (SELECT (kg*reps) FROM base ORDER BY (kg*reps) DESC, fecha DESC LIMIT 1) AS best_set_vol,
        (SELECT kg        FROM base ORDER BY (kg*reps) DESC, fecha DESC LIMIT 1) AS best_set_kg,
        (SELECT reps      FROM base ORDER BY (kg*reps) DESC, fecha DESC LIMIT 1) AS best_set_reps,
        (SELECT COALESCE(SUM(vol),0) FROM base WHERE fecha >= (CURDATE() - INTERVAL 28 DAY)) AS vol4w,
        (SELECT COALESCE(AVG(e1rm_avg),NULL) FROM ult4)
        - (SELECT COALESCE(AVG(e1rm_avg),NULL) FROM prev4)                  AS delta_e1rm
    """;
        Connection conn = databaseConection.getInstancia().getConnection();
        try (
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, idCliente);
            ps.setInt(2, idEj);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return new EjercicioMiniKpis(0,0,0,0,null);

                double bestE1rm = getD0(rs, "best_e1rm");
                double bestSetKg = getD0(rs, "best_set_kg");
                int    bestSetReps = rs.getInt("best_set_reps");
                double bestSetVol = getD0(rs, "best_set_vol");
                double vol4w = getD0(rs, "vol4w");
                Double delta = getD(rs, "delta_e1rm");

                // Fallback para Δ si no hay 8 semanas
                if (delta == null) {
                    final String sqlFallback = """
                  SELECT
                    (SELECT AVG(peso_usado * (1 + repeticiones/30.0))
                       FROM progreso_ejercicio
                      WHERE id_cliente=? AND id_ejercicio=?
                      ORDER BY YEARWEEK(fecha,3) DESC LIMIT 1) AS last_avg,
                    (SELECT AVG(peso_usado * (1 + repeticiones/30.0))
                       FROM progreso_ejercicio
                      WHERE id_cliente=? AND id_ejercicio=?
                      ORDER BY YEARWEEK(fecha,3) ASC  LIMIT 1) AS first_avg
                """;
                    try (PreparedStatement ps2 = conn.prepareStatement(sqlFallback)) {
                        ps2.setString(1, idCliente); ps2.setInt(2, idEj);
                        ps2.setString(3, idCliente); ps2.setInt(4, idEj);
                        try (ResultSet r2 = ps2.executeQuery()) {
                            if (r2.next()) {
                                Double last  = getD(r2, "last_avg");
                                Double first = getD(r2, "first_avg");
                                if (last != null && first != null) delta = last - first;
                            }
                        }
                    }
                }

                return new EjercicioMiniKpis(bestE1rm,bestSetKg,bestSetReps,vol4w,delta);
            }
        }
    }
    // convertir DECIMAL a double
    private static Double getD(ResultSet rs, String col) throws SQLException {
        java.math.BigDecimal bd = rs.getBigDecimal(col);
        return (bd == null) ? null : bd.doubleValue();
    }

    private static double getD0(ResultSet rs, String col) throws SQLException {
        java.math.BigDecimal bd = rs.getBigDecimal(col);
        return (bd == null) ? 0.0 : bd.doubleValue();
    }

    public List<ProgresoDato> seriesForEjercicio(String ownerCi, int ejId, LocalDate from, LocalDate to) throws SQLException {
        final String sql =
                "SELECT fecha, peso_usado, repeticiones " +
                        "FROM progreso_ejercicio " +
                        "WHERE id_cliente = ? " +
                        "  AND id_ejercicio = ? " +
                        "  AND fecha BETWEEN ? AND ? " +
                        "ORDER BY fecha ASC, id_progreso ASC";

        List<ProgresoDato> out = new ArrayList<>();

        Connection conn = databaseConection.getInstancia().getConnection();
        try (
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, ownerCi);   
            ps.setInt(2, ejId);  
            ps.setDate(3, Date.valueOf(from));
            ps.setDate(4, Date.valueOf(to));      

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Date fecha = rs.getDate("fecha");
                    BigDecimal peso = rs.getBigDecimal("peso_usado");     // puede ser null
                    int reps = rs.getObject("repeticiones") == null
                            ? 0
                            : rs.getInt("repeticiones");

                    out.add(new ProgresoDato(fecha, peso, reps));
                }
            }
        }

        return out;
    }
    public List<ProgresoDato> seriesForEjercicio(Connection cn, String ownerCi, int ejId, LocalDate from, LocalDate to) throws SQLException {
        final String sql =
                "SELECT fecha, peso_usado, repeticiones " +
                        "FROM progreso_ejercicio " +
                        "WHERE id_cliente = ? " +
                        "  AND id_ejercicio = ? " +
                        "  AND fecha BETWEEN ? AND ? " +
                        "ORDER BY fecha ASC, id_progreso ASC";

        List<ProgresoDato> out = new ArrayList<>();

        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, ownerCi);
            ps.setInt(2, ejId);
            ps.setDate(3, Date.valueOf(from));
            ps.setDate(4, Date.valueOf(to));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Date fecha = rs.getDate("fecha");
                    BigDecimal peso = rs.getBigDecimal("peso_usado");
                    int reps = rs.getObject("repeticiones") == null ? 0 : rs.getInt("repeticiones");
                    out.add(new ProgresoDato(fecha, peso, reps));
                }
            }
        }

        return out;
    }


}
