package com.example.gymtrackerweb.servlet;

import com.example.gymtrackerweb.dao.ProgresoEjercicioDAO;
import com.example.gymtrackerweb.model.Cliente;
import com.example.gymtrackerweb.model.ProgresoEjercicio;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/detalle-progreso")
public class DetalleProgresoServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        Cliente cliente = (Cliente) session.getAttribute("usuario");
        if (cliente == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        int idEjercicio = Integer.parseInt(request.getParameter("idEjercicio"));
        String idCliente = cliente.getCi();

        ProgresoEjercicioDAO dao = new ProgresoEjercicioDAO();
        List<ProgresoEjercicio> registros = dao.obtenerRegistrosDetallados(idEjercicio, idCliente);
        List<ProgresoEjercicio> prs = dao.obtenerPRs(idEjercicio, idCliente);

        Map<String, Object> data = new HashMap<>();
        data.put("registros", registros.stream().map(r -> Map.of(
                "fecha", new java.text.SimpleDateFormat("dd/MM/yyyy").format(r.getFecha()),
                "peso", r.getPesoUsado(),
                "reps", r.getRepeticiones()
        )).toList());

        data.put("prs", prs.stream().map(r -> Map.of(
                "fecha", new java.text.SimpleDateFormat("dd/MM/yyyy").format(r.getFecha()),
                "peso", r.getPesoUsado(),
                "reps", r.getRepeticiones()
        )).toList());

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
    }
}
