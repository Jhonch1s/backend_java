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

@WebServlet(name = "ProgresoServlet", value = "/progreso")
public class ProgresoServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        Cliente usuario = (Cliente) request.getSession().getAttribute("usuario");
        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            //Consigo el dao y la clase de progreso ejercicio(?) para obtener la lista de ejercicios de un cliente
            ProgresoEjercicioDAO dao = new ProgresoEjercicioDAO();
            ProgresoEjercicio progresoEjercicio= new ProgresoEjercicio();
            //??? Porque tengo que crear un ProgresoEjercicio para poder utilizar "listarProgresoEjercicioDeUsuario" en vez de simplemente pasarle la id del cliente
            progresoEjercicio.setIdCliente(Integer.parseInt(usuario.getCi()));

            //Consigo la lista y la guardo en el request
            List<ProgresoEjercicio> lista = dao.listarProgresoEjercicioDeUsuarioOrdenadoFecha(progresoEjercicio,false);
            request.setAttribute("listaProgresos", lista);

            EjercicioDAO ejercicioDAO = new EjercicioDAO();
            request.setAttribute("listaEjercicios", ejercicioDAO.listarEjercicios());

            request.getRequestDispatcher("/pages/cliente/progreso/verProgresosClientes.jsp").forward(request, response);

        } catch (Exception e) {
            throw new RuntimeException(e);
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

            // Vuelve a cargar la lista progreso
            response.sendRedirect(request.getContextPath() + "/progreso");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void destroy() {
    }
}