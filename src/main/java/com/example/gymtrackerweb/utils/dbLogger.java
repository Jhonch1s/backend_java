package com.example.gymtrackerweb.utils;


import com.example.gymtrackerweb.db.databaseConection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class dbLogger {
    public enum Accion {INSERT, UPDATE, DELETE}

    public dbLogger() {

    }

    public void insertarLog(Accion accion, String desc) throws SQLException{
        String sql = "INSERT INTO system_log (accion, descripcion )VALUES (?,?)";
        try {
            Connection conexion = databaseConection.getInstancia().getConnection();
            PreparedStatement sentencia = conexion.prepareStatement(sql);
            sentencia.setString(1, accion.name());
            sentencia.setString(2, desc);

            sentencia.execute();
        }catch (Exception e){
            System.err.println("Error: "+e.getMessage());
        }
    }
}
