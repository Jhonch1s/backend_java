package com.example.gymtrackerweb.flow;

import com.example.gymtrackerweb.dao.StaffDAO;
import com.example.gymtrackerweb.model.Staff;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Scanner;

public class StaffFlujo {
    private final Scanner scanner;
    public StaffFlujo(Scanner scanner) {
        this.scanner = scanner;
    }
    private final StaffDAO staffDAO = new StaffDAO();

    public void mostrarMenu() {
        boolean seguir = true;
        while (seguir) {
            System.out.println("--- Staff Flujo ---");
            System.out.println("1.Alta de Staff");
            System.out.println("2.Modificar Staff");
            System.out.println("3.Baja de Staff");
            System.out.println("4.Listar Staff");
            System.out.println("5.Volver");
            System.out.print("Opcion: ");

            String opcion = scanner.nextLine();
            int opcionInt = 0;
            if (opcion.matches("\\d+")) { opcionInt = Integer.parseInt(opcion); }
            switch (opcionInt) {
                case 1 -> alta();
                case 2 -> modificar();
                case 3 -> baja();
                case 4 -> listar();
                case 5 -> seguir = false;
                default -> System.out.println("Opcion invalida");
            }
        }
    }

    private void alta() {
        System.out.println("--- Alta de Staff ---");

        String usuario;
        while (true) {
            System.out.print("Usuario Login: ");
            usuario = scanner.nextLine();
            if (!usuario.isEmpty()) {
                if (staffDAO.existeStaffPorUsuarioLogin(usuario)) {
                    System.out.println("Ya existe un Staff con ese Usuario Login");
                    return;
                } else {
                    break;
                }
            }
            System.out.println("El usuario no puede estar vacio.");
        }

        String nombre;
        while (true) {
            System.out.print("Nombre Completo: ");
            nombre = scanner.nextLine();
            if (!nombre.isEmpty() && nombre.matches("[a-zA-ZÁÉÍÓÚáéíóúÑñ ]+")) {
                break;
            }
            System.out.println("El nombre no puede estar vacio y solo puede contener letras y espacios, sin numero o simbolos.");
        }

        int rol;
        while (true) {
            System.out.print("Administrador? (S/N): ");
            String linea = scanner.nextLine();
            linea = linea.toUpperCase();
            if (linea.equals("S")) {
                rol = 1;
                break;
            } else if (linea.equals("N")) {
                rol = 0;
                break;
            } else  {
                System.out.println("Opcion invalida");
            }
        }

        int estado;
        while (true) {
            System.out.print("Ingreso el estado: \n");
            System.out.print("A:Activo \nI:Inactivo \n");
            String linea = scanner.nextLine();
            linea = linea.toUpperCase();
            if (linea.equals("A")) {
                estado = 1;
                break;
            } else if (linea.equals("I")) {
                estado = 0;
                break;
            } else  {
                System.out.println("Opcion invalida");
            }
        }

        Staff s = new Staff(usuario, nombre, rol, estado);
        staffDAO.crearStaff(s);
    }

    private void modificar() {
        System.out.println("--- Modificar Staff ---");

        int id;
        listar();
        while (true) {
            System.out.print("ID del staff a modificar: ");
            String linea = scanner.nextLine();
            if (linea.matches("\\d+")) {
                id = Integer.parseInt(linea);
                    if (!staffDAO.existeStaffPorId(id)) {
                        System.out.println("No existe un staff con esa ID");
                        return;
                    } else {
                        break;
                    }
            } else {
                System.out.println("Ingrese una ID valida");
            }
        }

        String usuario;
        while (true) {
            System.out.print("Usuario Login: ");
            usuario = scanner.nextLine();
            if (!usuario.isEmpty()) {
                if (staffDAO.existeStaffPorUsuarioLogin(usuario)) {
                    System.out.println("Ya existe un Staff con ese Usuario Login");
                    return;
                } else {
                    break;
                }
            }
            System.out.println("El usuario no puede estar vacio");
        }

        String nombre;
        while (true) {
            System.out.print("Nombre Completo: ");
            nombre = scanner.nextLine();
            if (!nombre.isEmpty() && nombre.matches("[a-zA-ZÁÉÍÓÚáéíóúÑñ ]+")) {
                break;
            }
            System.out.println("El nombre no puede estar vacio y solo puede contener letras y espacios, sin numero o simbolos.");
        }

        int rol;
        while (true) {
            System.out.print("Administrador? (S/N): ");
            String linea = scanner.nextLine();
            linea = linea.toUpperCase();
            if (linea.equals("S")) {
                rol = 1;
                break;
            } else if (linea.equals("N")) {
                rol = 0;
                break;
            } else  {
                System.out.println("Opcion invalida");
            }
        }

        int estado;
        while (true) {
            System.out.print("Ingreso el estado: \n");
            System.out.print("A:Activo \nI:Inactivo \n");
            String linea = scanner.nextLine();
            linea = linea.toUpperCase();
            if (linea.equals("A")) {
                estado = 1;
                break;
            } else if (linea.equals("I")) {
                estado = 0;
                break;
            } else  {
                System.out.println("Opcion invalida");
            }
        }

        Staff s = new Staff(id, usuario, nombre, rol, estado);
        staffDAO.editarStaff(s);
    }

    private void baja() {
        System.out.println("--- Baja de Staff ---");

        int id;
        listar();
        while (true) {
            System.out.print("ID del staff a eliminar: ");
            String linea = scanner.nextLine().trim();
            try {
                id = Integer.parseInt(linea);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un número válido para ID.");
            }
        }

        staffDAO.eliminarStaff(id);
    }

    private void listar() {
        System.out.println("--- Listado de Staff ---");
        List<Staff> lista = staffDAO.listarStaff();
        if (lista.isEmpty()) {
            System.out.println("No hay ningun staff registrado");
            return;
        }
        System.out.println("ID | Usuario | Nombre Completo | ¿Administrador? | Estado");
        System.out.println("-----------------------------------------------");

        String Rol = "No";
        String Estado = "Inactivo";
        for (Staff st : lista) {
            if (st.getRol()==1) {Rol = "Si";} else {
                Rol = "No";
            }
            if (st.getEstado()==1) { Estado = "Activo";} else {
                Estado = "Inactivo";
            }
            System.out.println(
                    st.getId() + " | "
                            + st.getUsuarioLogin() + " | "
                            + st.getNombreCompleto() + " | "
                            + Rol + " | "
                            + Estado
            );
        }
    }
}
