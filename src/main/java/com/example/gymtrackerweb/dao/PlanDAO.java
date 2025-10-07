package com.example.gymtrackerweb.dao;
import com.example.gymtrackerweb.db.databaseConection;
import com.example.gymtrackerweb.model.Plan;
import com.example.gymtrackerweb.model.UnidadDuracion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class PlanDAO {

    public void agregarPlan(Plan p) {
        final String sql = "INSERT INTO plan (nombre, valor, duracion_total, duracion_unidad_id, urlImagen, estado) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        Connection conexion = databaseConection.getInstancia().getConnection();
        try {
            PreparedStatement sentencia = conexion.prepareStatement(sql);

            sentencia.setString(1, p.getNombre());
            sentencia.setBigDecimal(2, p.getValor()); // BigDecimal para DECIMAL(10,2)
            sentencia.setShort(3, p.getDuracionTotal()); // smallint
            sentencia.setByte(4, p.getDuracionUnidadId()); // tinyint
            sentencia.setString(5, p.getUrlImagen());
            sentencia.setBoolean(6, p.isEstado()); // tinyint(1) como boolean

            sentencia.execute();
            System.out.println("Plan agregado exitosamente");

        } catch (Exception e) {
            System.out.println("Error al insertar plan: " + e.getMessage());
        }
    }

    public Plan buscarPorId(int id) {
        final String sql = "SELECT id, nombre, valor, duracion_total, duracion_unidad_id, urlImagen, estado " +
                "FROM plan WHERE id = ?";
            Connection cn = databaseConection.getInstancia().getConnection();
        try {
            PreparedStatement sentencia = cn.prepareStatement(sql);
            sentencia.setInt(1, id);

            try (ResultSet rs = sentencia.executeQuery()) {
                if (rs.next()) {
                    return mapPlan(rs);
                }
            }
        } catch (Exception e) {
            System.out.println("Error al buscar plan por id: " + e.getMessage());
        }
        return null;
    }

    public List<Plan> listarTodos() {
        final String sql = """
                SELECT p.id, p.nombre, p.valor, p.duracion_total,
                       p.duracion_unidad_id,           -- ← vuelve el ID
                       u.nombre AS duracion_unidad_nombre,
                       p.urlImagen, p.estado
                FROM plan p
                JOIN duracion_unidad u ON u.id = p.duracion_unidad_id
                ORDER BY p.id
            """;
        List<Plan> lista = new ArrayList<>();
        Connection cn = databaseConection.getInstancia().getConnection();
        try (
                PreparedStatement ps = cn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Plan plan = mapPlan(rs);
                try {
                    plan.setDuracionUnidadNombre(rs.getString("duracion_unidad_nombre"));
                } catch (SQLException ignore) {}
                lista.add(plan);
            }
        } catch (Exception e) {
            System.out.println("Error al listar planes: " + e.getMessage());
        }
        return lista;
    }

    public List<Plan> listarActivos() {
        final String sql = """
            SELECT p.id, p.nombre, p.valor, p.duracion_total,
                   p.duracion_unidad_id,
                   u.nombre AS duracion_unidad_nombre,
                   p.urlImagen, p.estado
            FROM plan p
            JOIN duracion_unidad u ON u.id = p.duracion_unidad_id
            WHERE p.estado = 1
            ORDER BY p.id
        """;
        List<Plan> lista = new ArrayList<>();
        Connection cn = databaseConection.getInstancia().getConnection();
        try (
                PreparedStatement ps = cn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Plan plan = mapPlan(rs);
                try {
                    plan.setDuracionUnidadNombre(rs.getString("duracion_unidad_nombre"));
                } catch (SQLException ignore) {}
                lista.add(plan);
            }
        } catch (Exception e) {
            System.out.println("Error al listar planes activos: " + e.getMessage());
        }
        return lista;
    }

    public List<Plan> listarInactivos() {
        final String sql = """
            SELECT p.id, p.nombre, p.valor, p.duracion_total,
                   p.duracion_unidad_id,
                   u.nombre AS duracion_unidad_nombre,
                   p.urlImagen, p.estado
            FROM plan p
            JOIN duracion_unidad u ON u.id = p.duracion_unidad_id
            WHERE p.estado = 0
            ORDER BY p.id
        """;
        List<Plan> lista = new ArrayList<>();
        Connection cn = databaseConection.getInstancia().getConnection();
        try (
                PreparedStatement ps = cn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Plan plan = mapPlan(rs);
                try {
                    plan.setDuracionUnidadNombre(rs.getString("duracion_unidad_nombre"));
                } catch (SQLException ignore) {}
                lista.add(plan);
            }
        } catch (Exception e) {
            System.out.println("Error al listar planes inactivos: " + e.getMessage());
        }
        return lista;
    }


    //updates
    public int modificarPlan(Plan p) {
        final String sql = "UPDATE plan SET nombre = ?, valor = ?, duracion_total = ?, duracion_unidad_id = ?, " +
                "urlImagen = ?, estado = ? WHERE id = ?";
        Connection cn = databaseConection.getInstancia().getConnection();
        try (
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, p.getNombre());
            ps.setBigDecimal(2, p.getValor());
            ps.setShort(3, p.getDuracionTotal());
            ps.setByte(4, p.getDuracionUnidadId());
            ps.setString(5, p.getUrlImagen());
            ps.setBoolean(6, p.isEstado());
            ps.setInt(7, p.getId());

            int filas = ps.executeUpdate();
            if (filas > 0) {
                System.out.println("Plan modificado correctamente.");
            } else {
                System.out.println("No se encontró plan con id=" + p.getId());
            }
            return filas;

        } catch (Exception e) {
            System.out.println("Error al modificar plan: " + e.getMessage());
            return 0;
        }
    }

    //Por si lo llegamos a usar
    public boolean actualizarEstado(int id, boolean estado) {
        final String sql = "UPDATE plan SET estado = ? WHERE id = ?";
        Connection cn = databaseConection.getInstancia().getConnection();
        try (
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setBoolean(1, estado);
            ps.setInt(2, id);

            int filas = ps.executeUpdate();
            if (filas <= 0) {
                System.out.println("No se ha podido actualizar el estado del plan.");
                return false;
            }
            return true;

        } catch (Exception e) {
            System.out.println("Error al actualizar estado del plan: " + e.getMessage());
            return false;
        }
    }
    //hola
    //delete
    public boolean eliminarPlan(int id) {
        final String sql = "DELETE FROM plan WHERE id = ?";
        Connection cn = databaseConection.getInstancia().getConnection();
        try (
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);
            int filas = ps.executeUpdate();
            if (filas > 0) {
                System.out.println("Plan eliminado correctamente.");
                return true;
            } else {
                System.out.println("No se encontró plan con id=" + id);
                return  false;
            }

        } catch (SQLIntegrityConstraintViolationException e) {
            // Por si el plan está referenciado por otras tablas (FK)
            System.out.println("No se puede eliminar el plan (referenciado por otras entidades): " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.out.println("Error al eliminar plan: " + e.getMessage());
            return false;
        }
    }

    //parte de la corrección
    public List<UnidadDuracion> listarUnidadesDuracion() {
        String sql = "SELECT id, nombre FROM duracion_unidad ORDER BY id";
        List<UnidadDuracion> res = new ArrayList<>();
        Connection cn = databaseConection.getInstancia().getConnection();
        try (
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                UnidadDuracion u = new UnidadDuracion();
                u.setId(rs.getByte("id"));
                u.setNombre(rs.getString("nombre"));
                res.add(u);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error listando unidades de duración", e);
        }
        return res;
    }
    public boolean existeUnidadDuracion(byte id) {
        String sql = "SELECT 1 FROM duracion_unidad WHERE id = ?";
        Connection cn = databaseConection.getInstancia().getConnection();
        try (
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setByte(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error validando unidad de duración", e);
        }
    }

    public String obtenerNombreUnidadDuracion(byte id) {
        String sql = "SELECT nombre FROM duracion_unidad WHERE id = ?";
        Connection cn = databaseConection.getInstancia().getConnection();
        try (
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setByte(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("nombre");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener nombre de unidad de duración", e);
        }
        return "Desconocido"; // Si no existe el ID
    }

    //transformarmos result de consulta a objeto
    private Plan mapPlan(ResultSet rs) throws SQLException {
        Plan p = new Plan();
        p.setId(rs.getInt("id"));
        p.setNombre(rs.getString("nombre"));
        p.setValor(rs.getBigDecimal("valor")); // BigDecimal
        p.setDuracionTotal(rs.getShort("duracion_total"));
        p.setDuracionUnidadId(rs.getByte("duracion_unidad_id"));
        p.setUrlImagen(rs.getString("urlImagen"));
        p.setEstado(rs.getBoolean("estado"));
        return p;
    }
}
