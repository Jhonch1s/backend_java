package com.example.gymtrackerweb.flow;
import com.example.gymtrackerweb.dao.PlanDAO;
import com.example.gymtrackerweb.model.Plan;

import com.example.gymtrackerweb.flow.UtilidadesFlujo;
import com.example.gymtrackerweb.model.UnidadDuracion;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class PlanFlujo {
    private final PlanDAO planDAO;
    private final Scanner in;

    public PlanFlujo() {
        planDAO = new PlanDAO();
        in = new Scanner(System.in);
    }

    public void run(){
        int op;
                do{
                    mostrarMenuPrincipal();
                    System.out.println("Opcion: ");
                    op = in.nextInt();
                    in.nextLine();
                    manejarOpcionPrincipal(op);
                }while (op!=0);
                System.out.println("Saliendo del menú Plan...");
    }

    private void mostrarMenuPrincipal() {
        System.out.println("\n=== MENU PLANES ===");
        System.out.println("1) Alta de plan");
        System.out.println("2) Baja (eliminar) plan");
        System.out.println("3) Modificar plan");
        System.out.println("4) Listar planes");
        System.out.println("5) Buscar plan por ID");
        System.out.println("0) Salir");
    }

    private void manejarOpcionPrincipal(int opcion){
        switch (opcion){
            case 1->altaPlan();
            case 2->bajaPlan();
            case 3->modificarPlan();
            case 4->listarMenu();
            case 5->buscarPorId();
            case 0-> {}//salir
            default -> System.out.println("Opcion no valida");
        }
    }

    //acciones del flujo
    private void altaPlan(){
        System.out.println("       Alta de plan");
        //como se repite mucho mucho lo de leer texto/dato teniendo que imprimir y escanear
        //busqué como ahorrar líneas a partir de funciones que de paso verifiquen que sean
        //datos válidos, esto podría ser transversal a tod0 el proyecto...
        String nombre = UtilidadesFlujo.leerNoVacio("Nombre: ");
        BigDecimal valor = UtilidadesFlujo.leerBigDecimal("Valor (Ej: 1459.99): ");
        short duracionTotal = (short)UtilidadesFlujo.leerEntero("Duracion total (numero): ");

        //correcciones
        List<UnidadDuracion> unidades = planDAO.listarUnidadesDuracion();
        System.out.println("\nUnidades de duración disponibles:");
        imprimirUnidadesDuracion(unidades);

        byte duracionUnidad;
        while (true) {
            duracionUnidad = (byte) UtilidadesFlujo.leerEntero("Duración unidad ID: ");
            if (planDAO.existeUnidadDuracion(duracionUnidad)) {
                break;
            }
            System.out.println("ID de unidad inexistente. Elija uno de la lista.");
        }
        //fin correcciones

        String urlImagen = UtilidadesFlujo.leerOpcional("URL Imagen (Opcional, enter para omitir): ");
        boolean estado = UtilidadesFlujo.leerBooleanSiNo("¿Activo? (s/n): ");

        Plan p = new Plan(nombre, valor, duracionTotal, duracionUnidad,
                (urlImagen.isBlank() ? null : urlImagen), estado); //breve chequeo para url.
        planDAO.agregarPlan(p);
    }

    private void bajaPlan(){
        System.out.println("       Baja de plan");
        int id = UtilidadesFlujo.leerEntero("ID del plan a eliiminar: ");
        if (UtilidadesFlujo.leerBooleanSiNo("¿Confirmar eliminacion? (s/n)")){
            boolean ok = planDAO.eliminarPlan(id);
            if (ok)System.out.println("Plan eliminado");
        }
    }

    private void modificarPlan(){
        System.out.println("       Modificar plan");
        listarOrdenado(planDAO.listarTodos());
        int id = UtilidadesFlujo.leerEntero("ID del plan a modificar: ");
        Plan actual = planDAO.buscarPorId(id);
        if (actual==null){
            System.out.println("No existe un plan con ese ID");
            return;
        }

        System.out.println("Valores actuales (Enter para mantener)");
        System.out.println(detallePlanLinea(actual)); //formateamos en texto de 1 linea

        //Aqui logramos que se imprima la linea sin saltear para que si el usr quiere modificar, deba alterar la linea y dar enter
        String nombre = UtilidadesFlujo.leerOpcional("Nombre [" + actual.getNombre() + "]: ");
        String valorStr = UtilidadesFlujo.leerOpcional("Valor [" + actual.getValor() + "]: ");
        String durTotStr = UtilidadesFlujo.leerOpcional("Duración total [" + actual.getDuracionTotal() + "]: ");
        byte unidadElegida = leerUnidadDuracionId(actual.getDuracionUnidadId());
        String urlImg = UtilidadesFlujo.leerOpcional("URL Imagen [" + actual.getUrlImagen() + "]: ");
        String estadoStr = UtilidadesFlujo.leerOpcional("¿Activo? (s/n) [" + (actual.isEstado() ? "s" : "n") + "]: ");

        //Chequeamos valores y actualizamos en actual
        if (!nombre.isBlank()) actual.setNombre(nombre);
        if (!valorStr.isBlank()) actual.setValor(parseBigDecimal(valorStr, actual.getValor()));
        if (!durTotStr.isBlank()) actual.setDuracionTotal((short) parseEntero(durTotStr, actual.getDuracionTotal()));
        actual.setDuracionUnidadId(unidadElegida);
        if (!urlImg.isBlank()) actual.setUrlImagen(urlImg);
        if (!estadoStr.isBlank()) actual.setEstado(parseSiNo(estadoStr, actual.isEstado()));

        int filas = planDAO.modificarPlan(actual);
        if (filas > 0) System.out.println("Modificado.");
    }

    private void listarMenu(){
        int op;
        do {
            System.out.println("        Listar");
            System.out.println("1) Listar todos");
            System.out.println("2) Listar activos");
            System.out.println("3) Listar inactivos");
            System.out.println("0) Volver");
            op = UtilidadesFlujo.leerEntero("Opción: ");
            switch (op) {
                case 1 -> listarOrdenado(planDAO.listarTodos());
                case 2 -> listarOrdenado(planDAO.listarActivos());
                case 3 -> listarOrdenado(planDAO.listarInactivos());
                case 0 -> { /* volver :p */ }
                default -> System.out.println("Opción inválida.");
            }
        } while (op != 0);
    }

    private void listarOrdenado(List<Plan> lista) {
        if (lista == null) lista = new ArrayList<>();
        if (lista.isEmpty()) {
            System.out.println("Sin resultados.");
            return;
        }
        //orden fijo por lo obtenido de dao
        lista.sort(Comparator.comparingInt(Plan::getId));
        imprimirTabla(lista);
    }

    private void buscarPorId() {
        System.out.println("\n--- Buscar por ID ---");
        int id = UtilidadesFlujo.leerEntero("ID: ");
        Plan p = planDAO.buscarPorId(id);
        if (p == null) {
            System.out.println("No encontrado.");
            return;
        }
        System.out.println("\n" + detallePlanBloque(p));
    }


    //Utilidades: Lecturas
    //Utilidades movidas a UtilidadesFlujo

    //Utilidades: mostrar
    private String detallePlanLinea(Plan p) {
        return String.format("(%d) %s | $%s | %d (uni %d) | %s | %s",
                p.getId(),
                nvl(p.getNombre()),
                nvl(p.getValor()),
                p.getDuracionTotal(),
                p.getDuracionUnidadId(),
                (p.isEstado() ? "Activo" : "Inactivo"),
                nvl(p.getUrlImagen()));
    }
    private String nvl(Object o) { return o == null ? "" : String.valueOf(o); }


    private void imprimirTabla(List<Plan> lista) { // formateamos el texto en tablas
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


    private String detallePlanBloque(Plan p) {
        String unidadNombre = planDAO.obtenerNombreUnidadDuracion(p.getDuracionUnidadId());
        String header = String.format(
                "%-4s | %-24s | %-12s | %-8s | %-12s | %-8s | %-40s",
                "ID", "Nombre", "Valor", "DurTot", "Unidad", "Estado", "URL Imagen");
                                                 //unidad y no id!
        String separator = "-".repeat(header.length());

        String row = String.format(
                "%-4d | %-24s | %-12s | %-8d | %-12s | %-8s | %-40s",
                p.getId(),
                nvl(p.getNombre()),
                nvl(p.getValor()),
                p.getDuracionTotal(),
                nvl(unidadNombre),                // ← nombre en vez de ID
                (p.isEstado() ? "Activo" : "Inactivo"),
                nvl(p.getUrlImagen())
        );

        return "\n" + header + "\n" + separator + "\n" + row;
    }




    //Utilidades: parsear valores
    private BigDecimal parseBigDecimal(String raw, BigDecimal def) {
        try {
            return new BigDecimal(raw.trim());
        } catch (Exception ignored) {
            return def;
        }
    }

    private int parseEntero(String raw, int def) {
        try {
            return Integer.parseInt(raw.trim());
        } catch (Exception ignored) {
            return def;
        }
    }

    private boolean parseSiNo(String raw, boolean def) {
        String s = raw.trim().toLowerCase(Locale.ROOT);
        if (s.equals("s")) return true;
        if (s.equals("n")) return false;
        return def;
    }

    //luego de corrección
    private void imprimirUnidadesDuracion(List<UnidadDuracion> unidades) {
        String header = String.format("%-6s | %-24s", "ID", "Nombre");
        System.out.println(header);
        System.out.println("-".repeat(header.length()));
        for (UnidadDuracion u : unidades) {
            System.out.println(String.format(
                    "%-6d | %-24s",
                    u.getId(),
                    nvl(u.getNombre())
            ));
        }
    }


    private byte leerUnidadDuracionId(byte valorActual) {
        List<UnidadDuracion> unidades = planDAO.listarUnidadesDuracion();
        System.out.println("\nUnidades de duración disponibles:");
        imprimirUnidadesDuracion(unidades);

        while (true) {
            String s = UtilidadesFlujo.leerOpcional(
                    "Unidad duración ID [" + valorActual + "]: ");

            if (s.isBlank()) return valorActual; // mantiene

            int v = parseEntero(s, -1);
            if (v < Byte.MIN_VALUE || v > Byte.MAX_VALUE) {
                System.out.println("Valor fuera de rango byte.");
                continue;
            }
            byte candidato = (byte) v;

            if (!planDAO.existeUnidadDuracion(candidato)) {
                System.out.println("ID de unidad inexistente. Ingrese uno de la lista.");
                continue;
            }
            return candidato; // válido
        }
    }
}
