package com.example.gymtrackerweb.flow;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.*;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import com.example.gymtrackerweb.dto.*;

public final class UtilidadesFlujo {

    private static final Scanner IN = new Scanner(System.in);

    private UtilidadesFlujo() {} // no instanciable

    // basicos de entrada, usaremos todos? idk

    public static String leerNoVacio(String prompt) {
        String s;
        do {
            System.out.print(prompt);
            s = IN.nextLine().trim();
            if (s.isBlank()) System.out.println("El valor no puede ser vacío.");
        } while (s.isBlank());
        return s;
    }

    public static String leerOpcional(String prompt) {
        System.out.print(prompt);
        return IN.nextLine().trim();
    }

    public static int leerEntero(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = IN.nextLine().trim();
            try {
                return Integer.parseInt(s);
            } catch (Exception ignored) {
                System.out.println("Ingrese un número entero.");
            }
        }
    }

    public static int leerEnteroPositivo(String prompt) {
        int v;
        do {
            v = leerEntero(prompt);
            if (v <= 0) System.out.println("Debe ser > 0.");
        } while (v <= 0);
        return v;
    }

    public static short leerShortPositivo(String prompt) {
        return (short) leerEnteroPositivo(prompt);
    }

    public static byte leerBytePositivo(String prompt) {
        int v;
        do {
            v = leerEnteroPositivo(prompt);
            if (v > Byte.MAX_VALUE) System.out.println("Debe caber en un byte (<= 127).");
        } while (v > Byte.MAX_VALUE);
        return (byte) v;
    }

    public static BigDecimal leerBigDecimal(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = IN.nextLine().trim();
            s = s.replace(',', '.'); // admite coma o punto
            try {
                return new BigDecimal(s);
            } catch (Exception ignored) {
                System.out.println("Ingrese un decimal válido. Ej: 1499.99");
            }
        }
    }

    public static Date leerFechaSql(String prompt) {
        SimpleDateFormat formatoUsuario = new SimpleDateFormat("dd-MM-yyyy");
        formatoUsuario.setLenient(false); // Se supone que no permite fechas inválidas como 32-13-2025

        while (true) {
            System.out.print(prompt);
            String s = IN.nextLine().trim();
            try {
                // parseamo el string del usuario
                java.util.Date fecha = formatoUsuario.parse(s);

                // Cconvertimos a java.sql.Date
                return new Date(fecha.getTime());
            } catch (ParseException e) {
                System.out.println("Formato inválido. Use DD-MM-YYYY (ej: 17-09-2025).");
            }
        }
    }

    public static boolean leerBooleanSiNo(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = IN.nextLine().trim().toLowerCase(Locale.ROOT);
            if (s.equals("s")) return true;
            if (s.equals("n")) return false;
            System.out.println("Responda 's' o 'n'.");
        }
    }

    public static boolean confirmar(String prompt) {
        return leerBooleanSiNo(prompt);
    }

    // Parsers que ni se si voy a terminar usando

    public static BigDecimal parseBigDecimalOr(String raw, BigDecimal def) {
        if (raw == null || raw.isBlank()) return def;
        try { return new BigDecimal(raw.trim().replace(',', '.')); } catch (Exception ignored) { return def; }
    }

    public static int parseEnteroOr(String raw, int def) {
        if (raw == null || raw.isBlank()) return def;
        try { return Integer.parseInt(raw.trim()); } catch (Exception ignored) { return def; }
    }

    public static short parseShortOr(String raw, short def) {
        return (short) parseEnteroOr(raw, def);
    }

    public static byte parseByteOr(String raw, byte def) {
        return (byte) parseEnteroOr(raw, def);
    }

    public static boolean parseSiNoOr(String raw, boolean def) {
        if (raw == null || raw.isBlank()) return def;
        String s = raw.trim().toLowerCase(Locale.ROOT);
        if (s.equals("s")) return true;
        if (s.equals("n")) return false;
        return def;
    }

    public static Date parseFechaOr(String raw, Date def) {
        if (raw == null || raw.isBlank()) return def;
        try { return Date.valueOf(raw.trim()); } catch (Exception ignored) { return def; }
    }

    // ===== Formato / tabla =====

    public static String nvl(Object o) { return o == null ? "" : String.valueOf(o); }

    public static void printHeader(String[] headers, int[] widths) {
        String line = formatRow(headers, widths);
        System.out.println("\n" + line);
        System.out.println(repeat('-', line.length()));
    }

    public static String formatRow(Object[] values, int[] widths) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            String s = nvl(values[i]);
            if (s.length() > widths[i]) s = s.substring(0, widths[i]);
            sb.append(String.format("%-" + widths[i] + "s", s));
            if (i < values.length - 1) sb.append(" | ");
        }
        return sb.toString();
    }

    public static String repeat(char c, int n) {
        return String.valueOf(c).repeat(Math.max(0, n));
    }

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static LocalDateTime leerFechaHora(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = IN.nextLine().trim();
            try {
                return LocalDateTime.parse(s, FMT);
            } catch (Exception ignored) {
                System.out.println("Formato inválido. Use: YYYY-MM-DD HH:MM (ej: 2025-09-15 19:30)");
            }
        }
    }


    public static LocalDateTime parseFechaHoraOr(String raw, LocalDateTime def) {
        if (raw == null || raw.isBlank()) return def;
        try { return LocalDateTime.parse(raw.trim(), FMT); } catch (Exception ignored) { return def; }
    }

    public static long leerLongPositivo(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = IN.nextLine().trim();
            try {
                long v = Long.parseLong(s);
                if (v > 0) return v;
            } catch (Exception ignored) {}
            System.out.println("Ingrese un número entero > 0.");
        }
    }

    //esta me la goglee profe, no doy mas jajas
    public static Integer parseIntegerOr(String raw, Integer def) {
        if (raw == null || raw.isBlank()) return def;
        try {
            return Integer.valueOf(raw.trim());
        } catch (Exception ignored) {
            return def;
        }
    }
    //Correcciones Movimiento
    public static void imprimirOpcionesIdNombre(String titulo, List<IdNombre> ops) {
        System.out.println("\n-- " + titulo + " --");
        for (IdNombre o : ops) System.out.println("  " + o);
    }

    public static Integer seleccionarIdDeOpciones(List<IdNombre> ops, String prompt, boolean permitirVacio) {
        if (ops == null || ops.isEmpty()) {
            System.out.println("(No hay opciones disponibles)");
            return null;
        }
        while (true) {
            System.out.print(prompt);
            String s = IN.nextLine().trim();
            if (permitirVacio && s.isBlank()) return null;
            try {
                int id = Integer.parseInt(s);
                for (IdNombre o : ops) if (o.getId() == id) return id;
                System.out.println("ID inválido. Ingrese uno de la lista.");
            } catch (Exception ignored) {
                System.out.println("Ingrese un número de ID válido.");
            }
        }
    }

    public static void imprimirOpcionesClientes(String titulo, List<ClienteMin> ops) {
        System.out.println("\n-- " + titulo + " --");
        for (ClienteMin c : ops) System.out.println("  " + c);
    }

    public static String seleccionarClienteCI(List<ClienteMin> ops, String prompt, boolean permitirVacio) {
        if (ops == null || ops.isEmpty()) {
            System.out.println("(No hay clientes)");
            return null;
        }
        while (true) {
            System.out.print(prompt);
            String s = IN.nextLine().trim();
            if (permitirVacio && s.isBlank()) return null;
            for (ClienteMin c : ops) if (c.getCi().equals(s)) return s;
            System.out.println("CI inválido. Ingrese uno de la lista.");
        }
    }

    public static void imprimirOpcionesMembresias(String titulo, List<MembresiaMin> ops) {
        System.out.println("\n-- " + titulo + " --");
        for (MembresiaMin m : ops) System.out.println("  " + m);
    }

    public static Integer seleccionarMembresiaId(List<MembresiaMin> ops, String prompt, boolean permitirVacio) {
        if (ops == null || ops.isEmpty()) {
            System.out.println("(No hay membresías)");
            return null;
        }
        while (true) {
            System.out.print(prompt);
            String s = IN.nextLine().trim();
            if (permitirVacio && s.isBlank()) return null;
            try {
                int id = Integer.parseInt(s);
                for (MembresiaMin m : ops) if (m.getId() == id) return id;
                System.out.println("ID inválido. Ingrese uno de la lista.");
            } catch (Exception ignored) {
                System.out.println("Ingrese un número de ID válido.");
            }
        }
    }

    private static final DateTimeFormatter FMT_DAY = DateTimeFormatter.ofPattern("dd-MM-uuuu");

    public static LocalDateTime leerFechaDia(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = IN.nextLine().trim();
            try {
                LocalDate d = LocalDate.parse(s, FMT_DAY);
                return d.atStartOfDay(); // 00:00
            } catch (Exception ignored) {
                System.out.println("Formato inválido. Use DD-MM-YYYY (ej: 17-09-2025).");
            }
        }
    }
    public static LocalDateTime leerFechaDiaFin(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = IN.nextLine().trim();
            try {
                LocalDate d = LocalDate.parse(s, FMT_DAY);
                return d.plusDays(1).atStartOfDay(); // cubre tod0 el día ingresado
            } catch (Exception ignored) {
                System.out.println("Formato inválido. Use DD-MM-YYYY (ej: 17-09-2025).");
            }
        }
    }
}
