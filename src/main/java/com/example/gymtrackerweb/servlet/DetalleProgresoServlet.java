package com.example.gymtrackerweb.servlet;

import com.example.gymtrackerweb.dto.ProgresoDetalleView;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.example.gymtrackerweb.dao.ProgresoEjercicioDAO;
import com.example.gymtrackerweb.model.ProgresoEjercicio;
import com.example.gymtrackerweb.model.Cliente;
import com.google.gson.GsonBuilder;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;

@WebServlet("/detalle-progreso")
public class DetalleProgresoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\":\"Sesión no válida\"}");
            return;
        }

        Cliente cliente = (Cliente) session.getAttribute("usuario");
        String idCliente = cliente.getCi();

        String idEjercicioStr = request.getParameter("id");
        if (idEjercicioStr == null || idEjercicioStr.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\":\"ID de ejercicio faltante\"}");
            return;
        }

        int idEjercicio;
        try {
            idEjercicio = Integer.parseInt(idEjercicioStr);
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\":\"ID de ejercicio inválido\"}");
            return;
        }

        ProgresoEjercicioDAO dao = new ProgresoEjercicioDAO();
        List<ProgresoEjercicio> registros = dao.obtenerRegistrosDetallados(idEjercicio, idCliente);
        if (registros == null) registros = new ArrayList<>();

        List<ProgresoEjercicio> prs = calcularPRs(registros);

        // Transformar a DTOs con fecha formateada
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        List<ProgresoDetalleView> registrosDTO = registros.stream()
                .map(r -> new ProgresoDetalleView(
                        r.getFecha().toLocalDate().format(formatter),
                        r.getPesoUsado(),
                        r.getRepeticiones()
                ))
                .toList();

        List<ProgresoDetalleView> prsDTO = prs.stream()
                .map(r -> new ProgresoDetalleView(
                        r.getFecha().toLocalDate().format(formatter),
                        r.getPesoUsado(),
                        r.getRepeticiones()
                ))
                .toList();

        Map<String, Object> data = new HashMap<>();
        data.put("registros", registrosDTO);
        data.put("prs", prsDTO);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
                .create();

        response.getWriter().write(gson.toJson(data));

    }

    private List<ProgresoEjercicio> calcularPRs(List<ProgresoEjercicio> registros) {
        if (registros == null || registros.isEmpty()) return Collections.emptyList();

        List<ProgresoEjercicio> listaConRm = new ArrayList<>(registros);
        listaConRm.sort((a, b) -> {
            double rmA = a.getPesoUsado() * (1 + a.getRepeticiones() / 30.0);
            double rmB = b.getPesoUsado() * (1 + b.getRepeticiones() / 30.0);
            return Double.compare(rmB, rmA);
        });

        return listaConRm.subList(0, Math.min(3, listaConRm.size()));
    }
}
