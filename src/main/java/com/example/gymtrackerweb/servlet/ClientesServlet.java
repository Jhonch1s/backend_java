package com.example.gymtrackerweb.servlet;

import com.example.gymtrackerweb.dao.ClienteDAO;
import com.example.gymtrackerweb.model.Cliente;
import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig; // <-- NECESARIO AHORA
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@WebServlet(name = "ClientesServlet", value = "/api/clientes/crear")

public class ClientesServlet extends HttpServlet {

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final Gson gson = new Gson();
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("admin") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\":\"Sesión no válida\"}");
            return;
        }

        request.setCharacterEncoding("UTF-8");

        response.setContentType("application/json;charset=UTF-8");

        Map<String, Object> jsonResponse = new HashMap<>();
        Map<String, String> errors = new HashMap<>();

        String ci        = request.getParameter("ci");
        String email     = request.getParameter("email");
        String nombre    = request.getParameter("nombre");
        String apellido  = request.getParameter("apellido");
        String ciudad    = request.getParameter("ciudad");
        String pais      = request.getParameter("pais");
        String direccion = request.getParameter("direccion");
        String telefono  = request.getParameter("telefono");
        String fechaStr  = request.getParameter("fecha_ingreso");

        System.out.println("Valores leídos (x-www-form-urlencoded):");
        System.out.printf("ci=[%s], email=[%s], nombre=[%s], apellido=[%s], ciudad=[%s], pais=[%s], direccion=[%s], telefono=[%s], fecha_ingreso=[%s]%n",
                ci, email, nombre, apellido, ciudad, pais, direccion, telefono, fechaStr);

        // Validaciones
        if (ci == null || ci.trim().isEmpty()) errors.put("cliente-ci", "La cédula es obligatoria.");

        if (email == null || email.trim().isEmpty()) {
            errors.put("cliente-email", "El email es obligatorio.");
        } else if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            errors.put("cliente-email", "El formato del email no es válido.");
        }

        if (nombre == null || nombre.trim().isEmpty())   errors.put("cliente-nombre", "El nombre es obligatorio.");
        if (apellido == null || apellido.trim().isEmpty()) errors.put("cliente-apellido", "El apellido es obligatorio.");
        if (ciudad == null || ciudad.trim().isEmpty())   errors.put("cliente-ciudad", "La ciudad es obligatoria.");
        if (pais == null || pais.trim().isEmpty())       errors.put("cliente-pais", "El país es obligatorio.");
        if (direccion == null || direccion.trim().isEmpty()) errors.put("cliente-direccion", "La dirección es obligatoria.");
        if (telefono == null || telefono.trim().isEmpty())   errors.put("cliente-telefono", "El teléfono es obligatorio.");

        java.sql.Date fechaIngreso = null;
        if (fechaStr == null || fechaStr.isEmpty()) {
            errors.put("cliente-fecha-ingreso", "La fecha de ingreso es obligatoria.");
        } else if (!fechaStr.matches("\\d{4}-\\d{2}-\\d{2}")) {
            errors.put("cliente-fecha-ingreso", "Formato de fecha inválido (yyyy-MM-dd).");
        } else {
            try {
                fechaIngreso = java.sql.Date.valueOf(fechaStr);
            } catch (IllegalArgumentException e) {
                errors.put("cliente-fecha-ingreso", "La fecha ingresada no es válida.");
            }
        }

        if (!errors.isEmpty()) {
            System.out.println("Errores de validación: " + errors);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Por favor corrige los errores indicados.");
            jsonResponse.put("errors", errors);
            response.getWriter().write(gson.toJson(jsonResponse));
            return;
        }

        Cliente c = new Cliente();
        c.setCi(ci.trim());
        c.setEmail(email.trim());
        c.setNombre(nombre.trim());
        c.setApellido(apellido.trim());
        c.setCiudad(ciudad.trim());
        c.setDireccion(direccion.trim());
        c.setTel(telefono.trim());
        c.setPais(pais.trim());
        c.setFechaIngreso(fechaIngreso);

        try {
            clienteDAO.agregarCliente(c);
            response.setStatus(HttpServletResponse.SC_CREATED);
            jsonResponse.put("success", true);
            jsonResponse.put("message", "Cliente '" + nombre.trim() + " " + apellido.trim() + "' creado exitosamente.");
            response.getWriter().write(gson.toJson(jsonResponse));
        } catch (SQLException e) {
            e.printStackTrace();
            if (e instanceof SQLIntegrityConstraintViolationException) {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                jsonResponse.put("success", false);
                String errorMsg = e.getMessage().toLowerCase();
                if (errorMsg.contains("ci") || errorMsg.contains("primary")) {
                    jsonResponse.put("message", "La cédula ingresada ya existe.");
                    errors.put("cliente-ci", "Esta cédula ya está registrada.");
                } else if (errorMsg.contains("email")) {
                    jsonResponse.put("message", "El email ingresado ya existe.");
                    errors.put("cliente-email", "Este email ya está registrado.");
                } else {
                    jsonResponse.put("message", "Error de integridad: " + e.getMessage());
                }
                jsonResponse.put("errors", errors);
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Error SQL: " + e.getMessage());
            }
            response.getWriter().write(gson.toJson(jsonResponse));
        }
    }
}
