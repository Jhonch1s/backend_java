package com.example.gymtrackerweb.dao;
import com.example.gymtrackerweb.db.databaseConection;
import com.example.gymtrackerweb.dto.MembresiaPlanView;
import com.example.gymtrackerweb.model.Cliente;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.Date;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClienteDAO {
    public void agregarCliente(Cliente c) throws SQLException {
        // SQL para insertar en la tabla cliente
        final String sqlCliente = "INSERT INTO cliente (ci, email, nombre, apellido, ciudad, direccion, tel, pais, fecha_ingreso) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        // SQL para insertar en la tabla usuario_login
        final String sqlLogin = "INSERT INTO usuario_login (id_login_cliente, password) VALUES (?, ?)";

        Connection conexion = null; // Necesaria fuera del try para el rollback/finally
        boolean originalAutoCommitState = true; // Para restaurar el estado original
        conexion = databaseConection.getInstancia().getConnection();
        try {
            originalAutoCommitState = conexion.getAutoCommit(); // Guarda el estado actual
            // --- INICIO DE LA TRANSACCIÓN ---
            conexion.setAutoCommit(false);

            // --- 1. Insertar en la tabla 'cliente' ---
            try (PreparedStatement psCliente = conexion.prepareStatement(sqlCliente)) {
                psCliente.setString(1, c.getCi());
                psCliente.setString(2, c.getEmail());
                psCliente.setString(3, c.getNombre());
                psCliente.setString(4, c.getApellido());
                psCliente.setString(5, c.getCiudad());
                psCliente.setString(6, c.getDireccion());
                psCliente.setString(7, c.getTel());
                psCliente.setString(8, c.getPais());
                psCliente.setDate(9, c.getFechaIngreso());
                psCliente.executeUpdate();
                System.out.println("-> Registro 'cliente' insertado."); // Log simple
            } // psCliente se cierra automáticamente

            // --- 2. Hashear la contraseña (usando la CI como valor por defecto) ---
            // gensalt(12) genera una "sal" aleatoria con un costo de 12 (recomendado)
            String hashedPassword = BCrypt.hashpw(c.getCi(), BCrypt.gensalt(12));
            System.out.println("-> Contraseña hasheada (basada en CI)."); // Log simple

            // --- 3. Insertar en la tabla 'usuario_login' ---
            try (PreparedStatement psLogin = conexion.prepareStatement(sqlLogin)) {
                psLogin.setString(1, c.getCi());      // id_login_cliente es la CI
                psLogin.setString(2, hashedPassword); // La contraseña ya hasheada
                psLogin.executeUpdate();
                System.out.println("-> Registro 'usuario_login' insertado."); // Log simple
            } // psLogin se cierra automáticamente
            conexion.commit();
            System.out.println("Cliente y login agregados exitosamente (Commit realizado).");

        } catch (SQLException e) {
            System.err.println("¡ERROR en transacción! Haciendo rollback... Causa: " + e.getMessage());
            // --- ROLLBACK: Si algo falló, deshace todos los cambios ---
            if (conexion != null) {
                try {
                    conexion.rollback();
                    System.err.println("Rollback completado.");
                } catch (SQLException ex) {
                    System.err.println("¡ERROR al intentar hacer rollback! " + ex.getMessage());
                }
            }
            // Relanza la excepción original para que el Servlet la maneje
            throw e;
        } finally {
            // --- RESTAURAR AUTOCOMMIT ---
            // Es buena práctica restaurar el estado original de la conexión
            if (conexion != null) {
                try {
                    if (conexion.getAutoCommit() != originalAutoCommitState) {
                        conexion.setAutoCommit(originalAutoCommitState);
                        System.out.println("-> AutoCommit restaurado a: " + originalAutoCommitState);
                    }
                } catch (SQLException e) {
                    System.err.println("Error al restaurar AutoCommit: " + e.getMessage());
                }
            }
        }
    }

    public void eliminarCliente(String ci) {
        String sql = "DELETE FROM cliente WHERE ci = ?";
        try{
            Connection conexion = databaseConection.getInstancia().getConnection();
            PreparedStatement sentencia = conexion.prepareStatement(sql);
            sentencia.setString(1, ci);
            sentencia.execute();
            System.out.println("Cliente eliminado correctamente  .");
        }catch(Exception e){
            System.out.println("Error: "+e.getMessage());
        }
    }
    public void modificarCliente(Cliente c) {
        String sql = "UPDATE cliente SET email = ?, nombre = ?, apellido = ?, " +
                "ciudad = ?, direccion = ?, tel = ?, pais = ?, fecha_ingreso = ? " +
                "WHERE ci = ?";
        try {
            Connection conexion = databaseConection.getInstancia().getConnection();
            PreparedStatement sentencia = conexion.prepareStatement(sql);

            sentencia.setString(1, c.getEmail());
            sentencia.setString(2, c.getNombre());
            sentencia.setString(3, c.getApellido());
            sentencia.setString(4, c.getCiudad());
            sentencia.setString(5, c.getDireccion());
            sentencia.setString(6, c.getTel());
            sentencia.setString(7, c.getPais());
            sentencia.setDate(8, c.getFechaIngreso()); // java.sql.Date
            sentencia.setString(9, c.getCi()); // clave primaria en el WHERE

            int filas = sentencia.executeUpdate();

            if (filas > 0) {
                System.out.println("Cliente modificado correctamente.");
            } else {
                System.out.println("No se encontró cliente con CI: " + c.getCi());
            }

        } catch (Exception e) {
            System.out.println("Error al modificar cliente: " + e.getMessage());
        }
    }
    public void listarClientes(){
        String sql = "SELECT * FROM cliente";
        Connection conexion = databaseConection.getInstancia().getConnection();
        try{
            PreparedStatement sentencia = conexion.prepareStatement(sql);

            ResultSet resultado = sentencia.executeQuery();
            while (resultado.next()) {
                String ci = resultado.getString("ci");
                String email = resultado.getString("email");
                String nombre = resultado.getString("nombre");
                String apellido = resultado.getString("apellido");
                String ciudad = resultado.getString("ciudad");
                String direccion = resultado.getString("direccion");
                String tel = resultado.getString("tel");
                String pais = resultado.getString("pais");
                Date fechaIngreso = resultado.getDate("fecha_ingreso"); // java.sql.Date

                System.out.println("CI: " + ci);
                System.out.println("Nombre: " + nombre + " " + apellido);
                System.out.println("Email: " + email);
                System.out.println("Ciudad: " + ciudad);
                System.out.println("Dirección: " + direccion);
                System.out.println("Teléfono: " + tel);
                System.out.println("País: " + pais);
                System.out.println("Fecha de ingreso: " + fechaIngreso);
                System.out.println("-------------------------------");
            }


        }catch(Exception e){
            System.out.println("Error: "+e.getMessage());
        }
    }

    public Cliente buscarPorCi(String ci) {
        final String sql = "SELECT ci, email, nombre, apellido, ciudad, direccion, tel, pais, fecha_ingreso " +
                "FROM cliente WHERE ci = ?";
        Connection cn = databaseConection.getInstancia().getConnection();
        try (PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, ci);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapCliente(rs);
                }
            }

        } catch (Exception e) {
            System.out.println("Error al buscar cliente por CI: " + e.getMessage());
        }

        return null; // si no se encuentra
    }
    public List<Cliente> listarTodos() {
        final String sql = "SELECT ci, email, nombre, apellido, ciudad, direccion, tel, pais, fecha_ingreso " +
                "FROM cliente ORDER BY ci";
        List<Cliente> lista = new ArrayList<>();
        Connection cn = databaseConection.getInstancia().getConnection();
        try (PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapCliente(rs));
            }
        } catch (Exception e) {
            System.out.println("Error al listar clientes: " + e.getMessage());
        }
        return lista;
    }

    public Optional<MembresiaPlanView> buscarMembresiaActivaPorCi(String ci) throws Exception {
        final String sql = """
        SELECT  m.id,
                m.id_plan,
                m.id_cliente,
                m.fecha_inicio,
                m.fecha_fin,
                m.estado_id,
                p.nombre      AS plan_nombre,
                p.urlImagen  AS url_imagen
        FROM membresia m
        JOIN plan p ON p.id = m.id_plan
        WHERE m.id_cliente = ?
          AND m.estado_id = 1
          /* opcional fecha: AND m.fecha_fin >= CURRENT_DATE */
        ORDER BY m.fecha_fin DESC
        LIMIT 1
    """;

        Connection con = databaseConection.getInstancia().getConnection();
        try (
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, ci);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();

                MembresiaPlanView v = new MembresiaPlanView();
                v.setId(rs.getInt("id"));
                v.setIdPlan(rs.getInt("id_plan"));
                v.setIdCliente(rs.getString("id_cliente"));
                v.setFechaInicio(rs.getDate("fecha_inicio"));
                v.setFechaFin(rs.getDate("fecha_fin"));
                v.setEstadoId(rs.getInt("estado_id"));
                v.setPlanNombre(rs.getString("plan_nombre"));
                v.setUrlImagen(rs.getString("url_imagen"));

                return Optional.of(v);
            }
        }
    }

    //transformamos resulta de consulta a objeto
    private Cliente mapCliente(ResultSet rs) throws SQLException {
        Cliente c = new Cliente();
        c.setCi(rs.getString("ci"));
        c.setEmail(rs.getString("email"));
        c.setNombre(rs.getString("nombre"));
        c.setApellido(rs.getString("apellido"));
        c.setCiudad(rs.getString("ciudad"));
        c.setDireccion(rs.getString("direccion"));
        c.setTel(rs.getString("tel"));
        c.setPais(rs.getString("pais"));
        c.setFechaIngreso(rs.getDate("fecha_ingreso")); // java.sql.Date
        return c;
    }

    public String findDisplayNameByCi(String ownerCi) {
        final String sql = """
        SELECT nombre FROM cliente WHERE ci=?
    """;
        Connection con = databaseConection.getInstancia().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)){
            ps.setString(1, ownerCi);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("nombre");
                }
            }
        }catch(SQLException e){
            System.out.println("Error al buscar cliente por CI: " + e.getMessage());
        }
        return "";
    }
}
