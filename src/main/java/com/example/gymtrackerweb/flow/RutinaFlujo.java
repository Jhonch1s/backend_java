package com.example.gymtrackerweb.flow;

import java.util.List;
import java.util.Scanner;
import com.example.gymtrackerweb.dao.RutinaDAO;
import com.example.gymtrackerweb.model.Rutina;
import com.example.gymtrackerweb.model.enums.Objetivo;

public class RutinaFlujo {
    private final Scanner scanner;
    public RutinaFlujo(Scanner scanner) {
        this.scanner = scanner;
    }

    public void mostrarMenu() {
        boolean seguir = true;
        while (seguir) {
            System.out.println("--- Rutina Flujo ---");
            System.out.println("1. Crear Rutina");
            System.out.println("2. Eliminar Rutina");
            System.out.println("3. Editar Rutina");
            System.out.println("4. Listar Rutinas");
            System.out.println("5. Listar Rutinas por Objetivo");
            System.out.println("6. Volver");
            System.out.print("Opcion: ");
            String opcion = scanner.nextLine();
            int opcionInt = 0;
            if (opcion.matches("\\d+")) { opcionInt = Integer.parseInt(opcion); }
            switch (opcionInt) {
                case 1 -> crearRutina();
                case 2 -> eliminarRutina();
                case 3 -> editarRutina();
                case 4 -> listarRutinas();
                case 5 -> listarRutinasPorObjetivo();
                case 6 -> seguir = false;
                default -> System.out.println("Opcion invalida");
            }
        }
    }
    private void crearRutina() {
        RutinaDAO rutinaDAO = new RutinaDAO();
        String nombre;
        while (true) {
            nombre = UtilidadesFlujo.leerNoVacio("Ingrese el nombre de la rutina: ");
            if (rutinaDAO.existePorNombre(nombre)) {
                System.out.println("Error: ya existe una rutina con ese nombre. Intente con otro.");
            } else {
                break;
            }
        }
        String objetivoStr;
        Objetivo objetivo = null;
        while (objetivo == null) {
            objetivoStr = UtilidadesFlujo.leerNoVacio("Ingrese el objetivo de la rutina (Hipertrofia, Fuerza, Resistencia): ");
            try {
                objetivo = Objetivo.valueOf(objetivoStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Error: Debe ser Hipertrofia, Fuerza o Resistencia.");
            }
        }
        int semanas = UtilidadesFlujo.leerEnteroPositivo("Ingrese la duración en semanas: ");
        Rutina nuevaRutina = new Rutina(nombre, objetivo, semanas);
        rutinaDAO.agregarRutina(nuevaRutina);
    }


    private void listarRutinas() {
        System.out.println("Listado de rutinas:");
        RutinaDAO rutinaDAO = new RutinaDAO();
        List<Rutina> rutinas = rutinaDAO.listarRutinas();
        for (Rutina rutina : rutinas) {
            System.out.println(rutina.toStringID());
        }
    }

    private void eliminarRutina() {
        RutinaDAO rutinaDAO = new RutinaDAO();
        Rutina rutinaEliminar = null;
        while (rutinaEliminar == null) {
            listarRutinas();
            int id = UtilidadesFlujo.leerEnteroPositivo("Ingrese el ID de la rutina a eliminar: ");
            rutinaEliminar = rutinaDAO.buscarPorId(id);
            if (rutinaEliminar == null) {
                System.out.println("Error: ese ID no existe en el sistema. Intente nuevamente.");
            }
        }
        String confirmacion = UtilidadesFlujo.leerNoVacio("¿Está seguro que desea eliminar la rutina '" + rutinaEliminar.getNombre() + "' (ID: " + rutinaEliminar.getId() + ")? (S/N): ");
        if (confirmacion.equalsIgnoreCase("S")) {
            rutinaDAO.eliminarRutina(rutinaEliminar);
            System.out.println("Rutina eliminada correctamente.");
        } else {
            System.out.println("Operación cancelada.");
        }
    }

    private void editarRutina() {
        RutinaDAO rutinaDAO = new RutinaDAO();
        Rutina rutinaEditar = null;
        while (rutinaEditar == null) {
            listarRutinas();
            int id = UtilidadesFlujo.leerEnteroPositivo("Ingrese el ID de la rutina a editar: ");
            rutinaEditar = rutinaDAO.buscarPorId(id);
            if (rutinaEditar == null) {
                System.out.println("Error: ese ID no existe en el sistema. Intente nuevamente.");
            }
        }
        String nombre;
        while (true) {
            nombre = UtilidadesFlujo.leerNoVacio("Ingrese el nuevo nombre de la rutina: ");
            if (!nombre.equalsIgnoreCase(rutinaEditar.getNombre()) && rutinaDAO.existePorNombre(nombre)) { //esto de acá es por si el nuevo nombre es distinto del actual y que no exista en la base
                System.out.println("Error: ya existe una rutina con ese nombre. Intente con otro.");
            } else {
                break;
            }
        }
        Objetivo objetivo = null;
        while (objetivo == null) {
            String objetivoStr = UtilidadesFlujo.leerNoVacio("Ingrese el objetivo de la rutina (Hipertrofia, Fuerza, Resistencia): ");
            try {
                objetivo = Objetivo.valueOf(objetivoStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Error: Debe ser Hipertrofia, Fuerza o Resistencia.");
            }
        }
        int semanas = UtilidadesFlujo.leerEnteroPositivo("Ingrese la nueva duración en semanas: ");
        Rutina rutinaActualizada = new Rutina(rutinaEditar.getId(), nombre, objetivo, semanas);
        rutinaDAO.modificarRutina(rutinaActualizada);
        System.out.println("Rutina actualizada correctamente.");
    }


    private void listarRutinasPorObjetivo() {
        String objetivoStr;
        Objetivo objetivo = null;
        while (objetivo == null) {
            objetivoStr = UtilidadesFlujo.leerNoVacio("Ingrese el objetivo de la rutina (Hipertrofia, Fuerza, Resistencia): ");
            try {
                objetivo = Objetivo.valueOf(objetivoStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Error. Debe ser Hipertrofia, Fuerza o Resistencia.");
            }
        }
        RutinaDAO rutinaDAO = new RutinaDAO();
        List<Rutina> rutinas = rutinaDAO.listarRutinasPorObjetivo(objetivo);
        if (rutinas.isEmpty()) {
            System.out.println("No hay rutinas con el objetivo: " + objetivo);
        } else {
            System.out.println("Listado de rutinas:");
            for (Rutina rutina : rutinas) {
                System.out.println(rutina.toString());
            }
        }
    }
}
