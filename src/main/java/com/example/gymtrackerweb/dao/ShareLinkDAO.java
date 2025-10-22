package com.example.gymtrackerweb.dao;
// ShareLinkDAO.java

import com.example.gymtrackerweb.db.databaseConection;

import java.sql.*;
import java.time.LocalDateTime;

public class ShareLinkDAO {

    public String createToken(String ownerCi, LocalDateTime expiresAt) throws SQLException {
        String token = com.example.gymtrackerweb.utils.TokenGen.base62(22);

        final String sql = "INSERT INTO share_link (token, owner_ci, expires_at) VALUES (?, ?, ?)";
        Connection cn = databaseConection.getInstancia().getConnection();
        try (
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, token);
            ps.setString(2, ownerCi);
            if (expiresAt != null) {
                ps.setTimestamp(3, Timestamp.valueOf(expiresAt));
            } else {
                ps.setNull(3, Types.TIMESTAMP);
            }
            ps.executeUpdate();
        }
        return token;
    }

    public ShareLink findByToken(String token) throws SQLException {
        final String sql = "SELECT token, owner_ci, created_at, expires_at, is_revoked FROM share_link WHERE token = ?";
        Connection cn = databaseConection.getInstancia().getConnection();
        try (
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, token);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                ShareLink sl = new ShareLink();
                sl.token = rs.getString("token");
                sl.ownerCi = rs.getString("owner_ci");
                Timestamp ex = rs.getTimestamp("expires_at");
                sl.expiresAt = (ex != null) ? ex.toLocalDateTime() : null;
                sl.isRevoked = rs.getBoolean("is_revoked");
                return sl;
            }
        }
    }

    // POJO simple
    public static class ShareLink {
        public String token;
        public String ownerCi;
        public LocalDateTime expiresAt;
        public boolean isRevoked;
    }
}
