package com.example.gymtrackerweb.utils;
/*
public class DatabaseSeeders {
    private dbLogger logger = new dbLogger();
    private usuarioDAO usuarioDAO = new usuarioDAO();
    private animalDAO animalDAO = new animalDAO();
    // 1- Eliminar los datos
        // Función que se encargue de eliminar todos los datos de usuario y animal
    public void vaciarBaseDeDatos(){
        String sql = "DELETE FROM animales;";
        String sql2 = "DELETE FROM usuarios;";

        try{
            Connection conexion = databaseConection.getInstancia().getConnection();
            PreparedStatement sentencia = conexion.prepareStatement(sql);
            PreparedStatement sentencia2 = conexion.prepareStatement(sql2);
            sentencia.execute();
            sentencia2.execute();

            System.out.println("Usuarios y Animales eliminados correctamente  .");

            logger.insertarLog(dbLogger.Accion.DELETE, "Usuarios y Animales eliminados");
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
    // 2- Crear datos de pruebas
        // Función que recibe usuario y animales y los inserta en la BD
    public void crearUsuarioYAnimales(){
        usuario jhon = new usuario(1,"Jhon", "jhon@gmail.com");
        usuario mati = new usuario(2,"Mati", "mati@gmail.com");
        usuario juan = new usuario(3,"Juan", "Jjuan@gmail.com");
        usuario mauri = new usuario(4,"Mauri", "mauri@gmail.com");

        usuarioDAO.agregarUsuarioConID(jhon);
        usuarioDAO.agregarUsuarioConID(mati);
        usuarioDAO.agregarUsuarioConID(juan);
        usuarioDAO.agregarUsuarioConID(mauri);

        animal juancho = new animal(1,"juancho", 4, "felino", 1);
        animal pepe = new animal(2,"pepe", 4, "felino", 1);
        animal tomi= new animal(3,"tomi", 4, "felino", 1);
        animal jane= new animal(4,"jane", 4, "canino", 2);
        animal juja= new animal(5,"juja", 4, "canino", 3);
        animal nino= new animal(5,"nino", 4, "canino", 4);

        animalDAO.agregarAnimal(juancho);
        animalDAO.agregarAnimal(pepe);
        animalDAO.agregarAnimal(tomi);
        animalDAO.agregarAnimal(jane);
        animalDAO.agregarAnimal(juja);
        animalDAO.agregarAnimal(nino);

    }

}
*/