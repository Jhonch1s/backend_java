package com.example.gymtrackerweb.servlet;

import com.example.gymtrackerweb.dao.RegistroGymDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.*;

//al estar en /stats/overview no se choca con /cleinte/estadisticas
@WebServlet("/cliente/stats/overview")
public class StatsOverviewServlet extends HttpServlet {

    private static final ZoneId MONTEVIDEO = ZoneId.of("America/Montevideo");
    private final RegistroGymDAO registroDao = new RegistroGymDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        var session = req.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            // redirigir al login
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        var usuario = (com.example.gymtrackerweb.model.Cliente) session.getAttribute("usuario");
        String ci = usuario.getCi();

        String ymParam = req.getParameter("ym");
        YearMonth ym;
        if (ymParam == null || ymParam.isBlank()) {
            LocalDate hoyMvd = LocalDate.now(MONTEVIDEO);
            ym = YearMonth.of(hoyMvd.getYear(), hoyMvd.getMonth());
        } else {
            ym = YearMonth.parse(ymParam); // "YYYY-MM"
        }
        LocalDate anyDay = ym.atDay(1);

        try {
            int dias    = registroDao.contarDiasEntrenadosMes(ci, anyDay);
            int minProm = registroDao.minutosPromedioPorSesionMes(ci, anyDay);
            int minTot  = registroDao.minutosTotalesMes(ci, anyDay);

            resp.setContentType("application/json;charset=UTF-8");
            try (PrintWriter out = resp.getWriter()) {
                out.printf("""
          {"ok":true,"ym":"%s","dias":%d,"minTotales":%d,"minPromedio":%d}
        """, ym, dias, minTot, minProm);
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().printf("{\"ok\":false,\"error\":\"%s\"}", e.getMessage());
        }
    }
}
