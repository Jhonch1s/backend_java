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
@MultipartConfig // <-- ¡AÑADIR ESTA ANOTACIÓN!
public class ClientesServlet extends HttpServlet {

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final Gson gson = new Gson();
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\":\"Sesión no válida\"}");
            return;
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Map<String, Object> jsonResponse = new HashMap<>();
        Map<String, String> errors = new HashMap<>();

        String ci = null;
        String email = null;
        String nombre = null;
        String apellido = null;
        String ciudad = null;
        String pais = null;
        String direccion = null;
        String telefono = null;
        String fechaStr = null;
        Date fechaIngreso = null;

        try {
            ci = getStringValueFromPart(request.getPart("ci"));
            email = getStringValueFromPart(request.getPart("email"));
            nombre = getStringValueFromPart(request.getPart("nombre"));
            apellido = getStringValueFromPart(request.getPart("apellido"));
            ciudad = getStringValueFromPart(request.getPart("ciudad"));
            pais = getStringValueFromPart(request.getPart("pais"));
            direccion = getStringValueFromPart(request.getPart("direccion"));
            telefono = getStringValueFromPart(request.getPart("telefono"));
            fechaStr = getStringValueFromPart(request.getPart("fecha_ingreso"));

            System.out.println("Valores leídos de Parts:");
            System.out.println("  ci: [" + ci + "]");
            System.out.println("  email: [" + email + "]");
            System.out.println("  nombre: [" + nombre + "]");
            System.out.println("  apellido: [" + apellido + "]");
            System.out.println("  ciudad: [" + ciudad + "]");
            System.out.println("  pais: [" + pais + "]");
            System.out.println("  direccion: [" + direccion + "]");
            System.out.println("  telefono: [" + telefono + "]");
            System.out.println("  fecha_ingreso: [" + fechaStr + "]");

            if (ci == null || ci.trim().isEmpty()) errors.put("cliente-ci", "La cédula es obligatoria.");
            if (email == null || email.trim().isEmpty()) {
                errors.put("cliente-email", "El email es obligatorio.");
            } else if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
                errors.put("cliente-email", "El formato del email no es válido.");
            }
            if (nombre == null || nombre.trim().isEmpty()) errors.put("cliente-nombre", "El nombre es obligatorio.");
            if (apellido == null || apellido.trim().isEmpty()) errors.put("cliente-apellido", "El apellido es obligatorio.");
            if (ciudad == null || ciudad.trim().isEmpty()) errors.put("cliente-ciudad", "La ciudad es obligatoria.");
            if (pais == null || pais.trim().isEmpty()) errors.put("cliente-pais", "El país es obligatorio.");
            if (direccion == null || direccion.trim().isEmpty()) errors.put("cliente-direccion", "La dirección es obligatoria.");
            if (telefono == null || telefono.trim().isEmpty()) errors.put("cliente-telefono", "El teléfono es obligatorio.");


            if (fechaStr == null || fechaStr.isEmpty()) {
                errors.put("cliente-fecha-ingreso", "La fecha de ingreso es obligatoria.");
            } else {
                if (!fechaStr.matches("\\d{4}-\\d{2}-\\d{2}")) {
                    errors.put("cliente-fecha-ingreso", "Formato de fecha inválido (yyyy-MM-dd).");
                } else {
                    try { fechaIngreso = Date.valueOf(fechaStr); } catch (IllegalArgumentException e) { errors.put("cliente-fecha-ingreso", "La fecha ingresada no es válida."); }
                }
            }

            // 3. Si hay errores, devolverlos
            if (!errors.isEmpty()) {
                System.out.println("Errores de validación encontrados: " + errors);
                System.out.println("==== FIN doPost (Error Validacion) ====");
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
                System.out.println("Cliente guardado exitosamente.");
                System.out.println("==== FIN doPost (Éxito) ====");
                response.getWriter().write(gson.toJson(jsonResponse));

            } catch (SQLException e) {
                System.err.println("Error SQL al guardar cliente:"); e.printStackTrace();
                if (e instanceof SQLIntegrityConstraintViolationException) {
                    response.setStatus(HttpServletResponse.SC_CONFLICT); jsonResponse.put("success", false);
                    String errorMsg = e.getMessage().toLowerCase();
                    if (errorMsg.contains("ci") || errorMsg.contains("primary")) { jsonResponse.put("message", "La cédula ingresada ya existe."); errors.put("cliente-ci", "Esta cédula ya está registrada.");
                    } else if (errorMsg.contains("email")) { jsonResponse.put("message", "El email ingresado ya existe."); errors.put("cliente-email", "Este email ya está registrado.");
                    } else { jsonResponse.put("message", "Error de integridad: " + e.getMessage()); }
                    jsonResponse.put("errors", errors);
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); jsonResponse.put("success", false); jsonResponse.put("message", "Error SQL: " + e.getMessage());
                }
                System.out.println("==== FIN doPost (Error SQL) ====");
                response.getWriter().write(gson.toJson(jsonResponse));
            }

        } catch (Exception e) { // Captura errores al leer las Parts
            System.err.println("Error inesperado procesando la petición:"); e.printStackTrace();
            System.out.println("==== FIN doPost (Error General) ====");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Error procesando la solicitud: " + e.getMessage());
            response.getWriter().write(gson.toJson(jsonResponse));
        }
    }

    private String getStringValueFromPart(Part part) throws IOException {
        if (part == null) {
            return null;
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(part.getInputStream(), StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }
}