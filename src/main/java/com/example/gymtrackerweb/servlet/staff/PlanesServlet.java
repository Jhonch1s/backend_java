package com.example.gymtrackerweb.servlet.staff;


import com.example.gymtrackerweb.dao.PlanDAO;
import com.example.gymtrackerweb.model.Plan;
import com.example.gymtrackerweb.model.UnidadDuracion;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet(name = "PlanesServlet", value = "/staff/planes")
public class PlanesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        var session = request.getSession(false);
        if (session == null || session.getAttribute("admin") == null) {
            // redirigir al login
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            PlanDAO dao = new PlanDAO();
            List<Plan> lista = dao.listarTodos();
            List<UnidadDuracion> unidades = dao.listarUnidadesDuracion();

            request.setAttribute("listaPlanes", lista);
            request.setAttribute("unidadesDuracion", unidades);

            try {
                request.getRequestDispatcher("/pages/staff/planes.jsp").forward(request, response);
            } catch (ServletException e) {
                throw new RuntimeException(e);
            }

        } catch (Exception ex) {
            response.sendError(500, "Error al cargar los planes" + ex.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String accion = request.getParameter("accion");
        PlanDAO dao = new PlanDAO();

        try {
            if ("add".equals(accion)) {
                Plan p = new Plan();
                p.setNombre(request.getParameter("nombre").trim());
                p.setValor(new BigDecimal(request.getParameter("valor")));
                p.setDuracionTotal(Short.parseShort(request.getParameter("cantidad")));
                p.setDuracionUnidadId(Byte.parseByte(request.getParameter("unidad")));
                p.setUrlImagen(null);
                p.setEstado(request.getParameter("activo") != null);
                dao.agregarPlanCompleto(p);

            } else if ("edit".equals(accion)) {
                Plan p = new Plan();
                p.setId(Integer.parseInt(request.getParameter("id")));
                p.setNombre(request.getParameter("nombre").trim());
                p.setValor(new BigDecimal(request.getParameter("valor")));
                p.setDuracionTotal(Short.parseShort(request.getParameter("cantidad")));
                p.setDuracionUnidadId(Byte.parseByte(request.getParameter("unidad")));
                p.setUrlImagen(null);
                p.setEstado(request.getParameter("activo") != null);
                dao.modificarPlan(p);

            } else if ("toggle".equals(accion)) {
                int id = Integer.parseInt(request.getParameter("id"));
                boolean toEstado = Boolean.parseBoolean(request.getParameter("toEstado"));
                dao.actualizarEstado(id, toEstado);

            } else {
                throw new IllegalArgumentException("Accion no reconocida: " + accion);
            }


            String referer = request.getHeader("referer");
            if (referer != null && referer.contains("/staff/planes")) {
                response.sendRedirect(referer);
            } else {
                response.sendRedirect(request.getContextPath() + "/staff/planes");
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}