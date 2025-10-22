package com.example.gymtrackerweb.servlet;

import com.example.gymtrackerweb.dao.ProgresoEjercicioDAO;
import com.example.gymtrackerweb.model.Cliente;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/cliente/stats/exercises")
public class EstadisticasEjerciciosServlet extends HttpServlet {
    private ProgresoEjercicioDAO dao;

    @Override public void init() { dao = new ProgresoEjercicioDAO(); }

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, IOException {
        resp.setContentType("application/json;charset=UTF-8");

        var usuario = (Cliente) req.getSession().getAttribute("usuario");
        if (usuario == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("{\"ok\":false,\"error\":\"no-auth\"}");
            return;
        }

        try {
            String idCliente = usuario.getCi();
            var ejercicios = dao.listarEjerciciosPorCliente(idCliente); //matchea list<EjericiosMin>
            // JSON minimo
            var sb = new StringBuilder(64 + ejercicios.size() * 40);
            sb.append("{\"ok\":true,\"items\":[");
            for (int i=0;i<ejercicios.size();i++){
                var e = ejercicios.get(i);
                if (i>0) sb.append(',');
                sb.append("{\"id\":").append(e.getId()).append(",\"nombre\":\"")
                        .append(e.getNombre().replace("\"","\\\"")).append("\"}");
            }
            sb.append("]}");
            resp.getWriter().write(sb.toString());
        } catch (Exception ex) {
            resp.setStatus(500);
            resp.getWriter().write("{\"ok\":false,\"error\":\"server\"}");
        }
    }
}
