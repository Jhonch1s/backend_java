package com.example.gymtrackerweb.servlet.staff;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.RequestDispatcher;

import java.io.IOException;


@WebServlet(name = "ClienteModificarPageServlet", urlPatterns = {"/staff/clientes/modificar"})
public class ClienteModificarPageServlet extends HttpServlet {

    // 👉 Ajustá esta ruta al lugar real de tu JSP
    private static final String JSP_PATH = "/pages/staff/cliente/modificarCliente.jsp";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String ciParam = req.getParameter("ci");
        String ci = normalizeCi(ciParam);

        // Si llega una CI válida, la pasamos al JSP como atributo
        if (ci != null && !ci.isBlank()) {
            req.setAttribute("prefillCi", ci);
        }

        // Siempre derivamos al JSP, haya o no CI
        RequestDispatcher rd = req.getRequestDispatcher(JSP_PATH);
        rd.forward(req, resp);
    }

    private String normalizeCi(String ci) {
        if (ci == null) return null;
        return ci.replaceAll("[.\\-\\s]", "").trim();
    }
}

