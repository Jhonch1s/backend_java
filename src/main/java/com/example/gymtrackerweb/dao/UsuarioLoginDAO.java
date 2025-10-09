package com.example.gymtrackerweb.dao;


import com.example.gymtrackerweb.db.databaseConection;
import com.example.gymtrackerweb.model.UsuarioLogin;
import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;

public class UsuarioLoginDAO {
    private final Connection conn;

    public UsuarioLoginDAO(Connection conn) {
        this.conn = conn;
    }

    public UsuarioLoginDAO(){
        this.conn = databaseConection.getInstancia().getConnection();
    }

    // Insertar nuevo login
    public boolean insertar(String tipo, String id, String password) throws SQLException {
        String sql = "INSERT INTO usuario_login (id_login_staff, id_login_cliente, password) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            if ("cliente".equalsIgnoreCase(tipo)) {
                stmt.setNull(1, Types.INTEGER);
                stmt.setString(2, id);
            }else if ("staff".equalsIgnoreCase(tipo)) {
                stmt.setInt(1, Integer.parseInt(id));
                stmt.setNull(2, Types.VARCHAR);
            }else{
                throw new IllegalArgumentException("Tipo invalido, debe ser cliente o staff");
            }
            String pass = BCrypt.hashpw(password, BCrypt.gensalt(12));
            stmt.setString(3, pass);
            return stmt.executeUpdate() == 1;
        }
    }

    // Buscar por ID
    public UsuarioLogin buscarPorId(int idLogin) throws SQLException {
        String sql = "SELECT * FROM usuario_login WHERE id_login = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idLogin);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new UsuarioLogin(
                        rs.getInt("id_login"),
                        rs.getObject("id_login_staff", Integer.class),
                        rs.getString("id_login_cliente"),
                        rs.getString("password")
                );
            }
        }
        return null;
    }

    // Validar login por ID y contraseña
    public String validarLoginFlexible(String identificador, String plainPassword) throws SQLException {
        // Intentar primero como cliente (id_login_cliente)
        String sqlCliente = "SELECT password FROM usuario_login WHERE id_login_cliente = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sqlCliente)) {
            stmt.setString(1, identificador);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String hashed = rs.getString("password");
                if (BCrypt.checkpw(plainPassword, hashed)) return "cliente";
            }
        }

        // Si no se encontró como cliente, intentar como staff (id_login_staff)
        String sqlStaff = "SELECT id FROM staff WHERE usuario_login = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sqlStaff)) {
            stmt.setString(1, identificador); // Asume que el ID de staff es numérico
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int idConsulta = rs.getInt("id");
                String sqlPass = "SELECT password FROM usuario_login WHERE id_login_staff = ?";
                try (PreparedStatement stmt2 = conn.prepareStatement(sqlPass)) {
                    stmt2.setInt(1, idConsulta); // Asume que el ID de staff es numérico
                    ResultSet rs2 = stmt2.executeQuery();
                    if (rs2.next()) {
                        String hashed = rs2.getString("password");
                        if (BCrypt.checkpw(plainPassword, hashed)) return "staff";
                    }
                }
            }
        }


        // No se encontró en ninguna de las dos
        return "";
    }

    // Eliminar login
    public boolean eliminar(int idLogin) throws SQLException {
        String sql = "DELETE FROM usuario_login WHERE id_login = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idLogin);
            return stmt.executeUpdate() == 1;
        }
    }
}