package com.example.gymtrackerweb.servlet.staff;

import com.example.gymtrackerweb.dao.ClienteDAO;
import com.example.gymtrackerweb.dao.ClienteFotoDAO;
import com.example.gymtrackerweb.model.Cliente;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@WebServlet(name = "ClienteDetalleServlet", urlPatterns = {"/api/clientes/detalle"})
public class ClienteDetalleServlet extends HttpServlet {

    private final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private ClienteDAO clienteDAO;
    private ClienteFotoDAO clienteFotoDAO;

    @Override
    public void init() throws ServletException {
        // Ajustá si usás inyección/Singleton propio
        this.clienteDAO = new ClienteDAO();
        this.clienteFotoDAO = new ClienteFotoDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setContentType("application/json; charset=UTF-8");

        String ciRaw = req.getParameter("ci");
        String ci = normalizeCi(ciRaw);

        if (ci == null || ci.isBlank()) {
            writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "Parámetro 'ci' es requerido.");
            return;
        }

        try {
            Optional<Cliente> clienteOpt = Optional.ofNullable(clienteDAO.buscarPorCi(ci)); // asegurate que exista este método
            if (clienteOpt.isEmpty()) {
                writeError(resp, HttpServletResponse.SC_NOT_FOUND, "No existe un cliente con esa CI.");
                return;
            }

            Cliente c = clienteOpt.get();

            // Foto (opcional)
            Optional<String> fotoUrlOpt = clienteFotoDAO.obtenerUrlPorCliente(ci);
            String fotoUrl = fotoUrlOpt.orElse(null);

            // Formateo de fechaIngreso a yyyy-MM-dd (o null)
            String fechaIngresoStr = null;
            Date fi = c.getFechaIngreso();
            if (fi != null) {
                fechaIngresoStr = fi.toLocalDate().format(DATE_FMT);
            }

            // Construcción JSON liviana sin dependencias externas
            String json =
                    "{"
                            + "\"success\":true,"
                            + "\"cliente\":{"
                            + "\"ci\":\"" + esc(c.getCi()) + "\","
                            + "\"nombre\":\"" + esc(nvl(c.getNombre())) + "\","
                            + "\"apellido\":\"" + esc(nvl(c.getApellido())) + "\","
                            + "\"email\":\"" + esc(nvl(c.getEmail())) + "\","
                            + "\"tel\":\"" + esc(nvl(c.getTel())) + "\","
                            + "\"ciudad\":\"" + esc(nvl(c.getCiudad())) + "\","
                            + "\"direccion\":\"" + esc(nvl(c.getDireccion())) + "\","
                            + "\"pais\":\"" + esc(nvl(c.getPais())) + "\","
                            + "\"fechaIngreso\":" + (fechaIngresoStr == null ? "null" : ("\"" + esc(fechaIngresoStr) + "\""))
                            + "},"
                            + "\"fotoUrl\":" + (fotoUrl == null ? "null" : ("\"" + esc(fotoUrl) + "\""))
                            + "}";

            try (PrintWriter out = resp.getWriter()) {
                out.write(json);
            }

        } catch (Exception ex) {
            // Logueá el error en tu logger
            writeError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al obtener el detalle del cliente.");
        }
    }

    // --- Helpers ---

    /** Normaliza CI quitando puntos y guiones. */
    private String normalizeCi(String ci) {
        if (ci == null) return null;
        return ci.replaceAll("[.\\-\\s]", "").trim();
    }

    /** Escapa cadenas para JSON (mínimo indispensable). */
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
                    if (ch < 0x20) {
                        sb.append(String.format("\\u%04x", (int) ch));
                    } else {
                        sb.append(ch);
                    }
            }
        }
        return sb.toString();
    }

    private String nvl(String s) { return (s == null) ? "" : s; }

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
