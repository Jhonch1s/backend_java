package com.example.gymtrackerweb.servlet.staff;

import com.example.gymtrackerweb.dao.ClienteDAO;
import com.example.gymtrackerweb.dao.RegistroGymDAO;
import com.example.gymtrackerweb.dao.MembresiaDAO;
import com.example.gymtrackerweb.dao.PlanDAO;

import com.example.gymtrackerweb.model.Cliente;
import com.example.gymtrackerweb.model.Membresia;
import com.example.gymtrackerweb.model.Plan;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@WebServlet(name = "ClienteResumenServlet", urlPatterns = {"/api/clientes/resumen"})
public class ClienteResumenServlet extends HttpServlet {

    private final ZoneId ZONE_UY = ZoneId.of("America/Montevideo");
    private final DateTimeFormatter FMT_HUMAN = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private ClienteDAO clienteDAO;
    private RegistroGymDAO registroGymDAO;
    private MembresiaDAO membresiaDAO;
    private PlanDAO planDAO;

    @Override
    public void init() throws ServletException {
        this.clienteDAO = new ClienteDAO();
        this.registroGymDAO = new RegistroGymDAO();
        this.membresiaDAO = new MembresiaDAO();
        this.planDAO = new PlanDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setContentType("application/json; charset=UTF-8");

        final String ci = normalizeCi(req.getParameter("ci"));
        if (ci == null || ci.isBlank()) {
            writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "Parámetro 'ci' es requerido.");
            return;
        }

        try {
            Optional<Cliente> clienteOpt = Optional.ofNullable(clienteDAO.buscarPorCi(ci));
            if (clienteOpt.isEmpty()) {
                writeError(resp, HttpServletResponse.SC_NOT_FOUND, "No existe un cliente con esa CI.");
                return;
            }

            // === KPIs (usando tus firmas reales) ===
            LocalDate hoyUy = LocalDate.now(ZONE_UY);
            int visitasMes       = safe(registroGymDAO.totalSesionesMes(ci, hoyUy));
            Integer minPromMes   = registroGymDAO.minutosPromedioPorSesionMes(ci, hoyUy);
            int entrenosTotales  = safe(registroGymDAO.totalSesionesHistorico(ci));

            // === Membresía + Plan (nombre desde PlanDAO usando id_plan de Membresia) ===
            String planNombre = null;
            String planImgUrl = null;
            String membresiaEstado = "SIN_MEMBRESIA";
            String venceHuman = null;
            String venceIso   = null;

            try {
                Membresia m = membresiaDAO.obtenerMembresiaPorCedula(ci);
                if (m != null) {
                    // Estado 1 activo, 0 inactivo (según tu comentario)
                    boolean activo = (m.getEstadoId() == 1);

                    // Fecha de fin (manejo robusto por si el tipo varía)
                    LocalDate vence = null;
                    Object fechaFin = m.getFechaFin(); // adapta si tu getter devuelve LocalDate directamente
                    if (fechaFin != null) {
                        if (fechaFin instanceof LocalDate) {
                            vence = (LocalDate) fechaFin;
                        } else if (fechaFin instanceof java.sql.Date) {
                            vence = ((java.sql.Date) fechaFin).toLocalDate();
                        }
                    }
                    if (vence != null) {
                        venceHuman = vence.format(FMT_HUMAN);
                        venceIso   = vence.toString();
                        boolean vencida = vence.isBefore(hoyUy);
                        membresiaEstado = activo ? (vencida ? "VENCIDA" : "ACTIVA") : "INACTIVA";
                    } else {
                        // Sin fecha de fin: basamos en estado
                        membresiaEstado = activo ? "ACTIVA" : "INACTIVA";
                    }

                    // Nombre del plan a partir de id_plan (int)
                    int idPlan = m.getIdPlan(); // primitivo
                    if (idPlan > 0) {
                        try {
                            Plan plan = planDAO.buscarPorId(idPlan); // ajusta al nombre real si difiere
                            if (plan != null) {
                                planNombre = nvl(plan.getNombre());
                                planImgUrl = nvl(plan.getUrlImagen()); // NUEVO: obtenemos url de imagen
                            }
                        } catch (Exception ignore) { /* noop */ }
                    }

                }
            } catch (Exception ignore) {
                // dejar valores por defecto
            }

            // === JSON ===
            String json =
                    "{"
                            + "\"success\":true,"
                            + "\"resumen\":{"
                            + "\"ci\":\"" + esc(ci) + "\","
                            + "\"visitasMes\":" + visitasMes + ","
                            + "\"minutosPromedioMes\":" + (minPromMes == null ? "null" : minPromMes) + ","
                            + "\"entrenosTotales\":" + entrenosTotales + ","
                            + "\"planNombre\":" + (planNombre == null ? "null" : ("\"" + esc(planNombre) + "\"")) + ","
                            + "\"planImgUrl\":" + (planImgUrl == null ? "null" : ("\"" + esc(planImgUrl) + "\"")) + ","
                            + "\"membresia\":{"
                            + "\"estado\":\"" + esc(membresiaEstado) + "\","
                            + "\"venceHuman\":" + (venceHuman == null ? "null" : ("\"" + esc(venceHuman) + "\"")) + ","
                            + "\"venceIso\":" + (venceIso == null ? "null" : ("\"" + esc(venceIso) + "\""))
                            + "}"
                            + "}"
                            + "}";

            try (PrintWriter out = resp.getWriter()) {
                out.write(json);
            }

        } catch (Exception ex) {
            writeError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al obtener el resumen del cliente.");
        }
    }

    // ==== Helpers ====
    private String normalizeCi(String ci) {
        if (ci == null) return null;
        return ci.replaceAll("[.\\-\\s]", "").trim();
    }

    private int safe(Integer n) { return n == null ? 0 : n; }

    private String nvl(String s) { return s == null ? "" : s; }

    private String esc(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder(s.length() + 16);
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            switch (ch) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\b': sb.append("\\b"); break;
                case '\f': sb.append("\\f"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (ch < 0x20) sb.append(String.format("\\u%04x", (int) ch));
                    else sb.append(ch);
            }
        }
        return sb.toString();
    }

    private void writeError(HttpServletResponse resp, int status, String message) throws IOException {
        resp.setStatus(status);
        String json = "{"
                + "\"success\":false,"
                + "\"message\":\"" + esc(message) + "\""
                + "}";
        try (PrintWriter out = resp.getWriter()) {
            out.write(json);
        }
    }
}
