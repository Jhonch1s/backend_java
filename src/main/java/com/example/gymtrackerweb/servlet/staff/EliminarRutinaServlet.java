package com.example.gymtrackerweb.servlet.staff;

import com.example.gymtrackerweb.dao.RutinaDAO;
import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

// Esta URL debe coincidir con la del fetch() en tu JS
@WebServlet("/admin/eliminar-rutina")
public class EliminarRutinaServlet extends HttpServlet {

    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Map<String, Object> jsonResponse = new HashMap<>();

        String idStr = request.getParameter("id");

        try {
            int id = Integer.parseInt(idStr);
            RutinaDAO rutinaDAO = new RutinaDAO();

            // Aquí puedes agregar validaciones (ej. verificar si la rutina tiene ejercicios asociados)
            // Por ahora, la eliminamos directamente.

            boolean eliminado = rutinaDAO.eliminarRutina(id);

            if (eliminado) {
                jsonResponse.put("success", true);
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                throw new Exception("No se pudo eliminar la rutina (posiblemente no existía o tiene dependencias).");
            }

        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            jsonResponse.put("error", "ID inválido.");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.put("error", e.getMessage() != null ? e.getMessage() : "Error en el servidor al eliminar.");
        }

        response.getWriter().write(gson.toJson(jsonResponse));
    }
}
