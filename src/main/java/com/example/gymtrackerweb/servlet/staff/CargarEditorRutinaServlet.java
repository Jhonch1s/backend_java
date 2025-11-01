package com.example.gymtrackerweb.servlet.staff;

import com.example.gymtrackerweb.dao.EjercicioDAO; // Deberás crear este DAO
import com.example.gymtrackerweb.dao.GrupoMuscularDAO; // Deberás crear este DAO
import com.example.gymtrackerweb.dao.RutinaDAO;
import com.example.gymtrackerweb.model.GrupoMuscular;
import com.example.gymtrackerweb.model.Rutina;
import com.example.gymtrackerweb.dto.EjercicioAsignadoDTO; // Lo creamos más abajo
import com.example.gymtrackerweb.dto.EjercicioDTO; // Lo creamos más abajo
import com.google.gson.Gson;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;


@WebServlet("/admin/editar-rutina")
public class CargarEditorRutinaServlet extends HttpServlet {

    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int rutinaId = Integer.parseInt(request.getParameter("id"));

            RutinaDAO rutinaDAO = new RutinaDAO();
            EjercicioDAO ejercicioDAO = new EjercicioDAO();
            GrupoMuscularDAO grupoMuscularDAO = new GrupoMuscularDAO();

            Rutina rutina = rutinaDAO.buscarPorId(rutinaId);
            if (rutina == null) {
                response.sendRedirect(request.getContextPath() + "/admin/gestion-rutinas?error=Rutina no encontrada");
                return;
            }

            List<GrupoMuscular> listaGrupos = grupoMuscularDAO.listarTodos();

            List<EjercicioDTO> listaTodosEjercicios = ejercicioDAO.listarTodosDTO();

            List<EjercicioAsignadoDTO> listaEjerciciosAsignados = rutinaDAO.listarEjerciciosAsignados(rutinaId);

            request.setAttribute("rutina", rutina);
            request.setAttribute("listaGruposMusculares", listaGrupos);

            request.setAttribute("jsonDataTodosEjercicios", gson.toJson(listaTodosEjercicios));
            request.setAttribute("jsonDataEjerciciosAsignados", gson.toJson(listaEjerciciosAsignados));

            RequestDispatcher dispatcher = request.getRequestDispatcher("/pages/staff/rutina/editarRutina.jsp");
            dispatcher.forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/gestion-rutinas?error=ID inválido");
        } catch (Exception e) {
            e.printStackTrace(); // Log del error
            response.sendRedirect(request.getContextPath() + "/admin/gestion-rutinas?error=Error al cargar la rutina");
        }
    }
}