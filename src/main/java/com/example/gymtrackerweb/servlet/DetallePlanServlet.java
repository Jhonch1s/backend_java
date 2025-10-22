package com.example.gymtrackerweb.servlet;

import com.example.gymtrackerweb.dao.PlanDAO;
import com.example.gymtrackerweb.model.Plan;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

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

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\":\"Sesión no válida\"}");
            return;
        }

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