package com.example.gymtrackerweb.servlet;

import com.example.gymtrackerweb.dao.RegistroGymDAO;
import com.example.gymtrackerweb.model.Cliente;
import com.example.gymtrackerweb.model.RegistroGym;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@WebServlet(name = "ClienteCheckinServlet", urlPatterns = { "/cliente/checkin" })
public class ClienteCheckinServlet extends HttpServlet {

    private static final ZoneId ZONE = ZoneId.of("America/Montevideo");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");

        Cliente usuario = getCliente(req.getSession(false));
        if (usuario == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            writeJson(resp, "{\"ok\":false,\"error\":\"no_auth\"}");
            return;
        }

        String ci = usuario.getCi();
        RegistroGymDAO dao = new RegistroGymDAO();
        try {
            boolean abierta = dao.tieneSesionAbierta(ci);
            String label = abierta ? "Marcar salida" : "Marcar entrada";
            String state = abierta ? "open" : "closed";
            writeJson(resp, "{\"ok\":true,\"state\":\"" + state + "\",\"label\":\"" + label + "\"}");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writeJson(resp, "{\"ok\":false,\"error\":\"exception\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");

        Cliente usuario = getCliente(req.getSession(false));
        if (usuario == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            writeJson(resp, "{\"ok\":false,\"error\":\"no_auth\"}");
            return;
        }

        String ci = usuario.getCi();
        LocalDateTime ahoraUy = LocalDateTime.now(ZONE);
        RegistroGymDAO dao = new RegistroGymDAO();

        try {
            Optional<RegistroGym> abierta = dao.findSesionAbierta(ci);
            if (abierta.isPresent()) {
                // Cerrar sesión: setear salida = ahora
                boolean ok = dao.updateSalida(abierta.get().getIdRegistro(), ahoraUy);
                if (ok) {
                    writeJson(resp, "{\"ok\":true,\"action\":\"salida\",\"state\":\"closed\",\"label\":\"Marcar entrada\"}");
                } else {
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    writeJson(resp, "{\"ok\":false,\"error\":\"update_failed\"}");
                }
            } else {
                // Abrir sesión: insertar entrada = ahora (salida NULL)
                RegistroGym nuevo = RegistroGym.nuevo(ci, ahoraUy);
                int id = dao.insert(nuevo);
                if (id > 0) {
                    writeJson(resp, "{\"ok\":true,\"action\":\"entrada\",\"state\":\"open\",\"label\":\"Marcar salida\"}");
                } else {
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    writeJson(resp, "{\"ok\":false,\"error\":\"insert_failed\"}");
                }
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writeJson(resp, "{\"ok\":false,\"error\":\"exception\"}");
        }
    }

    // helpers
    private Cliente getCliente (HttpSession session) {
        if (session == null) return null;
        Object u = session.getAttribute("usuario");
        if (u == null) return null;
        return (Cliente) u;
    }

    private void writeJson(HttpServletResponse resp, String json) throws IOException {
        try (PrintWriter out = resp.getWriter()) {
            out.print(json);
        }
    }
}
