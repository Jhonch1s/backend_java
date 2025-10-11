package com.example.gymtrackerweb.dao;

import com.example.gymtrackerweb.db.databaseConection;
import com.example.gymtrackerweb.model.RegistroGym;

import java.sql.*;
import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RegistroGymDAO {

    private static final ZoneId MONTEVIDEO = ZoneId.of("America/Montevideo");

    public RegistroGymDAO() {

    }

    // helpers
    private static LocalDate firstDayOfMonth(LocalDate anyDay) {
        return anyDay.with(TemporalAdjusters.firstDayOfMonth());
    }
    private static LocalDate lastDayOfMonth(LocalDate anyDay) {
        return anyDay.with(TemporalAdjusters.lastDayOfMonth());
    }

    //crud basico
    public int insert(RegistroGym r) throws SQLException {
        final String sql = "INSERT INTO registro_gym (ci_cliente, entrada, salida) VALUES (?, ?, ?)";
        Connection conn= databaseConection.getInstancia().getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, r.getCiCliente());
            ps.setTimestamp(2, Timestamp.valueOf(r.getEntrada()));
            if (r.getSalida() != null) {
                ps.setTimestamp(3, Timestamp.valueOf(r.getSalida()));
            } else {
                ps.setNull(3, Types.TIMESTAMP);
            }
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    r.setIdRegistro(id);
                    // 'fecha' la calcula la BD (GENERATED STORED). Si querés, la podés refrescar con findById.
                    return id;
                }
            }
        }
        throw new SQLException("No se obtuvo clave generada para registro_gym");
    }

    // Aquí actualizamos la hora de salida de una sesión (cierra la sesión) ya que por defecto está en null. */
    public boolean updateSalida(int idRegistro, LocalDateTime salida) throws SQLException {
        final String sql = "UPDATE registro_gym SET salida = ? WHERE id_registro = ?";
        Connection conn= databaseConection.getInstancia().getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            if (salida != null) {
                ps.setTimestamp(1, Timestamp.valueOf(salida));
            } else {
                ps.setNull(1, Types.TIMESTAMP);
            }
            ps.setInt(2, idRegistro);
            return ps.executeUpdate() > 0;
        }
    }

    // buscamos por id
    public Optional<RegistroGym> findById(int idRegistro) throws SQLException {
        final String sql = """
            SELECT id_registro, ci_cliente, fecha, entrada, salida
            FROM registro_gym
            WHERE id_registro = ?
            """;
        Connection conn= databaseConection.getInstancia().getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idRegistro);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(RegistroGym.fromResultSet(rs));
                return Optional.empty();
            }
        }
    }

    // listamos todos los registros de un cliente en un mes (ordenado por fecha)
    public List<RegistroGym> findByClienteAndMes(String ciCliente, YearMonth ym) throws SQLException {
        LocalDate ini = ym.atDay(1);
        LocalDate fin = ym.atEndOfMonth();
        final String sql = """
            SELECT id_registro, ci_cliente, fecha, entrada, salida
            FROM registro_gym
            WHERE ci_cliente = ?
              AND fecha BETWEEN ? AND ?
            ORDER BY entrada ASC
            """;
        Connection conn= databaseConection.getInstancia().getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ciCliente);
            ps.setDate(2, Date.valueOf(ini));
            ps.setDate(3, Date.valueOf(fin));
            try (ResultSet rs = ps.executeQuery()) {
                List<RegistroGym> out = new ArrayList<>();
                while (rs.next()) out.add(RegistroGym.fromResultSet(rs));
                return out;
            }
        }
    }

    // Para el dashboard

    // Días distintos entrenados en el mes contando solo sesiones cerradas
    public int contarDiasEntrenadosMes(String ciCliente, LocalDate anyDayOfMonth) throws SQLException {
        LocalDate ini = firstDayOfMonth(anyDayOfMonth);
        LocalDate fin = lastDayOfMonth(anyDayOfMonth);
        final String sql = """
            SELECT COUNT(DISTINCT fecha)
            FROM registro_gym
            WHERE ci_cliente = ?
              AND fecha BETWEEN ? AND ?
              AND salida IS NOT NULL
            """;
        Connection conn= databaseConection.getInstancia().getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ciCliente);
            ps.setDate(2, Date.valueOf(ini));
            ps.setDate(3, Date.valueOf(fin));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    // Total de entrenamientos en el mes, solo cerradas.
    public int totalSesionesMes(String ciCliente, LocalDate anyDayOfMonth) throws SQLException {
        LocalDate ini = firstDayOfMonth(anyDayOfMonth);
        LocalDate fin = lastDayOfMonth(anyDayOfMonth);
        final String sql = """
            SELECT COUNT(*)
            FROM registro_gym
            WHERE ci_cliente = ?
              AND fecha BETWEEN ? AND ?
              AND salida IS NOT NULL
            """;
        Connection conn= databaseConection.getInstancia().getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ciCliente);
            ps.setDate(2, Date.valueOf(ini));
            ps.setDate(3, Date.valueOf(fin));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    //Total de entrenamientos desde que inició!
    public int totalSesionesHistorico(String ciCliente) throws SQLException {
        final String sql = """
        SELECT COUNT(*)
        FROM registro_gym
        WHERE ci_cliente = ?
          AND salida IS NOT NULL
        """;
        Connection conn= databaseConection.getInstancia().getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ciCliente);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }


    //Minutos promedio por sesión en el mes, redondeado.
    public int minutosPromedioPorSesionMes(String ciCliente, LocalDate anyDayOfMonth) throws SQLException {
        LocalDate ini = firstDayOfMonth(anyDayOfMonth);
        LocalDate fin = lastDayOfMonth(anyDayOfMonth);
        final String sql = """
            SELECT ROUND(AVG(TIMESTAMPDIFF(MINUTE, entrada, salida)))
            FROM registro_gym
            WHERE ci_cliente = ?
              AND fecha BETWEEN ? AND ?
              AND salida IS NOT NULL
            """;
        Connection conn= databaseConection.getInstancia().getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ciCliente);
            ps.setDate(2, Date.valueOf(ini));
            ps.setDate(3, Date.valueOf(fin));
            try (ResultSet rs = ps.executeQuery()) {
                int avg = rs.next() ? rs.getInt(1) : 0;
                if (rs.wasNull()) return 0; // sin filas -> AVG NULL
                return Math.max(avg, 0);
            }
        }
    }

    // QUIEN SABE SI USAREMOS ESTO de los rangos
    //cuenta dias entre rango de fechas
    public int contarDiasEntrenadosRango(String ciCliente, LocalDate ini, LocalDate fin) throws SQLException {
        final String sql = """
            SELECT COUNT(DISTINCT fecha)
            FROM registro_gym
            WHERE ci_cliente = ?
              AND fecha BETWEEN ? AND ?
              AND salida IS NOT NULL
            """;
        Connection conn= databaseConection.getInstancia().getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ciCliente);
            ps.setDate(2, Date.valueOf(ini));
            ps.setDate(3, Date.valueOf(fin));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    public int totalSesionesRango(String ciCliente, LocalDate ini, LocalDate fin) throws SQLException {
        final String sql = """
            SELECT COUNT(*)
            FROM registro_gym
            WHERE ci_cliente = ?
              AND fecha BETWEEN ? AND ?
              AND salida IS NOT NULL
            """;
        Connection conn= databaseConection.getInstancia().getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ciCliente);
            ps.setDate(2, Date.valueOf(ini));
            ps.setDate(3, Date.valueOf(fin));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    public int minutosPromedioPorSesionRango(String ciCliente, LocalDate ini, LocalDate fin) throws SQLException {
        final String sql = """
            SELECT ROUND(AVG(TIMESTAMPDIFF(MINUTE, entrada, salida)))
            FROM registro_gym
            WHERE ci_cliente = ?
              AND fecha BETWEEN ? AND ?
              AND salida IS NOT NULL
            """;
        Connection conn= databaseConection.getInstancia().getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ciCliente);
            ps.setDate(2, Date.valueOf(ini));
            ps.setDate(3, Date.valueOf(fin));
            try (ResultSet rs = ps.executeQuery()) {
                int avg = rs.next() ? rs.getInt(1) : 0;
                if (rs.wasNull()) return 0;
                return Math.max(avg, 0);
            }
        }
    }
}
