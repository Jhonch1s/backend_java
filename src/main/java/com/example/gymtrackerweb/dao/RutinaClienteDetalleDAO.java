package com.example.gymtrackerweb.dao;

import com.example.gymtrackerweb.db.databaseConection;
import com.example.gymtrackerweb.model.RutinaClienteDetalle;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RutinaClienteDetalleDAO {

    public List<RutinaClienteDetalle> listarTodos() throws SQLException {
        final String sql = """
            SELECT
                rc.id                         AS id,
                rc.id_cliente                 AS idCliente,
                c.nombre                      AS clienteNombre,
                rc.id_rutina                  AS idRutina,
                r.nombre                      AS rutinaNombre,
                rc.fecha_asignacion           AS fechaAsignacion,
                rc.estado                     AS estado
            FROM rutina_cliente rc
            JOIN cliente c ON c.ci = rc.id_cliente
            JOIN rutina  r ON r.id = rc.id_rutina
            ORDER BY rc.fecha_asignacion DESC
        """;

        List<RutinaClienteDetalle> lista = new ArrayList<>();
        try (Connection con = databaseConection.getInstancia().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(mapDetalle(rs));
            }
        }
        return lista;
    }

    public List<RutinaClienteDetalle> listarPorRutina(Integer idRutina) throws SQLException {
        final String sql = """
            SELECT
                rc.id                         AS id,
                rc.id_cliente                 AS idCliente,
                c.nombre                      AS clienteNombre,
                rc.id_rutina                  AS idRutina,
                r.nombre                      AS rutinaNombre,
                rc.fecha_asignacion           AS fechaAsignacion,
                rc.estado                     AS estado
            FROM rutina_cliente rc
            JOIN cliente c ON c.ci = rc.id_cliente
            JOIN rutina  r ON r.id = rc.id_rutina
            WHERE rc.id_rutina = ?
            ORDER BY rc.fecha_asignacion DESC
        """;

        List<RutinaClienteDetalle> lista = new ArrayList<>();
        try (Connection con = databaseConection.getInstancia().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(mapDetalle(rs));
            }
        }
        return lista;
    }

    public List<RutinaClienteDetalle> listarPorCliente(String idCliente) throws SQLException {
        final String sql = """
            SELECT
                rc.id                         AS id,
                rc.id_cliente                 AS idCliente,
                c.nombre                      AS clienteNombre,
                rc.id_rutina                  AS idRutina,
                r.nombre                      AS rutinaNombre,
                rc.fecha_asignacion           AS fechaAsignacion,
                rc.estado                     AS estado
            FROM rutina_cliente rc
            JOIN cliente c ON c.ci = rc.id_cliente
            JOIN rutina  r ON r.id = rc.id_rutina
            WHERE rc.id_cliente = ?
            ORDER BY rc.fecha_asignacion DESC
        """;

        List<RutinaClienteDetalle> lista = new ArrayList<>();
        try (Connection con = databaseConection.getInstancia().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(mapDetalle(rs));
            }
        }
        return lista;
    }

    private RutinaClienteDetalle mapDetalle(ResultSet rs) throws SQLException {
        RutinaClienteDetalle d = new RutinaClienteDetalle();
        d.setId(rs.getInt("id"));
        d.setIdCliente(rs.getString("idCliente"));
        d.setClienteNombre(rs.getString("clienteNombre"));
        d.setIdRutina(rs.getInt("idRutina"));
        d.setRutinaNombre(rs.getString("rutinaNombre"));
        d.setFechaAsignacion(rs.getDate("fechaAsignacion"));
        d.setEstado(rs.getString("estado"));
        return d;
    }
}