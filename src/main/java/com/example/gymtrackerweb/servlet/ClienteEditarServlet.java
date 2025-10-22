package com.example.gymtrackerweb.servlet;

import com.example.gymtrackerweb.dao.ClienteDAO;
import com.example.gymtrackerweb.model.Cliente;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.sql.Date;

@WebServlet(name = "ClienteEditarServlet", urlPatterns = {"/cliente/editar"})
public class ClienteEditarServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setContentType("application/json; charset=UTF-8");

        // debe haber usuario en sesión
        Cliente usuarioSesion = (Cliente) req.getSession().getAttribute("usuario");
        if (usuarioSesion == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            try (PrintWriter out = resp.getWriter()) {
                out.write("{\"ok\":false,\"msg\":\"Sesión expirada. Inicia sesión nuevamente.\"}");
            }
            return;
        }

        // SIEMPRE la CI de la sesión (no confiamos en el form)
        final String ci = usuarioSesion.getCi();

        // tomamos campos editables del form y los pasamos por trim nullsafe
        String nombre = safe(req.getParameter("nombre"));
        String apellido = safe(req.getParameter("apellido"));
        String email = safe(req.getParameter("email"));
        String tel = safe(req.getParameter("tel"));
        String direccion = safe(req.getParameter("direccion"));
        String ciudad = safe(req.getParameter("ciudad"));
        String pais = safe(req.getParameter("pais"));

        // Validaciones min.
        if (isBlank(nombre) || isBlank(apellido) || isBlank(email)) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            try (PrintWriter out = resp.getWriter()) {
                out.write("{\"ok\":false,\"msg\":\"Nombre, apellido y email son obligatorios.\"}");
            }
            return;
        }

        // armamos el Cliente a actualizar (en DAO fechaIngresoes obligatorio)
        Cliente c = new Cliente();
        c.setCi(ci);
        c.setNombre(nombre);
        c.setApellido(apellido);
        c.setEmail(email);
        c.setTel(emptyToNull(tel));
        c.setDireccion(emptyToNull(direccion));
        c.setCiudad(emptyToNull(ciudad));
        c.setPais(emptyToNull(pais));

        // Mantenemos la fecha de ingreso existente y ademas no es editable
        Date fechaIngreso = usuarioSesion.getFechaIngreso();
        c.setFechaIngreso(fechaIngreso);

        try (PrintWriter out = resp.getWriter()) {

            ClienteDAO dao = new ClienteDAO();
            dao.modificarCliente(c);

            // Refrescamos sesión con los nuevos datos (para que el perfil/hero se actualicen)
            Cliente actualizado = dao.buscarPorCi(ci);
            req.getSession().setAttribute("usuario", actualizado);


            // Respondemos JSON de éxito
            out.write("{\"ok\":true,\"msg\":\"Perfil actualizado correctamente.\"}");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try (PrintWriter out = resp.getWriter()) {
                String msg = e.getMessage() != null ? e.getMessage().replace("\"","\\\"") : "Error inesperado";
                out.write("{\"ok\":false,\"msg\":\"Error al actualizar: " + msg + "\"}");
            }
        }
    }

    //Helpers
    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }
    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
    private static String emptyToNull(String s) {
        return isBlank(s) ? null : s.trim();
    }
}
