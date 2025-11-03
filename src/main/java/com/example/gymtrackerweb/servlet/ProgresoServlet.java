package com.example.gymtrackerweb.servlet;

import com.example.gymtrackerweb.dao.EjercicioDAO;
import com.example.gymtrackerweb.dao.ProgresoEjercicioDAO;
import com.example.gymtrackerweb.model.Cliente;
import com.example.gymtrackerweb.model.ProgresoEjercicio;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.sql.Date;
import java.util.List;

@WebServlet(name = "ProgresoServlet", value = "/cliente/progreso")
public class ProgresoServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        Cliente usuario = (Cliente) request.getSession().getAttribute("usuario");
        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        final int ProgresosPorPagina = 20;

        try {
            ProgresoEjercicioDAO dao = new ProgresoEjercicioDAO();
            ProgresoEjercicio progresoEjercicio = new ProgresoEjercicio();
            progresoEjercicio.setIdCliente(Integer.parseInt(usuario.getCi()));

            // Parametros
            String orden = request.getParameter("orden");
            boolean ascendente = "asc".equals(orden);
            String stringIdEjercicio = request.getParameter("idEjercicio");
            Integer idEjercicio = (stringIdEjercicio != null && !stringIdEjercicio.isEmpty()) ? Integer.parseInt(stringIdEjercicio) : null;
            String stringPagina = request.getParameter("page");
            int paginaActual = (stringPagina != null) ? Integer.parseInt(stringPagina) : 1;
            if (paginaActual < 1) paginaActual = 1;
            int offset = (paginaActual - 1) * ProgresosPorPagina;

            // Conseguir lista
            List<ProgresoEjercicio> listaProgresos = dao.listarProgresosFiltrados(
                    Integer.parseInt(usuario.getCi()),
                    idEjercicio,
                    ascendente,
                    ProgresosPorPagina,
                    offset
            );

            // Contar total
            int totalRegistros = dao.contarProgresosFiltrados(Integer.parseInt(usuario.getCi()), idEjercicio);
            int totalPaginas = (int) Math.ceil((double) totalRegistros / ProgresosPorPagina);
            if (totalPaginas < 1) totalPaginas = 1;
            if (paginaActual > totalPaginas) paginaActual = totalPaginas;


            request.setAttribute("listaProgresos", listaProgresos);
            request.setAttribute("ordenActual", ascendente ? "asc" : "desc");
            request.setAttribute("idEjercicioSeleccionado", idEjercicio);
            request.setAttribute("paginaActual", paginaActual);
            request.setAttribute("totalPaginas", totalPaginas);
            EjercicioDAO ejercicioDAO = new EjercicioDAO();
            request.setAttribute("listaEjercicios", ejercicioDAO.listarEjercicios());

            request.getRequestDispatcher("/pages/cliente/progreso/verProgresosClientes.jsp").forward(request, response);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Post para realizar los cambios en la bd
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Cliente usuario = (Cliente) request.getSession().getAttribute("usuario");
        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String accion = request.getParameter("accion");
        ProgresoEjercicioDAO dao = new ProgresoEjercicioDAO();

        try {
            switch (accion) {
                case "add" -> {
                    ProgresoEjercicio nuevo = new ProgresoEjercicio();
                    nuevo.setIdCliente(Integer.parseInt(usuario.getCi()));
                    nuevo.setIdEjercicio(Integer.parseInt(request.getParameter("idEjercicio")));
                    nuevo.setFecha(Date.valueOf(request.getParameter("fecha")));
                    nuevo.setPesoUsado(Integer.parseInt(request.getParameter("pesoUsado")));
                    nuevo.setRepeticiones(Integer.parseInt(request.getParameter("repeticiones")));
                    dao.agregarProgresoEjercicio(nuevo);
                }
                case "edit" -> {
                    ProgresoEjercicio mod = new ProgresoEjercicio();
                    mod.setId(Integer.parseInt(request.getParameter("id")));
                    mod.setIdCliente(Integer.parseInt(usuario.getCi()));
                    mod.setIdEjercicio(Integer.parseInt(request.getParameter("idEjercicio")));
                    mod.setFecha(Date.valueOf(request.getParameter("fecha")));
                    mod.setPesoUsado(Integer.parseInt(request.getParameter("pesoUsado")));
                    mod.setRepeticiones(Integer.parseInt(request.getParameter("repeticiones")));
                    dao.actualizarProgresoEjercicio(mod);
                }
                case "delete" -> {
                    ProgresoEjercicio del = new ProgresoEjercicio();
                    del.setId(Integer.parseInt(request.getParameter("id")));
                    dao.eliminarProgresoEjercicio(del);
                }
                default -> throw new IllegalArgumentException("Accion no reconocida: " + accion);
            }

            String referer = request.getHeader("referer");
            if (referer != null && referer.contains("/cliente/progreso")) {
                response.sendRedirect(referer);
            } else {
                response.sendRedirect(request.getContextPath() + "/cliente/progreso");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void destroy() {
    }
}