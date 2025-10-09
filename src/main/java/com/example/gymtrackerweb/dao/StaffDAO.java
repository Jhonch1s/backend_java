package com.example.gymtrackerweb.dao;



import com.example.gymtrackerweb.db.databaseConection;
import com.example.gymtrackerweb.model.Staff;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StaffDAO {

//    private dbLogger logger = new dbLogger();

    public Staff iniciarSesion(String usuario) {
        String sql = "SELECT * FROM staff WHERE usuario_login = ?";
        Connection conexion = databaseConection.getInstancia().getConnection();
        try{
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, usuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Si existe, llenar el objeto Staff con los datos del usuario
                    Staff usuarioEncontrado = new Staff();
                    usuarioEncontrado.setId(rs.getInt("id"));
                    usuarioEncontrado.setUsuarioLogin(rs.getString("usuario_login"));
                    usuarioEncontrado.setRol(rs.getInt("rol"));
                    usuarioEncontrado.setEstado(rs.getInt("estado"));
                    return usuarioEncontrado;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al iniciar sesion", e);
        }
        // Si no encontró nada, devolvemos null
        return null;
    }

    public void crearStaff(Staff s) {
        String sql = "INSERT INTO staff (usuario_login, nombre_completo, rol, estado) VALUES (?,?,?,?)";
        try {
            Connection con = databaseConection.getInstancia().getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, s.getUsuarioLogin());
            ps.setString(2, s.getNombreCompleto());
            ps.setInt(3, s.getRol());
            ps.setInt(4, s.getEstado());

            ps.executeUpdate();

//            logger.insertarLog(dbLogger.Accion.INSERT, "Staff creado: " + s.getUsuarioLogin());
            System.out.println("Staff creado correctamente.");

        } catch (Exception e) {
            System.out.println("Error al crear staff: " + e.getMessage());
        }
    }

    public void editarStaff(Staff s) {
        String sql = "UPDATE staff SET nombre_completo = ?, rol = ?, estado = ? WHERE id = ?";
        try {
            Connection con = databaseConection.getInstancia().getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, s.getNombreCompleto());
            ps.setInt(2, s.getRol());
            ps.setInt(3, s.getEstado());
            ps.setInt(4, s.getId());

            int filas = ps.executeUpdate();

            if (filas > 0) {
//                logger.insertarLog(dbLogger.Accion.UPDATE, "Staff con id = " + s.getId() + " editado");
                System.out.println("Staff actualizado correctamente.");
            } else {
                System.out.println("No se encontro al staff con id = " + s.getId());
            }

        } catch (Exception e) {
            System.out.println("Error al editar staff: " + e.getMessage());
        }
    }

    public List<Staff> listarStaff() {
        List<Staff> staffList = new ArrayList<>();
        String consulta = "SELECT * FROM staff";
        try {
            Statement st = databaseConection.getInstancia().getConnection().createStatement();
            ResultSet rs = st.executeQuery(consulta);

            while (rs.next()) {
                Staff staff = new Staff(
                        rs.getInt("id"),
                        rs.getString("usuario_login"),
                        rs.getString("nombre_completo"),
                        rs.getInt("rol"),
                        rs.getInt("estado")
                );
                staffList.add(staff);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return staffList;
    }

    public void eliminarStaff(int id) {
        String consulta = "DELETE FROM staff WHERE id = ?";
        try {
            PreparedStatement ps = databaseConection.getInstancia().getConnection().prepareStatement(consulta);
            ps.setInt(1, id);
            ps.executeUpdate();
//            logger.insertarLog(dbLogger.Accion.DELETE, "Staff con id = " + id + " eliminado");
            System.out.println("Staff eliminado correctamente ");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public boolean existeStaffPorId(int id) {
        String consulta = "SELECT 1 FROM staff WHERE id = ?";
        try {
            PreparedStatement ps = databaseConection.getInstancia().getConnection().prepareStatement(consulta);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            System.out.println("Error al buscar Staff por ID: " + e.getMessage());
        }
        return false;
    }

    public boolean existeStaffPorUsuarioLogin(String uslogin) {
        String consulta = "SELECT 1 FROM staff WHERE usuario_login = ?";
        try {
            PreparedStatement ps = databaseConection.getInstancia().getConnection().prepareStatement(consulta);
            ps.setString(1,uslogin);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            System.out.println("Error al buscar Staff por Usuario Login: " + e.getMessage());
        }
        return false;
    }


}
