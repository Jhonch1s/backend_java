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
    public static record WeekActivity(LocalDate monday, boolean active) {} //lundes que representa la semana, active si entreno o no esa semana
    //Por lo investigado, usar un record es como un dto pero con menos codigo, genera constructor, getters, toString, etc.

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
                    return id;
                }
            }
        }
        throw new SQLException("No se obtuvo clave generada para registro_gym");
    }

    // actualizamos la hora de salida de una sesión (cierra la sesión) ya que por defecto está en null. */
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

    // Sesion abierta? (cerrar es null)
    public Optional<RegistroGym> findSesionAbierta(String ciCliente) throws SQLException {
        final String sql = """
        SELECT id_registro, ci_cliente, fecha, entrada, salida
        FROM registro_gym
        WHERE ci_cliente = ? AND salida IS NULL
        ORDER BY entrada DESC
        LIMIT 1
        """;
        Connection conn = databaseConection.getInstancia().getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ciCliente);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(RegistroGym.fromResultSet(rs));
                return Optional.empty();
            }
        }
    }

    public boolean tieneSesionAbierta(String ciCliente) throws SQLException {
        return findSesionAbierta(ciCliente).isPresent();
    }

    public int minutosTotalesMes(String ciCliente, LocalDate anyDayOfMonth) throws SQLException {
        LocalDate ini = anyDayOfMonth.withDayOfMonth(1);
        LocalDate fin = anyDayOfMonth.withDayOfMonth(anyDayOfMonth.lengthOfMonth());
        final String sql = """
        SELECT COALESCE(SUM(TIMESTAMPDIFF(MINUTE, entrada, salida)), 0)
        FROM registro_gym
        WHERE ci_cliente = ?
          AND fecha BETWEEN ? AND ?
          AND salida IS NOT NULL
        """;
        Connection conn = databaseConection.getInstancia().getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ciCliente);
            ps.setDate(2, Date.valueOf(ini));
            ps.setDate(3, Date.valueOf(fin));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

}
    // En RegistroGymDAO


    public List<WeekActivity> actividadSemanal(String ciCliente, int weeks, LocalDate hoyMvd) throws SQLException {
        // lunes de la semana actual
        int dow = hoyMvd.getDayOfWeek().getValue(); // 1..7 (1=lunes)
        LocalDate mondayThisWeek = hoyMvd.minusDays(dow - 1L);
        LocalDate mondayMin = mondayThisWeek.minusWeeks(weeks - 1L);

        final String sql = """
        WITH RECURSIVE weeks AS (
          SELECT ? AS monday
          UNION ALL
          SELECT DATE_ADD(monday, INTERVAL 7 DAY)
          FROM weeks
          WHERE monday < ?
        )
        SELECT
          w.monday AS monday,
          EXISTS (
            SELECT 1
            FROM registro_gym rg
            WHERE rg.ci_cliente = ?
              AND rg.salida IS NOT NULL
              AND rg.fecha BETWEEN w.monday AND DATE_ADD(w.monday, INTERVAL 6 DAY)
          ) AS active
        FROM weeks w
        ORDER BY w.monday
        """;
        //contamos solo sesiones cerradas, rg.salida IS NOT NULL
        Connection conn = databaseConection.getInstancia().getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(mondayMin));
            ps.setDate(2, Date.valueOf(mondayThisWeek));
            ps.setString(3, ciCliente);

            List<WeekActivity> out = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LocalDate monday = rs.getDate("monday").toLocalDate();
                    boolean active = rs.getBoolean("active");
                    out.add(new WeekActivity(monday, active));
                }
            }
            return out;
        }
    }

    //calcula rachas consecutivas desde semana actual para atras
    public static int calcularStreakSemanal(List<WeekActivity> weeksAsc, boolean ignoreCurrentIfInactive) {
        if (weeksAsc == null || weeksAsc.isEmpty()) return 0;
        int last = weeksAsc.size() - 1;
        if (ignoreCurrentIfInactive && !weeksAsc.get(last).active()) {
            last--;
        }
        int streak = 0;
        for (int i = last; i >= 0; i--) {
            if (weeksAsc.get(i).active()) streak++;
            else break;
        }
        return Math.max(streak, 0);
    }
}
