package com.example.gymtrackerweb.servlet;

import com.example.gymtrackerweb.dao.ProgresoEjercicioDAO;
import com.example.gymtrackerweb.model.Cliente;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/cliente/stats/exercise/mini")
public class EstadisticasEjercicioMiniServlet extends HttpServlet {
    private ProgresoEjercicioDAO dao;
    @Override public void init() { dao = new ProgresoEjercicioDAO(); }

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        Cliente usuario = (Cliente) req.getSession().getAttribute("usuario");
        if (usuario == null) { resp.setStatus(401); resp.getWriter().write("{\"ok\":false,\"error\":\"no-auth\"}"); return; }

        String ejParam = req.getParameter("ej");
        if (ejParam == null) { resp.setStatus(400); resp.getWriter().write("{\"ok\":false,\"error\":\"bad-req\"}"); return; }
        //para depurar
        System.out.println("[mini] ci=" + usuario.getCi() + " ej=" + req.getParameter("ej"));
        try {
            int idEj = Integer.parseInt(ejParam);
            String ci = usuario.getCi();
            var kpi = dao.miniKpis(ci, idEj);
            String json = """
              {"ok":true,
                "bestE1rm":%s,
                "bestSet":{"kg":%s,"reps":%s},
                "vol4w":%s,
                "deltaE1rm":%s}
            """.formatted(
                    fmt(kpi.getBestE1rm()),
                    fmt(kpi.getBestSetKg()), kpi.getBestSetReps(),
                    fmt(kpi.getVol4w()),
                    kpi.getDeltaE1rm()==null? "null" : fmt(kpi.getDeltaE1rm())
            );
            resp.getWriter().write(json);
        } catch (Exception e) {
            //para depurar
            e.printStackTrace();
            resp.setStatus(500);
            resp.getWriter().write("{\"ok\":false,\"error\":\"server\"}");
        }
    }

    private static String fmt(double d){ return String.format(java.util.Locale.US,"%.2f", d); }
}
