package com.example.gymtrackerweb.servlet.staff; // Asegúrate que el paquete sea el correcto

import com.example.gymtrackerweb.dao.RutinaDAO; // Necesitarás importar tu RutinaDAO
import com.example.gymtrackerweb.model.Rutina; // Necesitarás importar tu DTO/Modelo Rutina
import com.example.gymtrackerweb.model.Staff; // Asumiendo que tenés un modelo Staff para la sesión de admin

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

// Mapeamos el servlet a la URL que pusimos en el sidebar
@WebServlet(name = "GestionRutinasServlet", urlPatterns= "/admin/gestion-rutinas")
public class GestionRutinasServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

//        HttpSession session = request.getSession(false);
//        if (session == null || session.getAttribute("usuarioStaff") == null) {
//            response.sendRedirect(request.getContextPath() + "/login"); // Ajusta la URL del login si es diferente
//            return;
//        }

        RutinaDAO rutinaDAO = new RutinaDAO();
        List<Rutina> listaRutinas = null;
        try {
            listaRutinas = rutinaDAO.listarRutinas();
        } catch (Exception e) {
            System.err.println("Error al obtener la lista de rutinas: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorCarga", "No se pudieron cargar las rutinas.");
        }

        request.setAttribute("listaTodasRutinas", listaRutinas);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/pages/staff/rutina/gestionRutinas.jsp");
        dispatcher.forward(request, response);
    }

    // pero lo necesitarás para los servlets de Crear y Eliminar.
}
