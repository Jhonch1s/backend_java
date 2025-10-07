package com.example.gymtrackerweb.flow;
import com.example.gymtrackerweb.dao.MovimientoDAO;
import com.example.gymtrackerweb.model.Movimiento;
import com.example.gymtrackerweb.dto.*;
import com.example.gymtrackerweb.dao.MovimientoDetalleDAO;
import static com.example.gymtrackerweb.flow.UtilidadesFlujo.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class MovimientoFlujo  {
    MovimientoDAO dao = new MovimientoDAO();
    MovimientoDetalleDAO daoDetalle = new MovimientoDetalleDAO();

    public void run() {
        int op;
        do {
            mostrarMenu();
            op = leerEntero("Opción: ");
            switch (op) {
                case 1 -> altaMovimiento();
                case 2 -> listarTodosView();
                case 3 -> listarPorRangoView();
                case 4 -> listarPorStaffView();
                case 5 -> sumarImportesPeriodo();
                case 6 -> eliminarMovimiento();
                case 0 -> System.out.println("Saliendo de MovimientoFlujo...");
                default -> System.out.println("Opción inválida.");
            }
        } while (op != 0);
    }

    private void mostrarMenu() {
        System.out.println("\n=== MENÚ MOVIMIENTOS ===");
        System.out.println("1) Alta de movimiento");
        System.out.println("2) Listar todos");
        System.out.println("3) Listar por rango de fechas");
        System.out.println("4) Listar por staff (con rango opcional)");
        System.out.println("5) Sumar importes por período");
        System.out.println("6) Eliminar movimiento por ID");
        System.out.println("0) Salir");
    }

    //Acciones del flujo
    private void altaMovimiento() {
        System.out.println("\n--- Alta de Movimiento ---");

        int idStaff = leerEnteroPositivo("ID Staff: ");

        // Fecha/hora opcional: Enter -> ahora (null aquí; el DAO usa COALESCE/CURRENT_TIMESTAMP)
        String fhRaw = leerOpcional("Fecha y hora (YYYY-MM-DD HH:MM) [Enter = ahora]: ");
        LocalDateTime fechaHora = parseFechaHoraOr(fhRaw, null);

        BigDecimal importe;
        while (true) {
            importe = leerBigDecimal("Importe (ej: 1499.99): ");
            if (importe != null && importe.compareTo(BigDecimal.ZERO) > 0) break;
            System.out.println("El importe debe ser > 0.");
        }

        // 1) Medio de Pago
        List<IdNombre> medios;
        try { medios = dao.listarMediosPago(); }
        catch (Exception e) { System.out.println("Error cargando medios de pago: " + e.getMessage()); return; }
        imprimirOpcionesIdNombre("Medios de Pago", medios);
        Integer medioPagoSel = seleccionarIdDeOpciones(medios, "Ingrese ID de Medio de Pago: ", false);
        if (medioPagoSel == null || medioPagoSel > Byte.MAX_VALUE) { System.out.println("ID fuera de rango byte."); return; }
        byte medioPagoId = (byte) (int) medioPagoSel;

        // 2) Tipo de Cliente
        List<IdNombre> tipos;
        try { tipos = dao.listarTiposCliente(); }
        catch (Exception e) { System.out.println("Error cargando tipos de cliente: " + e.getMessage()); return; }
        imprimirOpcionesIdNombre("Tipos de Cliente", tipos);
        Integer tipoCliSel = seleccionarIdDeOpciones(tipos, "Ingrese ID de Tipo de Cliente: ", false);
        if (tipoCliSel == null || tipoCliSel > Byte.MAX_VALUE) { System.out.println("ID fuera de rango byte."); return; }
        byte tipoClienteId = (byte) (int) tipoCliSel;

        // 3) Origen Movimiento
        List<IdNombre> origenes;
        try { origenes = dao.listarOrigenesMovimiento(); }
        catch (Exception e) { System.out.println("Error cargando orígenes: " + e.getMessage()); return; }
        imprimirOpcionesIdNombre("Orígenes de Movimiento", origenes);
        Integer origenSel = seleccionarIdDeOpciones(origenes, "Ingrese ID de Origen: ", false);
        if (origenSel == null || origenSel > Byte.MAX_VALUE) { System.out.println("ID fuera de rango byte."); return; }
        byte origenId = (byte) (int) origenSel;

        // 4) Cliente (opcional)
        List<ClienteMin> clientes;
        try { clientes = dao.listarClientesMin(); }
        catch (Exception e) { System.out.println("Error cargando clientes: " + e.getMessage()); return; }
        imprimirOpcionesClientes("Clientes (CI - Nombre)", clientes);
        String idCliente = seleccionarClienteCI(clientes, "Ingrese CI de Cliente (Enter para omitir): ", true); // null si omite

        // 5) Membresías (opcional) — filtradas por cliente si se eligió uno
        List<MembresiaMin> membresias;
        try {
            membresias = dao.listarMembresiasMin();
            if (idCliente != null) {
                final String ciSel = idCliente;
                membresias.removeIf(m -> !Objects.equals(ciSel, m.getCiCliente())); // null-safe
            }
        } catch (Exception e) {
            System.out.println("Error cargando membresías: " + e.getMessage());
            return;
        }
        imprimirOpcionesMembresias("Membresías (ID - CI - Nombre)", membresias);
        Integer idMembresia = seleccionarMembresiaId(membresias, "Ingrese ID de Membresía (Enter para omitir): ", true); // null si omite

        // 6) Regla: al menos uno de cliente/membresía
        if (idCliente == null && idMembresia == null) {
            System.out.println("Debe indicar al menos Membresía o CI de Cliente.");
            return;
        }
        // 6) verificamos que la membresía pertenezca a ese cliente
        if (idCliente != null && idMembresia != null) {
            try {
                if (!dao.membresiaPerteneceACliente(idMembresia, idCliente)) {
                    System.out.println("La membresía #" + idMembresia + " no pertenece al cliente " + idCliente + ".");
                    return;
                }
            } catch (Exception e) {
                System.out.println("Error validando membresía/cliente: " + e.getMessage());
                return;
            }
        }

        // Construir e insertar
        Movimiento m = new Movimiento(
                idStaff, fechaHora, importe,
                medioPagoId, tipoClienteId, origenId,
                idMembresia, idCliente
        );
        dao.insertarMovimiento(m);
        System.out.println("Movimiento insertado.");
    }


    //listar tod0
    private void listarTodos() {
        System.out.println("\n--- Listar todos (orden fecha DESC) ---");
        List<Movimiento> lista;
        try {
            lista = dao.listarTodos(); // existente
        } catch (Exception e) {
            System.out.println("Error al listar: " + e.getMessage());
            return;
        }
        if (lista == null) lista = new ArrayList<>();
        if (lista.isEmpty()) {
            System.out.println("Sin resultados.");
            return;
        }
        // Orden por fecha desc para presentación (por si el SQL alguna vez cambia)
        lista.sort(Comparator.comparing(Movimiento::getFechaHora, Comparator.nullsLast(Comparator.naturalOrder())).reversed());
        imprimirTabla(lista);
    }
    private void listarPorRango() {
        System.out.println("\n     Listar por rango ");
        LocalDateTime desde = leerFechaDia("Desde (DD-MM-YYYY): ");      // 00:00 del día
        LocalDateTime hasta = leerFechaDiaFin("Hasta (DD-MM-YYYY): ");   // 00:00 del día SIGUIENTE
        List<Movimiento> lista;
        try {
            lista = dao.listarPorRango(desde, hasta);
        } catch (Exception e) {
            System.out.println("Error al listar por rango: " + e.getMessage());
            return;
        }
        if (lista.isEmpty()) {
            System.out.println("Sin resultados en ese rango.");
            return;
        }
        imprimirTabla(lista);
    }
    //ver los movimientos hechos por tal o cual staff, con opcion de si queremos darle un rango (por ej ver mes de un entrenador)
    private void listarPorStaff() {
        System.out.println("\n      Listar por staff");
        int idStaff = leerEnteroPositivo("ID Staff: ");
        String usarRango = leerOpcional("¿Filtrar por rango? (s/n, Enter = n): ").trim().toLowerCase();
        List<Movimiento> lista = null;
        try {
            if (usarRango.equals("s")) {
                LocalDateTime desde = leerFechaDia("Desde (DD-MM-YYYY): ");      // 00:00 del día
                LocalDateTime hasta = leerFechaDiaFin("Hasta (DD-MM-YYYY): ");   // 00:00 del día SIGUIENTE
                try {
                    lista = dao.listarPorStaff(idStaff, desde, hasta); //polimorfismo aqui
                } catch (Exception e) {
                    System.out.println("Error al listar por rango: " + e.getMessage());
                }
            } else {
                lista = dao.listarPorStaff(idStaff);
            }
        } catch (Exception e) {
            System.out.println("Error al listar por staff: " + e.getMessage());
            return;
        }
        if (lista.isEmpty()) {
            System.out.println("Sin resultados.");
            return;
        }
        imprimirTabla(lista);
    }

    private void sumarImportesPeriodo() {
        System.out.println("\n     Sumar importes de plazo");
        LocalDateTime desde = leerFechaDia("Desde (DD-MM-YYYY): ");      // 00:00 del día
        LocalDateTime hasta = leerFechaDiaFin("Hasta (DD-MM-YYYY): ");   // 00:00 del día SIGUIENTE
        try {
            BigDecimal total = dao.sumarImportes(desde, hasta);
            System.out.println("Total: " + total);
        } catch (Exception e) {
            System.out.println("Error al sumar importes: " + e.getMessage());
        }
    }

    private void eliminarMovimiento() {
        listarTodosView();
        System.out.println("\n      Eliminar movimiento ");
        long idMov = leerLongPositivo("ID del movimiento: ");
        if (!confirmar("¿Confirmar eliminacion? (s/n): ")) return;
        try {
            dao.eliminarMovimiento(idMov); // existente
            System.out.println("Eliminado.");
        } catch (Exception e) {
            System.out.println("Error al eliminar: " + e.getMessage());
        }
    }

    //Nuevos con View!
    private void listarTodosView() {
        System.out.println("\n      Listar todos ");
        List<MovimientoView> lista;
        try {
            lista = dao.listarViewTodos();
        } catch (Exception e) {
            System.out.println("Error al listar: " + e.getMessage());
            return;
        }
        imprimirTablaViewOrdenada(lista);
    }

    private void listarPorRangoView() {
        System.out.println("\n       Listar por rango (nombres)");
        LocalDateTime desde = leerFechaDia("Desde (DD-MM-YYYY): ");      // 00:00 del día
        LocalDateTime hasta = leerFechaDiaFin("Hasta (DD-MM-YYYY): ");   // 00:00 del día SIGUIENTE
        List<MovimientoView> lista;
        try {
            lista = dao.listarViewPorRango(desde, hasta);
        } catch (Exception e) {
            System.out.println("Error al listar por rango: " + e.getMessage());
            return;
        }
        imprimirTablaViewOrdenada(lista);
    }

    private void listarPorStaffView() {
        System.out.println("\n      Listar por staff (nombres)");
        int idStaff = leerEnteroPositivo("ID Staff: ");
        String usarRango = leerOpcional("¿Filtrar por rango? (s/n, Enter=n): ").trim().toLowerCase();
        List<MovimientoView> lista;
        try {
            if (usarRango.equals("s")) {
                LocalDateTime desde = leerFechaDia("Desde (DD-MM-YYYY): ");      // 00:00 del día
                LocalDateTime hasta = leerFechaDiaFin("Hasta (DD-MM-YYYY): ");   // 00:00 del día SIGUIENTE
                lista = dao.listarViewPorStaff(idStaff, desde, hasta);
            } else {
                lista = dao.listarViewPorStaff(idStaff);
            }
        } catch (Exception e) {
            System.out.println("Error al listar por staff: " + e.getMessage());
            return;
        }
        imprimirTablaViewOrdenada(lista);
    }
    //fromateamos a tabla
    private void imprimirTabla(List<Movimiento> lista) {
        String[] headers = {"ID", "FechaHora", "Importe", "Staff", "Medio", "TipoCli", "Origen", "Memb.", "Cliente"};
        int[] anchos = {6, 16, 12, 6, 5, 7, 6, 6, 12};
        printHeader(headers, anchos);
        for (Movimiento m : lista) {
            System.out.println(formatRow(new Object[]{
                    nvl(m.getIdMov()),
                    nvl(m.getFechaHora()),      // ISO-8601
                    nvl(m.getImporte()),
                    nvl(m.getIdStaff()),
                    nvl(m.getMedioPagoID()),
                    nvl(m.getTipoClienteID()),
                    nvl(m.getOrigenId()),
                    nvl(m.getIdMembresia()),
                    nvl(m.getIdCliente())
            }, anchos));
        }
    }

    //formateamos tabla view
    private void imprimirTablaViewOrdenada(List<MovimientoView> lista) {
        if (lista == null) lista = new ArrayList<>();
        if (lista.isEmpty()) {
            System.out.println("Sin resultados.");
            return;
        }

        String[] headers = {"ID", "FechaHora", "Importe", "Staff", "MedioPago", "TipoCliente", "Origen", "Memb.", "Cliente"};
        int[] anchos= {6, 16, 12, 14, 12, 14, 12, 6, 20};
        printHeader(headers, anchos);

        for (MovimientoView v : lista) {
            System.out.println(formatRow(new Object[]{
                    nvl(v.getIdMov()),
                    nvl(v.getFechaHora()),
                    nvl(v.getImporte()),
                    nvl(v.getStaffNombre()),
                    nvl(v.getMedioPagoNombre()),
                    nvl(v.getTipoClienteNombre()),
                    nvl(v.getOrigenNombre()),
                    nvl(v.getIdMembresia()),
                    nvl(v.getClienteNombre())
            }, anchos));
        }
    }

    //Utilidades de movimiento:
    private Integer nullInt() { return null; }
}
