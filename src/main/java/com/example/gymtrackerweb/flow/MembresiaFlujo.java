package com.example.gymtrackerweb.flow;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.sql.Date;

import com.example.gymtrackerweb.dao.ClienteDAO;
import com.example.gymtrackerweb.dao.EventoMembresiaDAO;
import com.example.gymtrackerweb.dao.MembresiaDAO;
import com.example.gymtrackerweb.dao.PlanDAO;
import com.example.gymtrackerweb.model.Cliente;
import com.example.gymtrackerweb.model.EventoMembresia;
import com.example.gymtrackerweb.model.Plan;
import com.example.gymtrackerweb.model.Membresia;

import static com.example.gymtrackerweb.flow.UtilidadesFlujo.*;
import static com.example.gymtrackerweb.flow.UtilidadesFlujo.nvl;


public class MembresiaFlujo {
    private final Scanner scanner;
    public MembresiaFlujo(Scanner scanner) {
        this.scanner = scanner;
    }
    SimpleDateFormat formateador = new SimpleDateFormat("dd/MM/yyyy");

    public void registrarMembresia() {
        System.out.println("Lista de clientes:");
        ClienteDAO clienteDAO = new ClienteDAO();
        List<Cliente> lista = clienteDAO.listarTodos();
        if (lista == null) lista = new ArrayList<>();
        if (lista.isEmpty()) {
            System.out.println("No hay clientes registrados en el sistema.");
            return;
        }
        imprimirTablaCliente(lista);
        System.out.print("Ingrese la cédula del cliente: ");
        String idcliente = scanner.nextLine();
        Cliente cliente = clienteDAO.buscarPorCi(idcliente);
        if (cliente == null) {
            System.out.println("No se encontró el cliente");
            return;
        }
        MembresiaDAO membresiaDAO = new MembresiaDAO();
        Membresia membresia = membresiaDAO.obtenerMembresiaPorCedula(idcliente);
        if (membresia != null) {
            System.out.println("Este cliente ya tiene una membresía. Puede renovarla.");
            return;
        }
        System.out.println("Datos del cliente:");
        System.out.println("-------------------------------");
        System.out.println("CI: " + cliente.getCi());
        System.out.println("Nombre: " + cliente.getNombre() + " " + cliente.getApellido());
        System.out.println("Email: " + cliente.getEmail());
        System.out.println("Ciudad: " + cliente.getCiudad());
        System.out.println("Dirección: " + cliente.getDireccion());
        System.out.println("Teléfono: " + cliente.getTel());
        System.out.println("País: " + cliente.getPais());
        System.out.println("Fecha de ingreso: " + formateador.format(cliente.getFechaIngreso()));
        System.out.println("-------------------------------");
        PlanDAO planDAO = new PlanDAO();
        Plan plan = null;
        String idplan = null;
        System.out.println("Planes activos:");
        List<Plan> listaPlan = planDAO.listarActivos();
        if (listaPlan == null) listaPlan = new ArrayList<>();
        if (listaPlan.isEmpty()) {
            System.out.println("No hay planes activos para realizar la renovación.");
            return;
        }
        listaPlan.sort(Comparator.comparingInt(Plan::getId));
        imprimirTablaPlan(listaPlan);
        while (plan == null) {
            System.out.print("Ingrese el número del plan: ");
            idplan = scanner.nextLine();
            plan = planDAO.buscarPorId(Integer.parseInt(idplan));
            if (plan == null) {
                System.out.println("No se encontró el plan");
            }
        }
        try {
            Date fechaDeInicio = Date.valueOf(LocalDate.now());
            Date fechaDeFin = switch (plan.getDuracionUnidadId()) {
                case 1 -> Date.valueOf(LocalDate.now().plusDays(plan.getDuracionTotal()));
                case 2 -> Date.valueOf(LocalDate.now().plusWeeks(plan.getDuracionTotal()));
                case 3 -> Date.valueOf(LocalDate.now().plusMonths(plan.getDuracionTotal()));
                case 4 -> Date.valueOf(LocalDate.now().plusYears(plan.getDuracionTotal()));
                default -> fechaDeInicio;
            };
            int idNuevaMembresia = membresiaDAO.agregarMembresia(new Membresia(-1, Integer.parseInt(idplan), idcliente, fechaDeInicio, fechaDeFin, 1));
            if (idNuevaMembresia == -1) {
                System.out.println("Error al registrar la membresia");
            }
            else {
                EventoMembresiaDAO eventoDAO = new EventoMembresiaDAO();
                eventoDAO.agregarEventoMembresia(new EventoMembresia(-1, 1, idNuevaMembresia, 2, Timestamp.from(Instant.now()), ""));
                System.out.println("Membresia registrada exitosamente. Fecha Inicio: " + formateador.format(fechaDeInicio) + ", Fecha Fin: " + formateador.format(fechaDeFin));
            }
        } catch (Exception e) {
            System.out.println("Error al registrar la membresia: " + e.getMessage());
        }

    }

    public void renovarMembresia() {
        System.out.println("Lista de clientes:");
        ClienteDAO clienteDAO = new ClienteDAO();
        List<Cliente> lista = clienteDAO.listarTodos();
        if (lista == null) lista = new ArrayList<>();
        if (lista.isEmpty()) {
            System.out.println("No hay clientes registrados en el sistema.");
            return;
        }
        imprimirTablaCliente(lista);
        System.out.print("Ingrese la cédula del cliente: ");
        String idcliente = scanner.nextLine();
        Cliente cliente = clienteDAO.buscarPorCi(idcliente);
        if (cliente == null) {
            System.out.println("No se encontró el cliente");
            return;
        }
        System.out.println("Datos del cliente:");
        System.out.println("-------------------------------");
        System.out.println("CI: " + cliente.getCi());
        System.out.println("Nombre: " + cliente.getNombre() + " " + cliente.getApellido());
        System.out.println("Email: " + cliente.getEmail());
        System.out.println("Ciudad: " + cliente.getCiudad());
        System.out.println("Dirección: " + cliente.getDireccion());
        System.out.println("Teléfono: " + cliente.getTel());
        System.out.println("País: " + cliente.getPais());
        System.out.println("Fecha de ingreso: " + formateador.format(cliente.getFechaIngreso()));
        System.out.println("-------------------------------");
        MembresiaDAO membresiaDAO = new MembresiaDAO();
        Membresia membresia = membresiaDAO.obtenerMembresiaPorCedula(idcliente);
        System.out.println("Datos de la membresía:");
        if (membresia == null) {
            System.out.println("No se encontró una membresía para este cliente. Regístrela primero.");
        }
        else {
            System.out.println("------------------------------");
            System.out.println("Número de plan: " + membresia.getIdPlan());
            System.out.println("Fecha de inicio: " + formateador.format(membresia.getFechaInicio()));
            System.out.println("Fecha de fin: " + formateador.format(membresia.getFechaFin()));
            System.out.println("Estado: " + (membresia.getEstadoId() == 1 ? "Activa" : "Inactiva"));
            System.out.println("------------------------------");
            PlanDAO planDAO = new PlanDAO();
            Plan plan = null;
            String idplan = null;
            System.out.println("Planes activos:");
            List<Plan> listaPlan = planDAO.listarActivos();
            if (listaPlan == null) listaPlan = new ArrayList<>();
            if (listaPlan.isEmpty()) {
                System.out.println("No hay planes activos para realizar la renovación.");
                return;
            }
            listaPlan.sort(Comparator.comparingInt(Plan::getId));
            imprimirTablaPlan(listaPlan);
            while (plan == null) {
                System.out.print("Ingrese el número del plan: ");
                idplan = scanner.nextLine();
                plan = planDAO.buscarPorId(Integer.parseInt(idplan));
                if (plan == null) {
                    System.out.println("No se encontró el plan");
                }
            }
            try {
                Date fechaDeInicio = Date.valueOf(LocalDate.now());
                Date fechaDeFin = switch (plan.getDuracionUnidadId()) {
                    case 1 -> Date.valueOf(LocalDate.now().plusDays(plan.getDuracionTotal()));
                    case 2 -> Date.valueOf(LocalDate.now().plusWeeks(plan.getDuracionTotal()));
                    case 3 -> Date.valueOf(LocalDate.now().plusMonths(plan.getDuracionTotal()));
                    case 4 -> Date.valueOf(LocalDate.now().plusYears(plan.getDuracionTotal()));
                    default -> fechaDeInicio;
                };
                membresiaDAO.modificarMembresia(new Membresia(membresia.getId(), Integer.parseInt(idplan), idcliente, fechaDeInicio, fechaDeFin, 1));
                EventoMembresiaDAO eventoDAO = new EventoMembresiaDAO();
                eventoDAO.agregarEventoMembresia(new EventoMembresia(-1, 1, membresia.getId(), 1, Timestamp.from(Instant.now()), ""));
                System.out.println("Membresia renovada exitosamente. Fecha Inicio: " + formateador.format(fechaDeInicio) + ", Fecha Fin: " + formateador.format(fechaDeFin));
            } catch (Exception e) {
                System.out.println("Error al renovar la membresia" + e.getMessage());
            }
        }
    }

    public void membresiaCliente() {
        System.out.println("Lista de clientes:");
        ClienteDAO clienteDAO = new ClienteDAO();
        List<Cliente> lista = clienteDAO.listarTodos();
        if (lista == null) lista = new ArrayList<>();
        if (lista.isEmpty()) {
            System.out.println("No hay clientes registrados en el sistema.");
            return;
        }
        imprimirTablaCliente(lista);
        System.out.print("Ingrese la cédula del cliente: ");
        String idcliente = scanner.nextLine();
        Cliente cliente = clienteDAO.buscarPorCi(idcliente);
        if (cliente == null) {
            System.out.println("No se encontró el cliente");
            return;
        }
        System.out.println("Datos del cliente:");
        System.out.println("-------------------------------");
        System.out.println("CI: " + cliente.getCi());
        System.out.println("Nombre: " + cliente.getNombre() + " " + cliente.getApellido());
        System.out.println("Email: " + cliente.getEmail());
        System.out.println("Ciudad: " + cliente.getCiudad());
        System.out.println("Dirección: " + cliente.getDireccion());
        System.out.println("Teléfono: " + cliente.getTel());
        System.out.println("País: " + cliente.getPais());
        System.out.println("Fecha de ingreso: " + formateador.format(cliente.getFechaIngreso()));
        System.out.println("-------------------------------");
        MembresiaDAO membresiaDAO = new MembresiaDAO();
        Membresia membresia = membresiaDAO.obtenerMembresiaPorCedula(idcliente);
        System.out.println("Datos de la membresía:");
        if (membresia == null) {
            System.out.println("Este cliente no tiene una membresía.");
        }
        else {
            System.out.println("------------------------------");
            System.out.println("Número de plan: " + membresia.getIdPlan());
            System.out.println("Fecha de inicio: " + formateador.format(membresia.getFechaInicio()));
            System.out.println("Fecha de fin: " + formateador.format(membresia.getFechaFin()));
            System.out.println("Estado: " + (membresia.getEstadoId() == 1 ? "Activa" : "Inactiva"));
            System.out.println("------------------------------");
        }
    }

    public void listarMembresias() {
        MembresiaDAO membresiaDAO = new MembresiaDAO();
        membresiaDAO.listarMembresia();
    }

    public void eventosMembresia() {
        EventoMembresiaDAO eventoMembresiaDAO = new EventoMembresiaDAO();
        eventoMembresiaDAO.listarEventoMembresia();
    }

    public void mostrarMenu() {
        boolean seguir = true;
        while (seguir) {
            System.out.println("--- Membresia Flujo ---");
            System.out.println("1. Registrar membresía");
            System.out.println("2. Renovar membresía");
            System.out.println("3. Listar membresías");
            System.out.println("4. Ver membresía de cliente");
            System.out.println("5. Eventos membresías");
            System.out.println("6. Volver");
            System.out.print("Opcion: ");
            String opcion = scanner.nextLine();
            int opcionInt = 0;
            if (opcion.matches("\\d+")) { opcionInt = Integer.parseInt(opcion); }
            switch (opcionInt){
                case 1 -> registrarMembresia();
                case 2 -> renovarMembresia();
                case 3 -> listarMembresias();
                case 4 -> membresiaCliente();
                case 5 -> eventosMembresia();
                case 6 -> seguir = false;
                default -> System.out.println("Opcion invalida");
            }
        }
    }
    private void imprimirTablaCliente(List<Cliente> lista) {
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
                    nvl(formateador.format(c.getFechaIngreso()))
            }, anchos));
        }
    }

    private void imprimirTablaPlan(List<Plan> lista) { // formateamos el texto en tablas
        String header = String.format(
                "%-4s | %-24s | %-12s | %-8s | %-12s | %-8s | %-40s",
                "ID", "Nombre", "Valor", "DurTot", "Unidad", "Estado", "URL Imagen"
        );
        System.out.println("\n" + header);
        System.out.println("-".repeat(header.length()));
        for (Plan p : lista) {
            // usa el nombre; si viene null, cae al ID como último recurso
            String unidadNombre = (p.getDuracionUnidadNombre() != null && !p.getDuracionUnidadNombre().isBlank())
                    ? p.getDuracionUnidadNombre()
                    : String.valueOf(p.getDuracionUnidadId()); // fallback

            System.out.println(String.format(
                    "%-4d | %-24s | %-12s | %-8d | %-12s | %-8s | %-40s",
                    p.getId(),
                    nvl(p.getNombre()),
                    nvl(p.getValor()),
                    p.getDuracionTotal(),
                    nvl(unidadNombre),
                    (p.isEstado() ? "Activo" : "Inactivo"),
                    nvl(p.getUrlImagen())
            ));
        }
    }
}
