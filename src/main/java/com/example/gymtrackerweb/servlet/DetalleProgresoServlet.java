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

        // --- LÓGICA DE PAGINACIÓN ---
        String paginaStr = request.getParameter("pagina");
        int pagina = 1; // Página por defecto
        int tamanoPagina = 5; // Registros por página
        if (paginaStr != null) {
            try {
                pagina = Integer.parseInt(paginaStr);
                if (pagina < 1) pagina = 1;
            } catch (NumberFormatException e) {
                pagina = 1; // Volver a la página 1 si el formato es inválido
            }
        }
        // --- FIN LÓGICA DE PAGINACIÓN ---


        ProgresoEjercicioDAO dao = new ProgresoEjercicioDAO();
        List<ProgresoEjercicio> registrosCompletos = dao.obtenerRegistrosDetallados(idEjercicio, idCliente);
        if (registrosCompletos == null) registrosCompletos = new ArrayList<>();

        // Ordenar por fecha descendente (más reciente primero)
        registrosCompletos.sort((a, b) -> b.getFecha().compareTo(a.getFecha()));

        // 2. Aplicar la paginación a la lista completa
        int totalRegistros = registrosCompletos.size();
        int totalPaginas = (int) Math.ceil((double) totalRegistros / tamanoPagina);

        // Asegurarse de que la página solicitada no esté fuera de rango
        if (pagina > totalPaginas && totalPaginas > 0) {
            pagina = totalPaginas;
        }

        int inicio = (pagina - 1) * tamanoPagina;
        int fin = Math.min(inicio + tamanoPagina, totalRegistros);

        List<ProgresoEjercicio> registrosParaEnviar;
        if (inicio > fin) {
            registrosParaEnviar = new ArrayList<>(); // Si no hay registros o la página está mal, lista vacía
        } else {
            registrosParaEnviar = registrosCompletos.subList(inicio, fin); // Obtenemos la página
        }

        List<ProgresoEjercicio> prs = calcularPRs(registrosCompletos);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        List<ProgresoDetalleView> registrosDTO = new ArrayList<>();
        for (int i = 0; i < registrosParaEnviar.size(); i++) {
            ProgresoEjercicio actual = registrosParaEnviar.get(i);
            int diferencia = 0;

            // La lógica de diferencia sigue funcionando porque mira la lista completa
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

        // PRs (sin cambios)
        List<ProgresoDetalleView> prsDTO = new ArrayList<>();
        for (ProgresoEjercicio pr : prs) {
            prsDTO.add(new ProgresoDetalleView(
                    pr.getFecha().toLocalDate().format(formatter),
                    pr.getPesoUsado(),
                    pr.getRepeticiones(),
                    null // No calculamos diferencia en PRs
            ));
        }

        List<ProgresoEjercicio> rms = calcularRMs(registrosCompletos);

        List<ProgresoDetalleView> rmsDTO = new ArrayList<>();
        for (ProgresoEjercicio rm : rms) {
            rmsDTO.add(new ProgresoDetalleView(
                    rm.getFecha().toLocalDate().format(formatter),
                    rm.getPesoUsado(),
                    rm.getRepeticiones(),
                    null
            ));
        }


        Map<String, Object> data = new HashMap<>();
        data.put("registros", registrosDTO);
        data.put("prs", prsDTO);
        data.put("rms", rmsDTO);
        // Enviamos la información de paginación
        data.put("paginaActual", pagina);
        data.put("totalPaginas", totalPaginas);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");

        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
                .create();

        response.getWriter().write(gson.toJson(data));
    }

    private List<ProgresoEjercicio> calcularPRs(List<ProgresoEjercicio> registros) {
        if (registros == null || registros.isEmpty()) return Collections.emptyList();

        List<ProgresoEjercicio> filtrados = new ArrayList<>();
        for (ProgresoEjercicio r : registros) {
            if (r.getRepeticiones() >= 4) {
                filtrados.add(r);
            }
        }
        filtrados.sort((a, b) -> Integer.compare(b.getPesoUsado(), a.getPesoUsado()));

        return filtrados;
    }


    private List<ProgresoEjercicio> calcularRMs(List<ProgresoEjercicio> registros) {
        if (registros == null || registros.isEmpty()) return Collections.emptyList();

        // 1. Encontrar el peso máximo histórico
        int maxPeso = registros.stream()
                .mapToInt(ProgresoEjercicio::getPesoUsado)
                .max()
                .orElse(0);

        // 2. Filtrar solo series válidas para RM: 1-3 repeticiones y peso >= 50% del máximo
        List<ProgresoEjercicio> filtrados = new ArrayList<>();
        for (ProgresoEjercicio r : registros) {
            if (r.getRepeticiones() >= 1 && r.getRepeticiones() <= 3) {
                double rmEstimado = r.getPesoUsado() * (1 + r.getRepeticiones() / 30.0); // fórmula Epley
                if (rmEstimado >= maxPeso * 0.5) { // al menos 50% del máximo histórico
                    filtrados.add(r);
                }
            }
        }

        filtrados.sort((a, b) -> Integer.compare(b.getPesoUsado(), a.getPesoUsado()));

        return filtrados;
    }



}
