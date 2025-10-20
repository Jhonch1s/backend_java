package com.example.gymtrackerweb.servlet;

import com.example.gymtrackerweb.dao.PlanDAO;
import com.example.gymtrackerweb.model.Plan;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Servlet para obtener los detalles de un plan específico por su ID.
 * Responde a GET /api/planes/detalle?id=X
 */
@WebServlet("/api/planes/detalle")
public class DetallePlanServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            int id = Integer.parseInt(request.getParameter("id"));
            PlanDAO planDAO = new PlanDAO();
            Plan plan = planDAO.buscarPorId(id);

            if (plan != null) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(new Gson().toJson(plan));
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\":\"Plan no encontrado\"}");
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"ID de plan inválido\"}");
        }
    }
}