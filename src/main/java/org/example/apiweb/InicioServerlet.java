package org.example.apiweb;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet(name = "InicioServlet", urlPatterns = {"/"})
public class InicioServerlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String ctx = req.getContextPath();         // p.ej. /ApiWeb
        String uri = req.getRequestURI();          // p.ej. /ApiWeb/assets/css/style.css

        // Dejar pasar recursos estáticos al DefaultServlet
        if (uri.startsWith(ctx + "/assets/")
                || uri.startsWith(ctx + "/js/")
                || uri.startsWith(ctx + "/img/")
                || uri.equals(ctx + "/manifest.json")
                || uri.startsWith(ctx + "/favicon")) {
            req.getServletContext().getNamedDispatcher("default").forward(req, resp);
            return;
        }

        // Solo la raíz: mostrar index.jsp
        if (uri.equals(ctx) || uri.equals(ctx + "/")) {
            req.getRequestDispatcher("/index.jsp").forward(req, resp);
            return;
        }

        // (opcional) 404 para otras rutas
        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }
}
