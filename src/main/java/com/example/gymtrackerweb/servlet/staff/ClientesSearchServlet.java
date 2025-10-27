package com.example.gymtrackerweb.servlet.staff;

import com.example.gymtrackerweb.dao.ClienteDAO;
import com.example.gymtrackerweb.model.Cliente;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@WebServlet(name = "ClientesSearchServlet", value = "/api/clientes/search")
public class ClientesSearchServlet extends HttpServlet {

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Autenticación (ajusta clave de sesión si difiere)
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            writeJson(response, Map.of("success", false, "message", "Sesión no válida"));
            return;
        }

        request.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("application/json;charset=UTF-8");

        String ciQuery = Optional.ofNullable(request.getParameter("ci")).orElse("").trim();
        String limitStr = Optional.ofNullable(request.getParameter("limit")).orElse("8");

        if (ciQuery.isEmpty()) {
            // Para UX es útil devolver vacío en vez de 400
            writeJson(response, Map.of("success", true, "items", List.of()));
            return;
        }

        // Normalizamos CI: sacamos puntos/guiones/espacios
        String normalized = normalizeCi(ciQuery);

        int limit;
        try {
            limit = Math.min(Math.max(Integer.parseInt(limitStr), 1), 15);
        } catch (NumberFormatException e) {
            limit = 8;
        }

        try {
            // === Requiere un método DAO eficiente (con índice) ===
            // Sugerencia SQL (MySQL):
            // SELECT ci, nombre, apellido, email
            // FROM cliente
            // WHERE REPLACE(REPLACE(ci, '.', ''), '-', '') LIKE CONCAT(?, '%')
            // ORDER BY ci
            // LIMIT ?
            //
            // Implementalo en tu ClienteDAO como:
            // List<Cliente> buscarPorPrefijoCiNormalizado(String ciSinPuntos, int limit)
            List<Cliente> encontrados = clienteDAO.buscarPorPrefijoCiNormalizado(normalized, limit);

            // Reducimos payload: solo campos necesarios para sugerencias
            List<Map<String, Object>> items = encontrados.stream().map(c -> {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("ci", safe(c.getCi()));
                m.put("nombre", safe(c.getNombre()));
                m.put("apellido", safe(c.getApellido()));
                m.put("email", safe(c.getEmail()));
                return m;
            }).collect(Collectors.toList());

            writeJson(response, Map.of("success", true, "items", items));

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writeJson(response, Map.of("success", false, "message", "Error buscando clientes: " + e.getMessage()));
        }
    }

    private static String normalizeCi(String s) {
        return s.replace(".", "").replace("-", "").replace(" ", "");
    }

    private static String safe(String s) { return s == null ? "" : s; }

    private void writeJson(HttpServletResponse resp, Object payload) throws IOException {
        resp.getWriter().write(gson.toJson(payload));
    }
}
