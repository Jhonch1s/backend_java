package com.example.gymtrackerweb.servlet;

// CompareStatsServlet.java
import com.example.gymtrackerweb.dao.ClienteDAO;
import com.example.gymtrackerweb.dao.ProgresoEjercicioDAO;
import com.example.gymtrackerweb.dao.ShareLinkDAO;
import com.example.gymtrackerweb.dao.ShareLinkDAO.ShareLink;
import com.example.gymtrackerweb.model.Cliente;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

@WebServlet("/cliente/stats/compare")
public class CompareStatsServlet extends HttpServlet {

    private final ProgresoEjercicioDAO progresoDao = new ProgresoEjercicioDAO();
    private final ShareLinkDAO shareDao = new ShareLinkDAO();
    private final ClienteDAO clienteDao = new ClienteDAO(); // ← Asegúrate de tener este DAO

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        try {
            // 1) Auth del viewer
            Cliente viewer = (Cliente) req.getSession().getAttribute("usuario");
            if (viewer == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }

            // 2) Params
            final String token = req.getParameter("t");
            final String r     = req.getParameter("r");
            final String ej    = req.getParameter("ej");
            if (token == null || r == null || ej == null) {
                resp.sendError(400, "missing params"); return;
            }

            final int ejId;
            try { ejId = Integer.parseInt(ej); } catch (NumberFormatException nfe) { resp.sendError(400, "bad ej"); return; }
            if (!java.util.Set.of("4w","3m","6m","12m","all").contains(r)) {
                resp.sendError(400, "bad range"); return;
            }

            // 3) Resolver dueño (desde token)
            ShareLink link = shareDao.findByToken(token);
            if (link == null || link.isRevoked) { resp.sendError(400, "invalid token"); return; }
            if (link.expiresAt != null && java.time.LocalDateTime.now().isAfter(link.expiresAt)) {
                resp.sendError(400, "expired token"); return;
            }
            final String ownerCi = link.ownerCi;

            // 4) Fechas
            LocalDate today = LocalDate.now();
            LocalDate from, to = today;
            switch (r) {
                case "4w":  from = today.minusWeeks(4);  break;
                case "3m":  from = today.minusMonths(3); break;
                case "6m":  from = today.minusMonths(6); break;
                case "12m": from = today.minusMonths(12);break;
                default:    from = LocalDate.of(1900,1,1);
            }

            // 5) Nombres
            final String ownerName  = Optional.ofNullable(clienteDao.findDisplayNameByCi(ownerCi)).orElse("Usuario compartido");
            final String viewerName = Optional.ofNullable(viewer.getNombre()).orElse("Tú");

            // 6) Cargar series una sola vez
            final var serieOwner  = progresoDao.seriesForEjercicio(ownerCi, ejId, from, to);
            final var serieViewer = progresoDao.seriesForEjercicio(viewer.getCi(), ejId, from, to);

            System.out.printf("[COMPARE] ownerCi=%s viewerCi=%s ejId=%d range=%s from=%s to=%s sizes owner=%d viewer=%d%n",
                    ownerCi, viewer.getCi(), ejId, r, from, to,
                    (serieOwner!=null?serieOwner.size():-1), (serieViewer!=null?serieViewer.size():-1));

            // 7) Si piden JSON, devolver ambas series en JSON
            String accept = req.getHeader("Accept");
            boolean wantsJson = accept != null && accept.toLowerCase().contains("application/json");
            if (wantsJson) {
                resp.setContentType("application/json;charset=UTF-8");
                StringBuilder sb = new StringBuilder(1024);
                sb.append("{\"ok\":true,");
                sb.append("\"meta\":{\"ownerName\":").append(jsonStr(ownerName))
                        .append(",\"viewerName\":").append(jsonStr(viewerName)).append("},");
                sb.append("\"owner\":[");
                for (int i = 0; i < (serieOwner!=null?serieOwner.size():0); i++) {
                    var d = serieOwner.get(i);
                    sb.append("{\"fecha\":\"").append(d.getFecha()).append("\",")
                            .append("\"kg\":").append(d.getPeso()==null?"null":d.getPeso().toPlainString()).append(",")
                            .append("\"reps\":").append(d.getRepeticiones()).append("}");
                    if (i < serieOwner.size() - 1) sb.append(",");
                }
                sb.append("],\"viewer\":[");
                for (int i = 0; i < (serieViewer!=null?serieViewer.size():0); i++) {
                    var d = serieViewer.get(i);
                    sb.append("{\"fecha\":\"").append(d.getFecha()).append("\",")
                            .append("\"kg\":").append(d.getPeso()==null?"null":d.getPeso().toPlainString()).append(",")
                            .append("\"reps\":").append(d.getRepeticiones()).append("}");
                    if (i < serieViewer.size() - 1) sb.append(",");
                }
                sb.append("]}");
                resp.getWriter().write(sb.toString());
                return;
            }

            // 8) HTML (JSP): setear atributos que usa compararProgreso.jsp
            req.setAttribute("ejId", ejId);
            req.setAttribute("range", r);
            req.setAttribute("from", from);
            req.setAttribute("to", to);
            req.setAttribute("ownerDisplay", ownerName);
            req.setAttribute("viewerDisplay", viewerName);
            req.setAttribute("ownerSeries", serieOwner);
            req.setAttribute("viewerSeries", serieViewer);

            req.getRequestDispatcher("/pages/cliente/compararProgreso.jsp").forward(req, resp);

        } catch (Exception ex) {
            ex.printStackTrace(); // LOG al Tomcat
            resp.sendError(500);
        }
    }

    // Helper para escapar String en JSON
    private String jsonStr(String s) {
        if (s == null) return "null";
        return "\"" + s.replace("\\","\\\\").replace("\"","\\\"") + "\"";
    }
}
