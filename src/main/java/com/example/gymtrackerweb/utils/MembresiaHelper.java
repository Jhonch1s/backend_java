package com.example.gymtrackerweb.utils;

import com.example.gymtrackerweb.dto.ClienteListadoDTO;
import com.example.gymtrackerweb.dto.ClienteListadoDTO.EstadoTemporal;
import com.example.gymtrackerweb.dto.ClienteListadoDTO.SemaforoWhatsapp;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Locale;

public final class MembresiaHelper {

    private static final ZoneId ZONA_UY = ZoneId.of("America/Montevideo");

    private MembresiaHelper() {}

    // calculo de bucket temporal

    /**
     * Regla:
     * - Si estado administrativo es PAUSADA/CANCELADA/BAJA/INACTIVA -> bucket = SIN (no empujamos por WhatsApp)
     * - Si no hay fecha_fin -> SIN
     * - Si estado es ACTIVA o TRIAL:
     *      fecha_fin > hoy      -> ACTIVA
     *      fecha_fin = hoy      -> VENCE_HOY
     *      1..10 días vencida   -> VENCIDA_LT10
     *      >10 días vencida     -> VENCIDA_GTE10
     */
    public static EstadoTemporal calcularBucketTemporal(String estadoNombre, Date fechaFinSql, LocalDate hoyUy) {
        String estado = safe(estadoNombre);
        if (hoyUy == null) hoyUy = LocalDate.now(ZONA_UY);

        // Estados administrativos que anulan empuje (no temporal)
        if (isAny(estado, "pausada", "cancelada", "baja", "inactivo")) {
            return EstadoTemporal.SIN;
        }

        // Sin fecha fin => no podemos calcular vencimiento
        if (fechaFinSql == null) {
            return EstadoTemporal.SIN;
        }

        LocalDate fechaFin = fechaFinSql.toLocalDate();

        // Solo aplicamos bucket temporal para 'activa' o 'trial'
        if (isAny(estado, "activa","activo", "trial")) {
            if (fechaFin.isAfter(hoyUy)) return EstadoTemporal.ACTIVA;
            if (fechaFin.isEqual(hoyUy)) return EstadoTemporal.VENCE_HOY;

            int dias = (int) java.time.temporal.ChronoUnit.DAYS.between(fechaFin, hoyUy);
            if (dias <= 10) return EstadoTemporal.VENCIDA_LT10;
            return EstadoTemporal.VENCIDA_GTE10;
        }

        // Cualquier otro estado desconocido se trata como SIN
        return EstadoTemporal.SIN;
    }

    public static Integer diasDesdeVencimiento(EstadoTemporal bucket, Date fechaFinSql, LocalDate hoyUy) {
        if (bucket == null || fechaFinSql == null) return null;
        if (hoyUy == null) hoyUy = LocalDate.now(ZONA_UY);

        switch (bucket) {
            case VENCE_HOY:
            case ACTIVA:
            case SIN:
                return null;
            case VENCIDA_LT10:
            case VENCIDA_GTE10: {
                LocalDate fechaFin = fechaFinSql.toLocalDate();
                return (int) java.time.temporal.ChronoUnit.DAYS.between(fechaFin, hoyUy);
            }
            default:
                return null;
        }
    }

    // WhatsApp semáforo  mensaje
    public static SemaforoWhatsapp calcularSemaforo(String estadoNombre, EstadoTemporal bucket, String telRaw) {
        if (isBlank(telRaw)) return SemaforoWhatsapp.GRIS;

        String estado = safe(estadoNombre);
        if (isAny(estado, "pausada", "cancelada", "baja", "inactiva", "inactivo")) {
            return SemaforoWhatsapp.GRIS;
        }

        if (bucket == null) return SemaforoWhatsapp.GRIS;
        switch (bucket) {
            case ACTIVA:      return SemaforoWhatsapp.VERDE;
            case VENCE_HOY:
            case VENCIDA_LT10:return SemaforoWhatsapp.ROJO;
            case VENCIDA_GTE10:
            case SIN:return SemaforoWhatsapp.AMARILLO;
            default:          return SemaforoWhatsapp.GRIS;
        }
    }
    /** para vencidos >=10 días o sin membresía */
    public static String mensajeRecupero(String nombreCompleto) {
        String nombre = safe(nombreCompleto);
        nombre = nombre.isEmpty() ? "¡Hola!" : "¡Hola " + nombre + "!";
        return nombre + " Soy del Golden Gym.\n"
                + "Hace un tiempo que no te vemos por el gym y nos encantaría que vuelvas :).\n"
                + "Tenemos planes y horarios flexibles; si querés, te paso opciones y promos.\n"
                + "¿Te reservo una visita esta semana?";
    }

    /** rojo para reenganche. */
    public static String mensajeReenganche(String nombreCompleto) {
        String nombre = safe(nombreCompleto);
        if (nombre.isEmpty()) nombre = "¡Hola!";
        else nombre = "¡Hola " + nombre + "!";

        return nombre + " Soy del Golden Gym\n"
                + "Vimos que tu membresía venció hace pocos días y queremos invitarte a reingresar :).\n"
                + "Si retomás ahora, no abonás matriculación por lo reciente de la pausa.\n"
                + "¿Te reservo un lugar para esta semana?";
    }

    public static String urlEncode(String raw) {
        return URLEncoder.encode(raw, StandardCharsets.UTF_8);
    }

    public static String normalizarTelefonoParaWa(String telRaw) {
        if (isBlank(telRaw)) return null;

        String t = telRaw.trim();
        // quitar todo lo que no sea dígito o '+'
        t = t.replaceAll("[^\\d+]", "");
        // si comienza con '+', removerlo (wa.me no lo usa)
        if (t.startsWith("+")) t = t.substring(1);

        // si faltó código país (asumimos UY = 598)
        if (!t.startsWith("598")) {
            // quitar ceros iniciales comunes en celulares locales (099 -> 99)
            t = t.replaceFirst("^0+", "");
            t = "598" + t;
        }

        // última sanitización: solo dígitos
        t = t.replaceAll("\\D", "");
        return t.isEmpty() ? null : t;
    }

    // ======= Utilitarios =======

    private static boolean isAny(String s, String... opciones) {
        for (String op : opciones) if (s.equals(op)) return true;
        return false;
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static String safe(String s) {
        return s == null ? "" : s.trim().toLowerCase(Locale.ROOT);
    }

    // ======= Builder conveniente para el DTO (opcional) =======

    /**
     * Llena campos del DTO relacionados a membresía/whatsapp,
     * dado estado administrativo, fin de membresía y teléfono crudo.
     */
    public static void rellenarCamposMembresia(ClienteListadoDTO dto, String estadoNombre, Date fechaFinSql, String telRaw, LocalDate hoyUy) {
        if (dto == null) return;
        if (hoyUy == null) hoyUy = LocalDate.now(ZONA_UY);

        // Calcular bucket y días
        EstadoTemporal bucket = calcularBucketTemporal(estadoNombre, fechaFinSql, hoyUy);
        Integer diasVenc = diasDesdeVencimiento(bucket, fechaFinSql, hoyUy);

        dto.setEstadoNombre(estadoNombre);
        dto.setFechaFinMembresia(fechaFinSql);
        dto.setBucketTemporal(bucket);
        dto.setDiasDesdeVencimiento(diasVenc);

        // Teléfono + semáforo
        String telNorm = normalizarTelefonoParaWa(telRaw);
        dto.setTelNormalizado(telNorm);

        SemaforoWhatsapp sem = calcularSemaforo(estadoNombre, bucket, telNorm);
        dto.setWhatsappSemaforo(sem);
        dto.setBucketTemporalStr(bucket != null ? bucket.name() : null);
        dto.setWhatsappSemaforoStr(sem != null ? sem.name() : null);


        // Mensaje de reenganche solo si corresponde "rojo"
        switch (sem) {
            case ROJO -> dto.setMensajeReenganche(mensajeReenganche(dto.getNombreCompleto()));
            case AMARILLO -> dto.setMensajeReenganche(mensajeRecupero(dto.getNombreCompleto()));
            default -> dto.setMensajeReenganche(null);
        }
    }
}
