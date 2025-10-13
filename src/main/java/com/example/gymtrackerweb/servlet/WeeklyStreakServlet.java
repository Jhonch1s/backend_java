package com.example.gymtrackerweb.servlet;

import com.example.gymtrackerweb.dao.RegistroGymDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

@WebServlet("/cliente/stats/weekly-streak")
public class WeeklyStreakServlet extends HttpServlet {

    private static final ZoneId MONTEVIDEO = ZoneId.of("America/Montevideo");
    private final RegistroGymDAO registroDao = new RegistroGymDAO();
    private static final DateTimeFormatter ISO_WEEK_FMT = DateTimeFormatter.ofPattern("YYYY-'W'ww"); // “año-semana”

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

        int weeksDots = 11; // el num maximo a renderizar en los dots del html
        try {
            String wParam = req.getParameter("weeks");
            if (wParam != null && !wParam.isBlank()) {
                weeksDots = Math.max(1, Math.min(52, Integer.parseInt(wParam)));
            }
        } catch (NumberFormatException ignored) {}

        LocalDate hoyMvd = LocalDate.now(MONTEVIDEO);

        try{
            List<RegistroGymDAO.WeekActivity> listForDots =
                    registroDao.actividadSemanal(ci, weeksDots, hoyMvd);

            int weeksForStreak = 520; //hasta 10 años de racha, nunca se sabe jaja
            List<RegistroGymDAO.WeekActivity> listForStreak =
                    registroDao.actividadSemanal(ci, weeksForStreak, hoyMvd);

            int streak = RegistroGymDAO.calcularStreakSemanal(listForStreak, true); //ignoramos la semana actual para no romper la racha

            resp.setContentType("application/json;charset=UTF-8");
            try (PrintWriter out = resp.getWriter()) {
                StringBuilder weeksJson = new StringBuilder("[");
                for (int i = 0; i < listForDots.size(); i++) {
                    var w = listForDots.get(i);
                    String iso = w.monday().format(java.time.format.DateTimeFormatter.ofPattern("YYYY-'W'ww"));
                    weeksJson.append(String.format(
                            "{\"iso\":\"%s\",\"monday\":\"%s\",\"active\":%s}",
                            iso, w.monday(), w.active() ? "true" : "false"
                    ));
                    if (i < listForDots.size() - 1) weeksJson.append(",");
                }
                weeksJson.append("]");

                out.printf("{\"ok\":true,\"streak\":%d,\"weeks\":%s}", streak, weeksJson);
            }
        }catch (Exception e){
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().printf("{\"ok\":false,\"error\":\"%s\"}", e.getMessage());
        }
    }
}
