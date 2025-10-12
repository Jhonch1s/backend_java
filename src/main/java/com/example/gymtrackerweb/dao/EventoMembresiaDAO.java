package com.example.gymtrackerweb.dao;



import com.example.gymtrackerweb.db.databaseConection;
import com.example.gymtrackerweb.model.EventoMembresia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EventoMembresiaDAO {
    public void agregarEventoMembresia(EventoMembresia c) {
        String sql = "INSERT INTO evento_membresia (id_staff, id_membresia, tipo_evento_id, fecha_evento, observaciones) " +
                "VALUES (?, ?, ?, ?, ?)";
        try{
            Connection conexion = databaseConection.getInstancia().getConnection();
            PreparedStatement sentencia = conexion.prepareStatement(sql);
            sentencia.setInt(1, c.getIdStaff());
            sentencia.setInt(2, c.getIdMembresia());
            sentencia.setInt(3, c.getTipoEventoId());
            sentencia.setTimestamp(4, c.getFechaEvento());
            sentencia.setString(5, c.getObservaciones());
            sentencia.execute();
            //System.out.println("Evento de Membresía cargado correctamente.");
        }catch(Exception e){
            System.out.println("Error: "+e.getMessage());
        }
    }

    public void eliminarEventoMembresia(int id) {
        String sql = "DELETE FROM evento_membresia WHERE id = ?";
        try{
            Connection conexion = databaseConection.getInstancia().getConnection();
            PreparedStatement sentencia = conexion.prepareStatement(sql);
            sentencia.setInt(1, id);
            sentencia.execute();
            System.out.println("Evento de Membresía eliminado correctamente.");
        }catch(Exception e){
            System.out.println("Error: "+e.getMessage());
        }
    }
    public void modificarEventoMembresia(EventoMembresia c) {
        String sql = "UPDATE evento_membresia SET id_staff = ?, id_membresia = ?, tipo_evento_id = ?, " +
                "fecha_evento = ?, observaciones = ? WHERE id = ?";
        try {
            Connection conexion = databaseConection.getInstancia().getConnection();
            PreparedStatement sentencia = conexion.prepareStatement(sql);

            sentencia.setInt(1, c.getIdStaff());
            sentencia.setInt(2, c.getIdMembresia());
            sentencia.setInt(3, c.getTipoEventoId());
            sentencia.setTimestamp(4, c.getFechaEvento());
            sentencia.setString(5, c.getObservaciones());
            sentencia.setInt(6, c.getId());

            int filas = sentencia.executeUpdate();

            if (filas > 0) {
                System.out.println("Evento de Membresía modificado correctamente.");
            } else {
                System.out.println("No se encontró evento de membresía con ID: " + c.getId());
            }

        } catch (Exception e) {
            System.out.println("Error al modificar evento de membresía: " + e.getMessage());
        }
    }
    public void listarEventoMembresia(){
        String sql = "SELECT ev.id, st.usuario_login as staff, ev.id_membresia, ti.nombre as tipo_evento, ev.fecha_evento, ev.observaciones " +
                "FROM evento_membresia ev, tipo_evento ti, staff st " +
                "WHERE ev.tipo_evento_id = ti.id AND ev.id_staff = st.id";
        try{
            Connection conexion = databaseConection.getInstancia().getConnection();
            PreparedStatement sentencia = conexion.prepareStatement(sql);

            ResultSet resultado = sentencia.executeQuery();
            while (resultado.next()) {
                int id = resultado.getInt("id");
                String staff = resultado.getString("staff");
                int id_membresia = resultado.getInt("id_membresia");
                String tipo_evento = resultado.getString("tipo_evento");
                java.sql.Timestamp fecha_evento = resultado.getTimestamp("fecha_evento");
                String observaciones = resultado.getString("observaciones");

                System.out.println("ID: " + id);
                System.out.println("Usuario Staff: " + staff);
                System.out.println("ID Membresía: " + id_membresia);
                System.out.println("Tipo Evento: " + tipo_evento);
                System.out.println("Fecha Evento: " + fecha_evento);
                System.out.println("Observaciones: " + observaciones);
                System.out.println("-------------------------------");
            }


        }catch(Exception e){
            System.out.println("Error: "+e.getMessage());
        }
    }


    private EventoMembresia mapEventoMembresia(ResultSet rs) throws SQLException {
        EventoMembresia c = new EventoMembresia();
        c.setId(rs.getInt("id"));
        c.setIdStaff(rs.getInt("id_staff"));
        c.setIdMembresia(rs.getInt("id_membresia"));
        c.setTipoEventoId(rs.getInt("tipo_evento_id"));
        c.setFechaEvento(rs.getTimestamp("fecha_evento"));
        c.setObservaciones(rs.getString("observaciones"));
        return c;
    }

}
