package com.example.gymtrackerweb.db;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class databaseConection {

    private static Properties reader = new Properties();
    private static databaseConection instancia;
    private Connection conexion;

    static{
        try{
            InputStream in = databaseConection.class.getResourceAsStream("/db.properties");
            reader.load(in);

        }catch(Exception e){
            System.out.println("Error: "+e.getMessage());
        }
    }
    private databaseConection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = reader.getProperty("db.url");
            String username = reader.getProperty("db.user");
            String password = reader.getProperty("db.password");
            conexion = DriverManager.getConnection(url,username,password);
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error al conectar a la bd: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static databaseConection getInstancia() {
        try {
            if (instancia == null || instancia.conexion.isClosed()) {
                instancia = new databaseConection();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return instancia;
    }

    public Connection getConnection() {
        return conexion;
    }
}
