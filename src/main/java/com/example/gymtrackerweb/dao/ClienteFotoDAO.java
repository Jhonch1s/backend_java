package com.example.gymtrackerweb.dao;

import com.example.gymtrackerweb.db.databaseConection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public class ClienteFotoDAO {
    public Optional<String> obtenerUrlPorCliente(String ci) throws SQLException {
        String sql = "SELECT secure_url FROM cliente_foto WHERE id_cliente = ?";
        Connection con = databaseConection.getInstancia().getConnection();
        try (
             var ps = con.prepareStatement(sql)) {
            ps.setString(1, ci);
            try (var rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(rs.getString(1)) : Optional.empty();
            }
        }
    }

    /** inserta o reemplaza la url de foto del cliente */
    public void upsertUrl(String ci, String secureUrl) throws SQLException {
        String sql = """
            INSERT INTO cliente_foto (id_cliente, secure_url)
            VALUES (?, ?)
            ON DUPLICATE KEY UPDATE secure_url = VALUES(secure_url)
        """;
        Connection con = databaseConection.getInstancia().getConnection();
        try (
             var ps = con.prepareStatement(sql)) {
            ps.setString(1, ci);
            ps.setString(2, secureUrl);
            ps.executeUpdate();
        }
    }

    public void borrarPorCliente(String ci) throws SQLException {
        String sql = "DELETE FROM cliente_foto WHERE id_cliente = ?";
        Connection con = databaseConection.getInstancia().getConnection();
        try (
             var ps = con.prepareStatement(sql)) {
            ps.setString(1, ci);
            ps.executeUpdate();
        }
    }
}
