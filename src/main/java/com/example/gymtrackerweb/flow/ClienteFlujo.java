package com.example.gymtrackerweb.flow;
import com.example.gymtrackerweb.dao.ClienteDAO;
import com.example.gymtrackerweb.model.Cliente;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.example.gymtrackerweb.flow.UtilidadesFlujo.*;
public class ClienteFlujo {
    private final ClienteDAO dao;

    public ClienteFlujo() {
        this.dao = new ClienteDAO();
    }

    public void run() throws SQLException {
        int op;
        do {
            mostrarMenuPrincipal();
            op = leerEntero("Opción: ");
            manejarOpcionPrincipal(op);
        } while (op != 0);
        System.out.println("Saliendo de ClienteFlujo...");
    }
    private void mostrarMenuPrincipal() {
        System.out.println("\n=== MENÚ CLIENTES ===");
        System.out.println("1) Alta de cliente");
        System.out.println("2) Baja (eliminar) cliente");
        System.out.println("3) Modificar cliente");
        System.out.println("4) Listar clientes");
        System.out.println("5) Buscar cliente por CI");
        System.out.println("0) Salir");
    }

    private void manejarOpcionPrincipal(int op) throws SQLException {
        switch (op) {
            case 1 -> altaCliente();
            case 2 -> bajaCliente();
            case 3 -> modificarCliente();
            case 4 -> listarTodos();
            case 5 -> buscarPorCi();
            case 0 -> { /* salir */ }
            default -> System.out.println("Opción inválida.");
        }
    }

    //acciones del flujo
    private void altaCliente() throws SQLException {
        System.out.println("\n      Alta de Cliente");
        String ci = leerNoVacio("CI: ");
        if (!ci.matches("\\d+")) {System.out.println("Error: La cedula solo debe estar compuesta por numeros"); return; }
        String email = leerNoVacio("Email: ");
        String nombre = leerNoVacio("Nombre: ");
        String apellido = leerNoVacio("Apellido: ");
        String ciudad = leerOpcional("Ciudad (opcional): ");
        String direccion = leerOpcional("Dirección (opcional): ");
        String tel = leerOpcional("Tel (opcional): ");
        String pais = leerOpcional("País (opcional): ");
        Date   fechaIng = leerFechaSql("Fecha de ingreso (DD-MM-YYY): ");

        Cliente c = new Cliente(ci, email, nombre, apellido, ciudad, direccion, tel, pais, fechaIng);
        dao.agregarCliente(c);
    }

    private void bajaCliente() {
        System.out.println("\n      Baja (eliminar) Cliente");
        String ci = leerNoVacio("CI del cliente a eliminar: ");
        Cliente actual = dao.buscarPorCi(ci);
        if (actual == null) {
            System.out.println("No existe un cliente con ese CI.");
            return;
        }
        if (confirmar("¿Confirmar eliminación? (s/n): ")) {
            dao.eliminarCliente(ci);
        }
    }

    private void modificarCliente() {
        listarTodos();
        System.out.println("\n     Modificar Cliente");
        String ci = leerNoVacio("CI del cliente a modificar: ");
        Cliente actual = dao.buscarPorCi(ci);
        if (actual == null) {
            System.out.println("No existe un cliente con ese CI.");
            return;
        }

        System.out.println("Valores actuales (Enter para mantener):");
        imprimirCliente(actual);

        String email = leerOpcional("Email ["+ nvl(actual.getEmail())+ "]: ");
        String nombre = leerOpcional("Nombre ["+ nvl(actual.getNombre())+ "]: ");
        String apellido = leerOpcional("Apellido ["+ nvl(actual.getApellido())+ "]: ");
        String ciudad = leerOpcional("Ciudad ["+ nvl(actual.getCiudad())+ "]: ");
        String direccion = leerOpcional("Dirección ["+ nvl(actual.getDireccion()) +"]: ");
        String tel = leerOpcional("Tel ["+ nvl(actual.getTel())+ "]: ");
        String pais = leerOpcional("País ["+ nvl(actual.getPais())+ "]: ");
        String fechaStr = leerOpcional("Fecha ingreso (YYYY-MM-DD) [" + nvl(actual.getFechaIngreso()) + "]: ");

        if (!email.isBlank()) actual.setEmail(email);
        if (!nombre.isBlank()) actual.setNombre(nombre);
        if (!apellido.isBlank()) actual.setApellido(apellido);
        if (!ciudad.isBlank()) actual.setCiudad(ciudad);
        if (!direccion.isBlank()) actual.setDireccion(direccion);
        if (!tel.isBlank()) actual.setTel(tel);
        if (!pais.isBlank()) actual.setPais(pais);
        actual.setFechaIngreso(parseFechaOr(fechaStr, actual.getFechaIngreso()));

        dao.modificarCliente(actual);
        System.out.println("Cliente modificado.");
    }

    private void listarTodos() {
        System.out.println("\n      Listar Clientes");
        List<Cliente> lista = dao.listarTodos();
        if (lista == null) lista = new ArrayList<>();
        if (lista.isEmpty()) {
            System.out.println("Sin resultados.");
            return;
        }
        imprimirTabla(lista);
    }

    private void buscarPorCi() {
        System.out.println("\n     Buscar por CI");
        String ci = leerNoVacio("CI: ");
        Cliente c = dao.buscarPorCi(ci);
        if (c == null) {
            System.out.println("No encontrado.");
            return;
        }
        imprimirCliente(c);
    }

    //Impresiones en pantalla

    //Formateamos tabla
    private void imprimirTabla(List<Cliente> lista) {
        String[] headers = {"CI", "Email", "Nombre", "Apellido", "Ciudad", "Dirección", "Tel", "País", "FechaIngreso"};
        int[] anchos  = {12, 24, 14, 14, 12, 24, 12, 12, 12};
        printHeader(headers, anchos);

        for (Cliente c : lista) {
            System.out.println(formatRow(new Object[]{
                    nvl(c.getCi()),
                    nvl(c.getEmail()),
                    nvl(c.getNombre()),
                    nvl(c.getApellido()),
                    nvl(c.getCiudad()),
                    nvl(c.getDireccion()),
                    nvl(c.getTel()),
                    nvl(c.getPais()),
                    nvl(c.getFechaIngreso())
            }, anchos));
        }
    }
    //formateamos texto
    //esto queda descartado
    private String detalleClienteBloque(Cliente c) {
        return "Cliente {\n" +
                "  ci='" + nvl(c.getCi()) + "',\n" +
                "  email='" + nvl(c.getEmail()) + "',\n" +
                "  nombre='" + nvl(c.getNombre()) + "',\n" +
                "  apellido='" + nvl(c.getApellido()) + "',\n" +
                "  ciudad='" + nvl(c.getCiudad()) + "',\n" +
                "  direccion='" + nvl(c.getDireccion()) + "',\n" +
                "  tel='" + nvl(c.getTel()) + "',\n" +
                "  pais='" + nvl(c.getPais()) + "',\n" +
                "  fechaIngreso=" + nvl(c.getFechaIngreso()) + "\n" +
                "}";
    }

    //nuevo imprimir cliente como tabla
    private void imprimirCliente(Cliente c) {
        String[] headers = {"CI", "Email", "Nombre", "Apellido", "Ciudad", "Dirección", "Tel", "País", "FechaIngreso"};
        int[] anchos  = {12, 24, 14, 14, 12, 24, 12, 12, 12};

        printHeader(headers, anchos);
        // Imprimimos una sola fila en este caso....
        System.out.println(formatRow(new Object[]{
                nvl(c.getCi()),
                nvl(c.getEmail()),
                nvl(c.getNombre()),
                nvl(c.getApellido()),
                nvl(c.getCiudad()),
                nvl(c.getDireccion()),
                nvl(c.getTel()),
                nvl(c.getPais()),
                nvl(c.getFechaIngreso())
        }, anchos));
    }

}
