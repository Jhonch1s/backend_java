package com.example.gymtrackerweb.servlet.staff;

import com.example.gymtrackerweb.dao.RutinaDAO;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

// Esta es la URL a la que el modal hace POST
@WebServlet("/admin/guardar-asignacion-rutina")
public class AsignarRutinaClienteServlet extends HttpServlet {

    private final Gson gson = new Gson();
    private final RutinaDAO rutinaDAO = new RutinaDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            String clienteId = request.getParameter("clienteId");
            int rutinaId = Integer.parseInt(request.getParameter("rutinaId"));
            LocalDate fechaAsignacion = LocalDate.parse(request.getParameter("fechaAsignacion"));

            rutinaDAO.asignarNuevaRutinaCliente(clienteId, rutinaId, fechaAsignacion);

            response.getWriter().write(gson.toJson(Map.of("success", true)));

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(Map.of("success", false, "error", e.getMessage())));
        }
    }
}
