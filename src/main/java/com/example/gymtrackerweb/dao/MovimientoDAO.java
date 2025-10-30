package com.example.gymtrackerweb.dao;
import com.example.gymtrackerweb.db.databaseConection;
import com.example.gymtrackerweb.dto.ClienteMin;
import com.example.gymtrackerweb.dto.*;
import com.example.gymtrackerweb.model.Movimiento;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.sql.*;

public class MovimientoDAO {
    //inserts
    public void insertarMovimiento(Movimiento m){
        final String sql = """
        INSERT INTO movimiento
            (id_staff, fecha_hora, importe, medio_pago_id, tipo_cliente_id, origen_id, id_membresia, id_cliente)
        VALUES
            (?, COALESCE(?, CURRENT_TIMESTAMP), ?, ?, ?, ?, ?, ?)
        """;


        Connection conexion = databaseConection.getInstancia().getConnection();
        try(PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, m.getIdStaff());

            // <-- clave: null seguro aquí; COALESCE pone CURRENT_TIMESTAMP
            if (m.getFechaHora() == null) {
                ps.setTimestamp(2, null);
            } else {
                ps.setTimestamp(2, Timestamp.valueOf(m.getFechaHora()));
            }

            ps.setBigDecimal(3, m.getImporte());
            ps.setByte(4, m.getMedioPagoID());
            ps.setByte(5, m.getTipoClienteID());
            ps.setByte(6, m.getOrigenId());
            ps.setObject(7, m.getIdMembresia(), Types.INTEGER); // puede ser null
            ps.setString(8, m.getIdCliente());                            // puede ser null

            ps.executeUpdate();
        }catch(SQLException ex){
            System.out.print(ex.getMessage());
        }
    }

    //Sumar importes de cierto período de tiempo
    public BigDecimal sumarImportes(LocalDateTime desde, LocalDateTime hasta) throws SQLException{
        String sentencia = "SELECT COALESCE(SUM(importe), 0) AS total FROM movimiento WHERE " +
                "fecha_hora >= ? AND fecha_hora < ?";
        Connection conexion = databaseConection.getInstancia().getConnection();
        try(
        PreparedStatement ps = conexion.prepareStatement(sentencia);){
            ps.setTimestamp(1, Timestamp.valueOf(desde));
            ps.setTimestamp(2, Timestamp.valueOf(hasta));
            try (ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    return rs.getBigDecimal("total");
                }
            }
        }
        return BigDecimal.ZERO;
    }

    // Obtener movimientos por Staff
    public List<Movimiento> listarPorStaff(Integer idStaff)throws SQLException{
        List<Movimiento> lista = new ArrayList<>();
        String sql ="SELECT * FROM movimiento WHERE id_staff = ?  ORDER BY fecha_hora DESC";
        Connection conexion = databaseConection.getInstancia().getConnection();
        try(
        PreparedStatement ps = conexion.prepareStatement(sql);){
            ps.setInt(1, idStaff);
            try (ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    lista.add(mapMovimiento(rs));
                }
            }
        }
        return lista;
    }

    //Polimorfismo aqui con diferentes parametros de entrada al metodo
    public List<Movimiento> listarPorStaff(Integer idStaff, LocalDateTime desde, LocalDateTime hasta) throws SQLException {
        List<Movimiento> lista = new ArrayList<>();
        final String sql = "SELECT * FROM movimiento WHERE id_staff = ? AND fecha_hora >= ? AND fecha_hora < ? ORDER BY fecha_hora DESC";
        try (Connection c = databaseConection.getInstancia().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idStaff);
            ps.setTimestamp(2, Timestamp.valueOf(desde));
            ps.setTimestamp(3, Timestamp.valueOf(hasta));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapMovimiento(rs));
            }
        }
        return lista;
    }

    public List<Movimiento> listarPorRango(LocalDateTime desde, LocalDateTime hasta) throws SQLException {
        List<Movimiento> lista = new ArrayList<>();
        final String sql = "SELECT * FROM movimiento WHERE fecha_hora >= ? AND fecha_hora < ? ORDER BY fecha_hora DESC";
        Connection c = databaseConection.getInstancia().getConnection();
        try (
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(desde));
            ps.setTimestamp(2, Timestamp.valueOf(hasta));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapMovimiento(rs));
            }
        }
        return lista;
    }

    // Obtener todos los movmimentos
    public List<Movimiento> listarTodos() throws SQLException {
        List<Movimiento> lista = new ArrayList<>();
        String sentencia = "SELECT * FROM movimiento ORDER BY id_mov DESC";
        Connection conexion = databaseConection.getInstancia().getConnection();
        try (
        PreparedStatement pw = conexion.prepareStatement(sentencia);
        ResultSet rs = pw.executeQuery()) {
            while (rs.next()) {
                lista.add(mapMovimiento(rs));
            }
        }
        return lista;
    }

    //Eliminar
    public void eliminarMovimiento (Long idMov) throws SQLException{
        String sentencia = "DELETE  FROM movimiento WHERE id_mov = ?";
        Connection conexion = databaseConection.getInstancia().getConnection();
        try (
        PreparedStatement pw = conexion.prepareStatement(sentencia);){
            pw.setLong (1, idMov);
            pw.executeUpdate();
        }catch(SQLException ex){
            System.out.print(ex.getMessage());
        }
    }

    //Nuevos: para movimientoView para que se vean nombres y no ids
    public List<MovimientoView> listarViewTodos() throws SQLException {
        final String sql = """
            SELECT m.id_mov,
                   m.fecha_hora,
                   m.importe,
                   s.nombre_completo  AS staff_nombre,
                   mp.nombre AS medio_pago_nombre,
                   tc.nombre AS tipo_cliente_nombre,
                   o.nombre  AS origen_nombre,
                   m.id_membresia,
                   CASE WHEN c.ci IS NOT NULL THEN CONCAT(c.nombre, ' ', c.apellido) ELSE NULL END AS cliente_nombre
            FROM movimiento m
            LEFT JOIN staff s ON s.id = m.id_staff
            LEFT JOIN medio_pago mp ON mp.id = m.medio_pago_id
            LEFT JOIN tipo_cliente tc ON tc.id = m.tipo_cliente_id
            LEFT JOIN origen_movimiento o ON o.id = m.origen_id
            LEFT JOIN cliente c ON c.ci = m.id_cliente
            ORDER BY m.fecha_hora DESC
        """;
        Connection cn = databaseConection.getInstancia().getConnection();
        try (
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<MovimientoView> lista = new ArrayList<>();
            while (rs.next()) lista.add(mapMovimientoView(rs));
            return lista;
        }
    }

    public List<MovimientoView> listarViewPorRango(LocalDateTime desde, LocalDateTime hasta) throws SQLException {
        final String sql = """
            SELECT m.id_mov,
                   m.fecha_hora,
                   m.importe,
                   s.nombre_completo  AS staff_nombre,
                   mp.nombre AS medio_pago_nombre,
                   tc.nombre AS tipo_cliente_nombre,
                   o.nombre  AS origen_nombre,
                   m.id_membresia,
                   CASE WHEN c.ci IS NOT NULL THEN CONCAT(c.nombre, ' ', c.apellido) ELSE NULL END AS cliente_nombre
            FROM movimiento m
            LEFT JOIN staff s ON s.id = m.id_staff
            LEFT JOIN medio_pago mp ON mp.id = m.medio_pago_id
            LEFT JOIN tipo_cliente tc ON tc.id = m.tipo_cliente_id
            LEFT JOIN origen_movimiento o ON o.id = m.origen_id
            LEFT JOIN cliente c ON c.ci = m.id_cliente
            WHERE m.fecha_hora >= ? AND m.fecha_hora < ?
            ORDER BY m.fecha_hora DESC
        """;
        Connection cn = databaseConection.getInstancia().getConnection();
        try (
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(desde));
            ps.setTimestamp(2, Timestamp.valueOf(hasta));

            try (ResultSet rs = ps.executeQuery()) {
                List<MovimientoView> lista = new ArrayList<>();
                while (rs.next()) lista.add(mapMovimientoView(rs));
                return lista;
            }
        }
    }

    public List<MovimientoView> listarViewPorStaff(int idStaff) throws SQLException {
        final String sql = """
            SELECT m.id_mov,
                   m.fecha_hora,
                   m.importe,
                   s.nombre_completo  AS staff_nombre,
                   mp.nombre AS medio_pago_nombre,
                   tc.nombre AS tipo_cliente_nombre,
                   o.nombre  AS origen_nombre,
                   m.id_membresia,
                   CASE WHEN c.ci IS NOT NULL THEN CONCAT(c.nombre, ' ', c.apellido) ELSE NULL END AS cliente_nombre
            FROM movimiento m
            LEFT JOIN staff s  ON s.id = m.id_staff
            LEFT JOIN medio_pago mp ON mp.id = m.medio_pago_id
            LEFT JOIN tipo_cliente tc ON tc.id = m.tipo_cliente_id
            LEFT JOIN origen_movimiento o  ON o.id = m.origen_id
            LEFT JOIN cliente c  ON c.ci = m.id_cliente
            WHERE m.id_staff = ?
            ORDER BY m.fecha_hora DESC
        """;

        try (Connection cn = databaseConection.getInstancia().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idStaff);

            try (ResultSet rs = ps.executeQuery()) {
                List<MovimientoView> lista = new ArrayList<>();
                while (rs.next()) lista.add(mapMovimientoView(rs));
                return lista;
            }
        }
    }

    public List<MovimientoView> listarViewPorStaff(int idStaff, LocalDateTime desde, LocalDateTime hasta) throws SQLException {
        final String sql = """
            SELECT m.id_mov,
                   m.fecha_hora,
                   m.importe,
                   s.nombre_completo  AS staff_nombre,
                   mp.nombre AS medio_pago_nombre,
                   tc.nombre AS tipo_cliente_nombre,
                   o.nombre  AS origen_nombre,
                   m.id_membresia,
                   CASE WHEN c.ci IS NOT NULL THEN CONCAT(c.nombre, ' ', c.apellido) ELSE NULL END AS cliente_nombre
            FROM movimiento m
            LEFT JOIN staff s  ON s.id = m.id_staff
            LEFT JOIN medio_pago mp ON mp.id = m.medio_pago_id
            LEFT JOIN tipo_cliente tc ON tc.id = m.tipo_cliente_id
            LEFT JOIN origen_movimiento o ON o.id = m.origen_id
            LEFT JOIN cliente c ON c.ci = m.id_cliente
            WHERE m.id_staff = ? AND m.fecha_hora >= ? AND m.fecha_hora < ?
            ORDER BY m.fecha_hora DESC
        """;

        Connection cn = databaseConection.getInstancia().getConnection();
        try (
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idStaff);
            ps.setTimestamp(2, Timestamp.valueOf(desde));
            ps.setTimestamp(3, Timestamp.valueOf(hasta));

            try (ResultSet rs = ps.executeQuery()) {
                List<MovimientoView> lista = new ArrayList<>();
                while (rs.next()) lista.add(mapMovimientoView(rs));
                return lista;
            }
        }
    }

    public List<IdNombre> listarMediosPago() throws SQLException {
        final String sql = "SELECT id, nombre FROM medio_pago ORDER BY id";
        Connection c = databaseConection.getInstancia().getConnection();
        try (
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<IdNombre> out = new ArrayList<>();
            while (rs.next()) out.add(new IdNombre(rs.getInt("id"), rs.getString("nombre")));
            return out;
        }
    }

    public List<IdNombre> listarTiposCliente() throws SQLException {
        final String sql = "SELECT id, nombre FROM tipo_cliente ORDER BY id";
        Connection c = databaseConection.getInstancia().getConnection();
        try (
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<IdNombre> out = new ArrayList<>();
            while (rs.next()) out.add(new IdNombre(rs.getInt("id"), rs.getString("nombre")));
            return out;
        }
    }

    public List<IdNombre> listarOrigenesMovimiento() throws SQLException {
        final String sql = "SELECT id, nombre FROM origen_movimiento ORDER BY id";
        Connection c = databaseConection.getInstancia().getConnection();
        try (
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<IdNombre> out = new ArrayList<>();
            while (rs.next()) out.add(new IdNombre(rs.getInt("id"), rs.getString("nombre")));
            return out;
        }
    }
    public List<ClienteMin> listarClientesMin() throws SQLException {
        final String sql = "SELECT ci, CONCAT(nombre,' ',apellido) AS nom FROM cliente ORDER BY nom";
        Connection c = databaseConection.getInstancia().getConnection();
        try (
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<ClienteMin> out = new ArrayList<>();
            while (rs.next()) out.add(new ClienteMin(rs.getString("ci"), rs.getString("nom")));
            return out;
        }
    }

    public List<MembresiaMin> listarMembresiasMin() throws SQLException {
        final String sql = """
        SELECT me.id, me.id_cliente AS ci, CONCAT(c.nombre,' ',c.apellido) AS nom
        FROM membresia me
        JOIN cliente c ON c.ci = me.id_cliente
        ORDER BY me.id DESC
        """;
        Connection c = databaseConection.getInstancia().getConnection();
        try (
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<MembresiaMin> out = new ArrayList<>();
            while (rs.next()) out.add(new MembresiaMin(rs.getInt("id"), rs.getString("ci"), rs.getString("nom")));
            return out;
        }
    }

    //Transformamos de resultset a objeto movimiento
    private Movimiento mapMovimiento (ResultSet rs) throws SQLException {
        Movimiento mov = new Movimiento();
        mov.setIdMov(rs.getLong("id_mov"));
        mov.setIdStaff(rs.getInt("id_staff"));
        mov.setFechaHora(rs.getTimestamp("fecha_hora").toLocalDateTime());
        mov.setImporte(rs.getBigDecimal("importe"));
        mov.setMedioPagoID(rs.getByte("medio_pago_id"));
        mov.setTipoClienteID(rs.getByte("tipo_cliente_id"));
        mov.setOrigenId(rs.getByte("origen_id"));
        mov.setIdMembresia(rs.getInt("id_membresia"));
        mov.setIdCliente(rs.getString("id_cliente"));
        return mov;
    }

    private MovimientoView mapMovimientoView(ResultSet rs) throws SQLException {
        long idMov = rs.getLong("id_mov");
        Timestamp ts = rs.getTimestamp("fecha_hora");
        LocalDateTime fechaHora = (ts == null ? null : ts.toLocalDateTime());

        return new MovimientoView(
                idMov,
                fechaHora,
                rs.getBigDecimal("importe"),
                rs.getString("staff_nombre"),
                rs.getString("medio_pago_nombre"),
                rs.getString("tipo_cliente_nombre"),
                rs.getString("origen_nombre"),
                rs.getObject("id_membresia", Integer.class),
                rs.getString("cliente_nombre")
        );
    }

    public boolean membresiaPerteneceACliente(int idMembresia, String ciCliente) {
        final String sql = "SELECT 1 FROM membresia WHERE id = ? AND id_cliente = ? LIMIT 1";
        Connection cn = databaseConection.getInstancia().getConnection();
        try (
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idMembresia);
            ps.setString(2, ciCliente);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // true si existe esa relación
            }
        } catch (Exception e) {
            System.out.println("Error verificando membresía/cliente: " + e.getMessage());
            return false;
        }
    };

    public List<MovimientoViewExtra> listarMovimientosFiltrados(
            String staffLogin,
            String ciCliente,
            LocalDate desde,
            LocalDate hasta,
            int limit,
            int offset
    ) throws SQLException {

        List<MovimientoViewExtra> lista = new ArrayList<>();

        String sql = """
        SELECT
            m.id_mov,
            m.fecha_hora,
            m.importe,
            m.id_membresia,
            s.nombre_completo  AS staff_nombre,
            s.usuario_login    AS staff_login,
            mp.nombre          AS medio_pago_nombre,
            tc.nombre          AS tipo_cliente_nombre,
            o.nombre           AS origen_nombre,
            c.ci               AS cliente_ci,
            CASE WHEN c.ci IS NOT NULL THEN CONCAT(c.nombre, ' ', c.apellido) ELSE NULL END AS cliente_nombre,
            p.nombre           AS plan_nombre
        FROM movimiento m
        LEFT JOIN staff              s  ON s.id  = m.id_staff
        LEFT JOIN medio_pago         mp ON mp.id = m.medio_pago_id
        LEFT JOIN tipo_cliente       tc ON tc.id = m.tipo_cliente_id
        LEFT JOIN origen_movimiento  o  ON o.id  = m.origen_id
        LEFT JOIN cliente            c  ON c.ci  = m.id_cliente
        LEFT JOIN membresia          me ON me.id = m.id_membresia
        LEFT JOIN plan               p  ON p.id  = me.id_plan
        WHERE 1=1
        """;

        if (staffLogin != null && !staffLogin.isBlank()) sql += " AND s.usuario_login = ?";
        if (ciCliente != null && !ciCliente.isBlank())   sql += " AND m.id_cliente = ?";
        if (desde != null)                                sql += " AND m.fecha_hora >= ?";
        if (hasta != null)                                sql += " AND m.fecha_hora < ?";

        sql += " ORDER BY m.fecha_hora DESC LIMIT ? OFFSET ?";

        try (Connection cn = databaseConection.getInstancia().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            int idx = 1;
            if (staffLogin != null && !staffLogin.isBlank()) ps.setString(idx++, staffLogin);
            if (ciCliente != null && !ciCliente.isBlank())   ps.setString(idx++, ciCliente);
            if (desde != null)                                ps.setTimestamp(idx++, Timestamp.valueOf(desde.atStartOfDay()));
            if (hasta != null)                                ps.setTimestamp(idx++, Timestamp.valueOf(hasta.plusDays(1).atStartOfDay()));

            ps.setInt(idx++, limit);
            ps.setInt(idx, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MovimientoViewExtra v = new MovimientoViewExtra();
                    v.setIdMov(rs.getLong("id_mov"));
                    Timestamp ts = rs.getTimestamp("fecha_hora");
                    v.setFechaHora(ts == null ? null : ts.toLocalDateTime());
                    v.setImporte(rs.getBigDecimal("importe"));
                    v.setIdMembresia(rs.getObject("id_membresia", Integer.class));
                    v.setStaffNombre(rs.getString("staff_nombre"));
                    v.setMedioPagoNombre(rs.getString("medio_pago_nombre"));
                    v.setTipoClienteNombre(rs.getString("tipo_cliente_nombre"));
                    v.setOrigenNombre(rs.getString("origen_nombre"));
                    v.setClienteCi(rs.getString("cliente_ci"));
                    v.setClienteNombre(rs.getString("cliente_nombre"));
                    v.setPlanNombre(rs.getString("plan_nombre"));
                    lista.add(v);
                }
            }
        }
        return lista;
    }

    public int contarMovimientosFiltrados(
            String staffLogin,
            String ciCliente,
            LocalDate desde,
            LocalDate hasta
    ) throws SQLException {

        String sql = """
        SELECT COUNT(*) AS total
        FROM movimiento m
        LEFT JOIN staff             s  ON s.id  = m.id_staff
        LEFT JOIN cliente           c  ON c.ci  = m.id_cliente
        WHERE 1=1
        """;

        if (staffLogin != null && !staffLogin.isBlank()) sql += " AND s.usuario_login = ?";
        if (ciCliente != null && !ciCliente.isBlank())   sql += " AND m.id_cliente = ?";
        if (desde != null)                                sql += " AND m.fecha_hora >= ?";
        if (hasta != null)                                sql += " AND m.fecha_hora < ?";

        try (Connection cn = databaseConection.getInstancia().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            int idx = 1;
            if (staffLogin != null && !staffLogin.isBlank()) ps.setString(idx++, staffLogin);
            if (ciCliente != null && !ciCliente.isBlank())   ps.setString(idx++, ciCliente);
            if (desde != null)                                ps.setTimestamp(idx++, Timestamp.valueOf(desde.atStartOfDay()));
            if (hasta != null)                                ps.setTimestamp(idx++, Timestamp.valueOf(hasta.plusDays(1).atStartOfDay()));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("total");
            }
        }
        return 0;
    }

    public BigDecimal sumarImportesFiltrados(
            String staffLogin,
            String ciCliente,
            LocalDate desde,
            LocalDate hasta
    ) throws SQLException {

        String sql = """
        SELECT COALESCE(SUM(m.importe), 0) AS total
        FROM movimiento m
        LEFT JOIN staff s ON s.id = m.id_staff
        WHERE 1=1
    """;

        if (staffLogin != null && !staffLogin.isBlank()) sql += " AND s.usuario_login = ?";
        if (ciCliente != null && !ciCliente.isBlank())   sql += " AND m.id_cliente = ?";
        if (desde != null)                                sql += " AND m.fecha_hora >= ?";
        if (hasta != null)                                sql += " AND m.fecha_hora < ?";

        try (Connection cn = databaseConection.getInstancia().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            int idx = 1;
            if (staffLogin != null && !staffLogin.isBlank()) ps.setString(idx++, staffLogin);
            if (ciCliente != null && !ciCliente.isBlank())   ps.setString(idx++, ciCliente);
            if (desde != null)                                ps.setTimestamp(idx++, Timestamp.valueOf(desde.atStartOfDay()));
            if (hasta != null)                                ps.setTimestamp(idx++, Timestamp.valueOf(hasta.plusDays(1).atStartOfDay()));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getBigDecimal("total");
            }
        }
        return BigDecimal.ZERO;
    }
}
