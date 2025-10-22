package com.example.gymtrackerweb.servlet;

import com.example.gymtrackerweb.dao.PlanDAO;
import com.example.gymtrackerweb.model.Plan;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@WebServlet("/api/planes/editar")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024, // 1 MB
        maxFileSize = 1024 * 1024 * 5,    // 5 MB
        maxRequestSize = 1024 * 1024 * 10 // 10 MB
)
public class EditarPlanServlet extends HttpServlet {

    private static final String UPLOAD_DIRECTORY = "/opt/tomcat/gym-uploads/planes";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> jsonResponse = new HashMap<>();
        PlanDAO planDAO = new PlanDAO();

        try {
            // 1. Obtener ID y cargar el plan existente desde la BD
            int idPlan = Integer.parseInt(request.getParameter("plan_id"));
            Plan planAEditar = planDAO.buscarPorId(idPlan);

            if (planAEditar == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                jsonResponse.put("success", false);
                jsonResponse.put("message", "El plan que intentas editar no existe.");
                response.getWriter().write(new Gson().toJson(jsonResponse));
                return;
            }

            // 2. Actualizar los campos del objeto Plan
            planAEditar.setNombre(request.getParameter("nombre"));
            planAEditar.setValor(new BigDecimal(request.getParameter("valor")));
            planAEditar.setDuracionTotal(Short.parseShort(request.getParameter("cantidad")));
            planAEditar.setDuracionUnidadId(Byte.parseByte(request.getParameter("unidad")));
            planAEditar.setEstado("on".equalsIgnoreCase(request.getParameter("activo")));

            // 3. Lógica para manejar la actualización de la imagen
            boolean mantenerImagen = "on".equalsIgnoreCase(request.getParameter("mantener_imagen"));
            Part filePart = request.getPart("imagen");

            if (!mantenerImagen && filePart != null && filePart.getSize() > 0) {
                // El usuario quiere reemplazar la imagen

                // a) Borrar la imagen antigua si existe
                String imagenAntigua = planAEditar.getUrlImagen();
                if (imagenAntigua != null && !imagenAntigua.isEmpty()) {
                    File oldFile = new File(UPLOAD_DIRECTORY, imagenAntigua);
                    if (oldFile.exists()) {
                        oldFile.delete();
                    }
                }

                // b) Guardar la nueva imagen
                String submittedFileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                String extension = submittedFileName.substring(submittedFileName.lastIndexOf('.'));
                String nuevoNombreArchivo = UUID.randomUUID().toString() + extension;

                filePart.write(UPLOAD_DIRECTORY + File.separator + nuevoNombreArchivo);
                planAEditar.setUrlImagen(nuevoNombreArchivo); // Actualizar al nuevo nombre

            } else if (!mantenerImagen) {
                // El usuario desmarcó "mantener" pero no subió una nueva, implica eliminar la imagen
                String imagenAntigua = planAEditar.getUrlImagen();
                if (imagenAntigua != null && !imagenAntigua.isEmpty()) {
                    File oldFile = new File(UPLOAD_DIRECTORY, imagenAntigua);
                    if (oldFile.exists()) {
                        oldFile.delete();
                    }
                }
                planAEditar.setUrlImagen(null);
            }
            // Si "mantenerImagen" es true, no hacemos nada y el nombre del archivo se conserva.

            // 4. Guardar los cambios en la Base de Datos
            planDAO.modificarPlan(planAEditar);

            // 5. Enviar respuesta de éxito
            jsonResponse.put("success", true);
            jsonResponse.put("message", "Plan '" + planAEditar.getNombre() + "' actualizado exitosamente.");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(new Gson().toJson(jsonResponse));

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Ocurrió un error en el servidor al editar el plan: " + e.getMessage());
            response.getWriter().write(new Gson().toJson(jsonResponse));
            e.printStackTrace();
        }
    }
}