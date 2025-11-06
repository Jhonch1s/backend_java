package com.example.gymtrackerweb.servlet.staff;


import com.cloudinary.utils.ObjectUtils;
import com.example.gymtrackerweb.dao.PlanDAO;
import com.example.gymtrackerweb.model.Plan;
import com.example.gymtrackerweb.model.UnidadDuracion;
import com.example.gymtrackerweb.utils.CloudinaryConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@WebServlet(name = "PlanesServlet", value = "/staff/planes")
@MultipartConfig(maxFileSize = 5 * 1024 * 1024) // hasta 5MB
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
        request.setCharacterEncoding("UTF-8");
        String accion = request.getParameter("accion");
        PlanDAO dao = new PlanDAO();

        try {
            if ("add".equals(accion) || "edit".equals(accion)) {
                Plan p = new Plan();

                if ("edit".equals(accion)) {
                    p.setId(Integer.parseInt(request.getParameter("id")));
                }

                p.setNombre(request.getParameter("nombre").trim());
                p.setValor(new BigDecimal(request.getParameter("valor")));
                p.setDuracionTotal(Short.parseShort(request.getParameter("cantidad")));
                p.setDuracionUnidadId(Byte.parseByte(request.getParameter("unidad")));
                p.setEstado(request.getParameter("activo") != null);

                Part filePart = request.getPart("imagen");
                if (filePart != null && filePart.getSize() > 0) {
                    File tmp = File.createTempFile("plan_", ".img");
                    try (var in = filePart.getInputStream(); var out = new FileOutputStream(tmp)) {
                        in.transferTo(out);
                    }

                    Map resCld;
                    try {
                        var cloud = CloudinaryConfig.getInstance();
                        resCld = cloud.uploader().upload(tmp, ObjectUtils.asMap(
                                "folder", "gymtracker/planes",
                                "overwrite", true,
                                "resource_type", "image"
                        ));
                    } finally {
                        tmp.delete();
                    }

                    String secureUrl = (String) resCld.get("secure_url");
                    p.setUrlImagen(secureUrl);
                } else {
                    if ("edit".equals(accion)) {
                        Plan existente = dao.buscarPorId(p.getId());
                        p.setUrlImagen(existente != null ? existente.getUrlImagen() : null);
                    } else {
                        p.setUrlImagen(null);
                    }
                }

                if ("add".equals(accion)) {
                    dao.agregarPlanCompleto(p);
                } else {
                    dao.modificarPlan(p);
                }

                response.sendRedirect(request.getContextPath() + "/staff/planes");
                return;
            }

            if ("toggle".equals(accion)) {
                int id = Integer.parseInt(request.getParameter("id"));
                boolean toEstado = Boolean.parseBoolean(request.getParameter("toEstado"));
                dao.actualizarEstado(id, toEstado);
                response.sendRedirect(request.getContextPath() + "/staff/planes");
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500, "Error al procesar plan: " + e.getMessage());
        }
    }
}