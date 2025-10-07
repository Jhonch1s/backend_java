package com.example.gymtrackerweb.dao;

import com.example.gymtrackerweb.db.databaseConection;
import com.example.gymtrackerweb.model.MovimientoDetalle;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.sql.*;


public class MovimientoDetalleDAO {


    public List<MovimientoDetalle> listarDetallePorStaff(Integer idStaff) throws SQLException {
        final String sql = """
        SELECT
            m.id_mov                    AS idMov,
            s.nombre_completo                    AS staffNombre,
            m.fecha_hora                AS fechaHora,
            m.importe                   AS importe,
            mp.nombre                   AS medioPagoNombre,
            tc.nombre                   AS tipoClienteNombre,
            o.nombre                    AS origenNombre,
            m.id_membresia              AS idMembresia,
            m.id_cliente                AS idCliente,
            c.nombre                    AS clienteNombre
        FROM movimiento m
        LEFT JOIN staff        s  ON s.id          = m.id_staff
        LEFT JOIN medio_pago   mp ON mp.id         = m.medio_pago_id
        LEFT JOIN tipo_cliente tc ON tc.id         = m.tipo_cliente_id
        LEFT JOIN origen_movimiento       o  ON o.id          = m.origen_id
        LEFT JOIN cliente      c  ON c.ci  = m.id_cliente
        WHERE m.id_staff = ?
        ORDER BY m.fecha_hora DESC
    """;

        List<MovimientoDetalle> lista = new ArrayList<>();
        try (Connection conn = databaseConection.getInstancia().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idStaff);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapMovimientoDetalle(rs));
                }
            }
        }
        return lista;
    }


    private MovimientoDetalle mapMovimientoDetalle (ResultSet rs )throws SQLException{
        MovimientoDetalle m = new MovimientoDetalle();
        m.setIdMembresia(rs.getInt("idMembresia"));
        Timestamp fechaHora = rs.getTimestamp("fechaHora");
        m.setFechaHora(fechaHora.toLocalDateTime());
        m.setImporte(rs.getBigDecimal("importe"));
        m.setStaffNombre(rs.getString("staffNombre"));
        m.setMedioPagoNombre(rs.getString("medioPagoNombre"));
        m.setTipoClienteNombre(rs.getString("tipoClienteNombre"));
        m.setOrigenNombre(rs.getString("origenNombre"));
        m.setIdMembresia(rs.getInt("idMembresia"));
        m.setIdCliente(rs.getString("idCliente"));
        m.setClienteNombre(rs.getString("clienteNombre"));
        return m;
    }
}
