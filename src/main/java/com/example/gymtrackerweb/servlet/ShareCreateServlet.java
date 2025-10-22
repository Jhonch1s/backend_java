package com.example.gymtrackerweb.servlet;
// ShareCreateServlet.java

import com.example.gymtrackerweb.dao.ShareLinkDAO;
import com.example.gymtrackerweb.model.Cliente; // ajusta el import
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLSyntaxErrorException;
import java.time.LocalDateTime;
import java.util.Set;

@WebServlet("/cliente/stats/share/create")
public class ShareCreateServlet extends HttpServlet {

    private final ShareLinkDAO shareDao = new ShareLinkDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setContentType("application/json;charset=UTF-8");

        Cliente owner = (Cliente) req.getSession().getAttribute("usuario");
        if (owner == null) { resp.setStatus(401); resp.getWriter().write("{\"error\":\"auth\"}"); return; }

        String ejParam = req.getParameter("ejId");
        String range   = req.getParameter("range");
        if (ejParam == null || ejParam.isBlank() || range == null || range.isBlank()) {
            resp.setStatus(400); resp.getWriter().write("{\"error\":\"missing_params\"}"); return;
        }

        int ejId;
        try { ejId = Integer.parseInt(ejParam); }
        catch (NumberFormatException nfe) { resp.setStatus(400); resp.getWriter().write("{\"error\":\"bad_ejId\"}"); return; }

        if (!java.util.Set.of("4w","3m","6m","12m","all").contains(range)) {
            resp.setStatus(400); resp.getWriter().write("{\"error\":\"bad_range\"}"); return;
        }

        try {
            LocalDateTime expira = LocalDateTime.now().plusDays(30);
            // 👇 intenta varias veces por si choca la UNIQUE del token
            String token = null;
            for (int i=0; i<5; i++) {
                try {
                    token = shareDao.createToken(owner.getCi(), expira);
                    break;
                } catch (SQLIntegrityConstraintViolationException dupe) {
                    // token repetido, reintenta
                }
            }
            if (token == null) {
                resp.setStatus(500);
                resp.getWriter().write("{\"error\":\"token_generation_failed\"}");
                return;
            }

            String ctx = req.getContextPath();
            String url = String.format("%s/cliente/stats/compare?t=%s&ej=%d&r=%s", ctx, token, ejId, range);
            resp.getWriter().write("{\"url\":\"" + url + "\"}");
        } catch (SQLSyntaxErrorException ddl) {
            ddl.printStackTrace(); // 👈 log en consola/Tomcat
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"sql_syntax_or_table_missing\"}");
        } catch (SQLException sql) {
            sql.printStackTrace(); // 👈 log completo
            resp.setStatus(500);
            // Mensaje útil para depurar (no exponer en prod)
            String msg = sql.getMessage().replace("\"","'");
            resp.getWriter().write("{\"error\":\"sql\",\"detail\":\"" + msg + "\"}");
        } catch (Exception e) {
            e.printStackTrace(); // 👈 log general
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"server\",\"detail\":\"" + e.getClass().getSimpleName() + "\"}");
        }
    }


}
