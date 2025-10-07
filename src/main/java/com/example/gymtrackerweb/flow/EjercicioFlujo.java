package com.example.gymtrackerweb.flow;

import com.example.gymtrackerweb.dao.EjercicioDAO;
import com.example.gymtrackerweb.model.Ejercicio;
import com.example.gymtrackerweb.model.enums.Dificultad;
import java.util.List;
import java.util.Scanner;

public class EjercicioFlujo {
    private final Scanner scanner;
    public EjercicioFlujo(Scanner scanner) {
        this.scanner = scanner;
    }

    public void mostrarMenu() {
        boolean seguir = true;
        while (seguir) {
            System.out.println("--- Ejercicio Flujo ---");
            System.out.println("1. Crear Ejercicio");
            System.out.println("2. Eliminar Ejercicio");
            System.out.println("3. Editar Ejercicio");
            System.out.println("4. Listar Ejercicios");
            System.out.println("5. Listar Ejercicios por Grupo Muscular");
            System.out.println("6. Volver");
            System.out.print("Opcion: ");
            String opcion = scanner.nextLine();
            int opcionInt = 0;
            if (opcion.matches("\\d+")) { opcionInt = Integer.parseInt(opcion); }
            switch (opcionInt){
                case 1 -> crearEjercicio();
                case 2 -> eliminarEjercicio();
                case 3 -> editarEjercicio();
                case 4 -> listarEjercicios();
                case 5 -> listarEjerciciosPorGrupoMuscular();
                case 6 -> seguir = false;
                default -> System.out.println("Opcion invalida");
            }
        }
    }
    private void crearEjercicio() {
        EjercicioDAO ejercicioDAO = new EjercicioDAO();
        String nombre;
        while (true) {
            nombre = UtilidadesFlujo.leerNoVacio("Ingrese el nombre del ejercicio: ");
            if (ejercicioDAO.existePorNombre(nombre)) {
                System.out.println("Error: ya existe un ejercicio con ese nombre. Intente con otro.");
            } else {
                break;
            }
        }
        ejercicioDAO.listarGruposMusculares();
        int grupoMuscularId = UtilidadesFlujo.leerEnteroPositivo("Ingrese el ID del grupo muscular: ");

        Dificultad dificultad = null;
        while (dificultad == null) {
            String dificultadStr = UtilidadesFlujo.leerNoVacio("Ingrese la dificultad (PRINCIPIANTE, INTERMEDIO, AVANZADO): ");
            try {
                dificultad = Dificultad.valueOf(dificultadStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Error: Debe ser PRINCIPIANTE, INTERMEDIO o AVANZADO.");
            }
        }
        String url = UtilidadesFlujo.leerNoVacio("Ingrese foto o video del ejercicio (url): ");
        Ejercicio nuevoEjercicio = new Ejercicio(nombre, grupoMuscularId, dificultad, url);
        ejercicioDAO.agregarEjercicio(nuevoEjercicio);
        System.out.println("Ejercicio creado correctamente.");
    }



    private void eliminarEjercicio() {
        EjercicioDAO ejercicioDAO = new EjercicioDAO();
        Ejercicio ejercicioEliminar = null;
        while (ejercicioEliminar == null) {
            listarEjercicios();
            int id = UtilidadesFlujo.leerEnteroPositivo("Ingrese el ID del ejercicio a eliminar: ");
            ejercicioEliminar = ejercicioDAO.buscarPorId(id);

            if (ejercicioEliminar == null) {
                System.out.println("Error: ese id no existe en el sistema. Intente nuevamente.");
            }
        }
        String confirmacion = UtilidadesFlujo.leerNoVacio("¿Está seguro que desea eliminar el ejercicio con ID " + ejercicioEliminar.getId() + "? (S/N): ");
        if (confirmacion.equalsIgnoreCase("S")) {
            ejercicioDAO.eliminarEjercicio(ejercicioEliminar);
            System.out.println("Ejercicio eliminado correctamente.");
        } else {
            System.out.println("Operación cancelada.");
        }
    }


    private void editarEjercicio() {
        EjercicioDAO ejercicioDAO = new EjercicioDAO();
        Ejercicio ejercicioEditar = null;
        while (ejercicioEditar == null) {
            listarEjercicios();
            int id = UtilidadesFlujo.leerEnteroPositivo("Ingrese el ID del ejercicio: ");
            ejercicioEditar = ejercicioDAO.buscarPorId(id);
            if (ejercicioEditar == null) {
                System.out.println("Error: ese id no existe en el sistema. Intente nuevamente.");
            }
        }
        String nombre;
        while (true) {
            nombre = UtilidadesFlujo.leerNoVacio("Ingrese el nuevo nombre del ejercicio: ");
            if (!nombre.equalsIgnoreCase(ejercicioEditar.getNombre())
                    && ejercicioDAO.existePorNombre(nombre)) {
                System.out.println("Error: ya existe un ejercicio con ese nombre. Intente con otro.");
            } else {
                break;
            }
        }
        ejercicioDAO.listarGruposMusculares();
        int grupoMuscularId = UtilidadesFlujo.leerEnteroPositivo("Ingrese el ID del grupo muscular: ");
        Dificultad dificultad = null;
        while (dificultad == null) {
            String dificultadStr = UtilidadesFlujo.leerNoVacio("Ingrese la dificultad (PRINCIPIANTE, INTERMEDIO, AVANZADO): ");
            try {
                dificultad = Dificultad.valueOf(dificultadStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Error: Debe ser PRINCIPIANTE, INTERMEDIO o AVANZADO.");
            }
        }
        String url = UtilidadesFlujo.leerNoVacio("Ingrese foto o video del ejercicio (url): ");
        Ejercicio nuevoEjercicio = new Ejercicio(ejercicioEditar.getId(), nombre, grupoMuscularId, dificultad, url);
        ejercicioDAO.modificarEjercicio(nuevoEjercicio);
        System.out.println(" Ejercicio editado correctamente.");
    }


    private void listarEjercicios() {
        System.out.println("Listado de ejercicios:");
        EjercicioDAO ejercicioDAO = new EjercicioDAO();
        List<Ejercicio> ejercicios = ejercicioDAO.listarEjercicios();
        for (Ejercicio ejercicio : ejercicios) {
            System.out.println(ejercicio.toString());
        }
    }

    private void listarEjerciciosPorGrupoMuscular() {
        EjercicioDAO ejercicioDAO1 = new EjercicioDAO();
        ejercicioDAO1.listarGruposMusculares();
        String nombre = UtilidadesFlujo.leerNoVacio("Ingrese el nombre del grupo muscular: ");
        EjercicioDAO ejercicioDAO2 = new EjercicioDAO();
        List<Ejercicio> ejercicios = ejercicioDAO2.listarEjerciciosPorGrupoMuscular(nombre);

        if (ejercicios.isEmpty()) {
            System.out.println("No hay cargados ejercicios con ese grupo muscular: " + nombre);
        } else {
            System.out.println("Listado de ejercicios:");
            for (Ejercicio ejercicio : ejercicios) {
                System.out.println(ejercicio);
            }
        }
    }


}
