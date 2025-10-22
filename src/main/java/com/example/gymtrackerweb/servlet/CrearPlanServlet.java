package com.example.gymtrackerweb.servlet;

import com.example.gymtrackerweb.dao.PlanDAO;
import com.example.gymtrackerweb.model.Plan;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.BufferedReader; // Necesario para leer Parts
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader; // Necesario
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets; // Necesario
import java.nio.file.Paths;
import java.sql.SQLException; // Importar SQLException
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors; // Necesario

@WebServlet("/api/planes/crear")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 10)
public class CrearPlanServlet extends HttpServlet {

    private static final String UPLOAD_DIRECTORY = "/opt/tomcat/gym-uploads/planes"; // Ajusta si es necesario
    private final PlanDAO planDAO = new PlanDAO();
    private final Gson gson = new Gson();

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

        String nombre = null;
        String valorStr = null;
        String cantidadStr = null;
        String unidadIdStr = null;
        String activoStr = null;
        BigDecimal valor = null;
        short cantidad = 0;
        byte unidadId = 0;
        boolean activo = false;
        String nombreArchivoImagen = null; // Variable para el nombre del archivo

        try {
            // 1. Obtener Datos usando getPart
            nombre = getStringValueFromPart(request.getPart("nombre"));
            valorStr = getStringValueFromPart(request.getPart("valor"));
            cantidadStr = getStringValueFromPart(request.getPart("cantidad"));
            unidadIdStr = getStringValueFromPart(request.getPart("unidad"));
            activoStr = getStringValueFromPart(request.getPart("activo"));

            // 2. Validaciones (Asegúrate que estas validaciones sean las que necesitas)
            if (nombre == null || nombre.trim().isEmpty()) errors.put("plan-nombre", "El nombre es obligatorio.");
            if (valorStr == null || valorStr.trim().isEmpty()) {
                errors.put("plan-valor", "El valor es obligatorio.");
            } else {
                try {
                    valor = new BigDecimal(valorStr);
                    if (valor.compareTo(BigDecimal.ZERO) < 0) errors.put("plan-valor", "El valor no puede ser negativo.");
                    if (valor.scale() > 2) errors.put("plan-valor", "El valor puede tener máximo 2 decimales.");
                } catch (NumberFormatException e) { errors.put("plan-valor", "El valor debe ser un número válido."); }
            }
            if (cantidadStr == null || cantidadStr.trim().isEmpty()) {
                errors.put("plan-cantidad", "La cantidad es obligatoria.");
            } else {
                try {
                    cantidad = Short.parseShort(cantidadStr);
                    if (cantidad <= 0) errors.put("plan-cantidad", "La cantidad debe ser mayor que cero.");
                } catch (NumberFormatException e) { errors.put("plan-cantidad", "La cantidad debe ser un número entero."); }
            }
            if (unidadIdStr == null || unidadIdStr.isEmpty()) {
                errors.put("plan-unidad", "Debe seleccionar una unidad.");
            } else {
                try {
                    unidadId = Byte.parseByte(unidadIdStr);
                    // Opcional: Validar si la unidad existe
                    // if (!planDAO.existeUnidadDuracion(unidadId)) errors.put("plan-unidad", "Unidad no válida.");
                } catch (NumberFormatException e) { errors.put("plan-unidad", "Unidad no válida.");
                } /*catch (SQLException e) { errors.put("plan-unidad", "Error al verificar unidad.");}*/ // Descomenta si validas contra BD
            }
            activo = "on".equalsIgnoreCase(activoStr);

            // 3. Si hay errores de validación, devolverlos
            if (!errors.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Por favor corrige los errores indicados.");
                jsonResponse.put("errors", errors);
                response.getWriter().write(gson.toJson(jsonResponse));
                return;
            }

            // 4. Procesar Imagen (IGNORADO POR AHORA)
            Part filePart = request.getPart("imagen");
            if (filePart != null && filePart.getSize() > 0) {
                String submittedFileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                if (submittedFileName != null && !submittedFileName.isEmpty()) {
                    System.out.println("Imagen recibida [" + submittedFileName + "], pero no se guardará por ahora.");
                    // Aquí iría la lógica para guardar si la activaras:
                    // String extension = ...
                    // nombreArchivoImagen = UUID.randomUUID().toString() + extension;
                    // File uploadDir = new File(UPLOAD_DIRECTORY);
                    // if (!uploadDir.exists()) uploadDir.mkdirs();
                    // filePart.write(UPLOAD_DIRECTORY + File.separator + nombreArchivoImagen);
                }
            }

            // 5. Crear objeto Plan (con imagen null)
            Plan nuevoPlan = new Plan();
            nuevoPlan.setNombre(nombre.trim());
            nuevoPlan.setValor(valor);
            nuevoPlan.setDuracionTotal(cantidad);
            nuevoPlan.setDuracionUnidadId(unidadId);
            nuevoPlan.setEstado(activo);
            nuevoPlan.setUrlImagen(null); // Imagen NULL por ahora

            // 6. Guardar en la BD usando el NUEVO método del DAO
            // ============ CAMBIO AQUÍ ============
            planDAO.agregarPlanCompleto(nuevoPlan);
            // =====================================

            // 7. Enviar respuesta de éxito
            response.setStatus(HttpServletResponse.SC_CREATED);
            jsonResponse.put("success", true);
            jsonResponse.put("message", "Plan '" + nombre.trim() + "' creado exitosamente.");
            response.getWriter().write(gson.toJson(jsonResponse));

        } catch (SQLException e) { // Captura errores del DAO
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Error al guardar el plan en la base de datos: " + e.getMessage());
            response.getWriter().write(gson.toJson(jsonResponse));
            e.printStackTrace();
        } catch (Exception e) { // Captura errores de getPart, parsing, etc.
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // Podría ser 500 también
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Error procesando la solicitud: " + e.getMessage());
            response.getWriter().write(gson.toJson(jsonResponse));
            e.printStackTrace();
        }
    }

    private String getStringValueFromPart(Part part) throws IOException {
        if (part == null) return null;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(part.getInputStream(), StandardCharsets.UTF_8))) {
            String value = reader.lines().collect(Collectors.joining(System.lineSeparator()));
            return value != null ? value.trim() : null;
        }
    }
}