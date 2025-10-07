package com.example.gymtrackerweb.dao;



import com.example.gymtrackerweb.db.databaseConection;
import com.example.gymtrackerweb.model.Membresia;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static com.example.gymtrackerweb.flow.UtilidadesFlujo.*;

public class MembresiaDAO {
    public int agregarMembresia(Membresia c) {
        String sql = "INSERT INTO membresia (id_plan, id_cliente, fecha_inicio, fecha_fin, estado_id) " +
                "VALUES (?, ?, ?, ?, ?)";
        try{
            Connection conexion = databaseConection.getInstancia().getConnection();
            PreparedStatement sentencia = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            sentencia.setInt(1, c.getIdPlan());
            sentencia.setString(2, c.getIdCliente());
            sentencia.setDate(3, c.getFechaInicio());
            sentencia.setDate(4, c.getFechaFin());
            sentencia.setInt(5, c.getEstadoId());
            sentencia.execute();
            ResultSet rs = sentencia.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
            System.out.println("Membresía cargada correctamente.");
        }catch(Exception e){
            System.out.println("Error: "+e.getMessage());
        }
        return -1;
    }

    public void eliminarMembresia(int id) {
        String sql = "DELETE FROM membresia WHERE id = ?";
        try{
            Connection conexion = databaseConection.getInstancia().getConnection();
            PreparedStatement sentencia = conexion.prepareStatement(sql);
            sentencia.setInt(1, id);
            sentencia.execute();
            System.out.println("Membresía eliminada correctamente.");
        }catch(Exception e){
            System.out.println("Error: "+e.getMessage());
        }
    }
    public void modificarMembresia(Membresia c) {
        String sql = "UPDATE membresia SET id_plan = ?, id_cliente = ?, fecha_inicio = ?, " +
                "fecha_fin = ?, estado_id = ? WHERE id = ?";
        try {
            Connection conexion = databaseConection.getInstancia().getConnection();
            PreparedStatement sentencia = conexion.prepareStatement(sql);

            sentencia.setInt(1, c.getIdPlan());
            sentencia.setString(2, c.getIdCliente());
            sentencia.setDate(3, c.getFechaInicio());
            sentencia.setDate(4, c.getFechaFin());
            sentencia.setInt(5, c.getEstadoId());
            sentencia.setInt(6, c.getId());

            int filas = sentencia.executeUpdate();

            if (filas > 0) {
                System.out.println("Membresía modificada correctamente.");
            } else {
                System.out.println("No se encontró membresía con ID: " + c.getId());
            }

        } catch (Exception e) {
            System.out.println("Error al modificar membresía: " + e.getMessage());
        }
    }

    public void listarMembresia(){
        SimpleDateFormat formateador = new SimpleDateFormat("dd/MM/yyyy");
        String sql = "SELECT cliente.ci f, CONCAT(CONCAT(cliente.nombre, ' '), cliente.apellido) a, plan.nombre b, membresia.fecha_inicio c, membresia.fecha_fin d, membresia.estado_id e FROM membresia, cliente, plan WHERE membresia.id_cliente = cliente.ci AND membresia.id_plan = plan.id";
        try{
            Connection conexion = databaseConection.getInstancia().getConnection();
            PreparedStatement sentencia = conexion.prepareStatement(sql);
            ResultSet resultado = sentencia.executeQuery();
            String[] headers = {"CI", "Nombre completo", "Plan", "Fecha Inicio", "Fecha Fin", "Estado"};
            int[] anchos = {9, 20, 9, 13, 11, 10};
            printHeader(headers, anchos);
            while (resultado.next()) {
                System.out.println(formatRow(new Object[]{
                        nvl(resultado.getString("f")),
                        nvl(resultado.getString("a")),
                        nvl(resultado.getString("b")),
                        nvl(formateador.format(resultado.getDate("c"))),
                        nvl(formateador.format(resultado.getDate("d"))),
                        nvl((resultado.getInt("e") == 1 ? "Activa" : "Inactiva"))
                }, anchos));
            }
        }catch(Exception e){
            System.out.println("Error: "+e.getMessage());
        }
    }

    public List<Membresia> obtenerMembresias(){
        List<Membresia> lista = new ArrayList<>();
        String sql = "SELECT * FROM membresia";
        try{
            Connection conexion = databaseConection.getInstancia().getConnection();
            PreparedStatement sentencia = conexion.prepareStatement(sql);

            ResultSet resultado = sentencia.executeQuery();
            while (resultado.next()) {
                lista.add(mapMembresia(resultado));
            }
        }catch(Exception e){
            System.out.println("Error: "+e.getMessage());
        }
        return lista;
    }

    public Membresia obtenerMembresiaPorCedula(String cedula){
        String sql = "SELECT * FROM membresia WHERE id_cliente = ?";
        try{
            Connection conexion = databaseConection.getInstancia().getConnection();
            PreparedStatement sentencia = conexion.prepareStatement(sql);
            sentencia.setString(1, cedula);
            ResultSet resultado = sentencia.executeQuery();
            if (resultado.next()) {
                return mapMembresia(resultado);
            }
        }catch(Exception e){
            System.out.println("Error: "+e.getMessage());
        }
        return null;
    }

    private Membresia mapMembresia(ResultSet rs) throws SQLException {
        Membresia c = new Membresia();
        c.setId(rs.getInt("id"));
        c.setIdPlan(rs.getInt("id_plan"));
        c.setIdCliente(rs.getString("id_cliente"));
        c.setFechaInicio(rs.getDate("fecha_inicio"));
        c.setFechaFin(rs.getDate("fecha_fin"));
        c.setEstadoId(rs.getInt("estado_id"));
        return c;
    }

}
