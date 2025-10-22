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

        String limiteStr = request.getParameter("limite");
        Integer limite = null;
        if (limiteStr != null) {
            try {
                limite = Integer.parseInt(limiteStr);
            } catch (NumberFormatException e) {
                // Ignorar si el formato es inválido, se devolverán todos
            }
        }

        ProgresoEjercicioDAO dao = new ProgresoEjercicioDAO();
        List<ProgresoEjercicio> registrosCompletos = dao.obtenerRegistrosDetallados(idEjercicio, idCliente);
        if (registrosCompletos == null) registrosCompletos = new ArrayList<>();

        // Ordenar por fecha descendente (más reciente primero)
        registrosCompletos.sort((a, b) -> b.getFecha().compareTo(a.getFecha()));

        // 2. Aplicar el límite si existe
        List<ProgresoEjercicio> registrosParaEnviar;
        boolean hayMasRegistros = false;

        if (limite != null && limite > 0 && registrosCompletos.size() > limite) {
            registrosParaEnviar = registrosCompletos.subList(0, limite);
            hayMasRegistros = true; // Informamos que hay más registros que no se enviaron
        } else {
            registrosParaEnviar = registrosCompletos; // Enviar la lista completa
        }

        // El cálculo de PRs se hace sobre la lista completa siempre
        List<ProgresoEjercicio> prs = calcularPRs(registrosCompletos);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // 3. Procesar la lista a enviar (que puede estar limitada o no)
        List<ProgresoDetalleView> registrosDTO = new ArrayList<>();
        for (int i = 0; i < registrosParaEnviar.size(); i++) {
            ProgresoEjercicio actual = registrosParaEnviar.get(i);
            int diferencia = 0;
            // La lógica de diferencia debe mirar la lista completa para ser precisa
            int indiceOriginal = registrosCompletos.indexOf(actual);
            if (indiceOriginal + 1 < registrosCompletos.size()) {
                ProgresoEjercicio anterior = registrosCompletos.get(indiceOriginal + 1);
                diferencia = actual.getPesoUsado() - anterior.getPesoUsado();
            }

            registrosDTO.add(new ProgresoDetalleView(
                    actual.getFecha().toLocalDate().format(formatter),
                    actual.getPesoUsado(),
                    actual.getRepeticiones(),
                    diferencia
            ));
        }

        // PRs
        List<ProgresoDetalleView> prsDTO = new ArrayList<>();
        for (ProgresoEjercicio pr : prs) {
            prsDTO.add(new ProgresoDetalleView(
                    pr.getFecha().toLocalDate().format(formatter),
                    pr.getPesoUsado(),
                    pr.getRepeticiones(),
                    null // No calculamos diferencia en PRs
            ));
        }


        Map<String, Object> data = new HashMap<>();
        data.put("registros", registrosDTO);
        data.put("prs", prsDTO);
        data.put("hayMasRegistros", hayMasRegistros);

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
