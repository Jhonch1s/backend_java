package com.example.gymtrackerweb.servlet;

import com.example.gymtrackerweb.dao.ClienteDAO;
import com.example.gymtrackerweb.dao.ClienteFotoDAO;
import com.example.gymtrackerweb.dao.ProgresoEjercicioDAO;
import com.example.gymtrackerweb.dao.RegistroGymDAO;
import com.example.gymtrackerweb.dto.MembresiaPlanView;
import com.example.gymtrackerweb.model.Cliente;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@WebServlet(name = "ClientePerfilServlet", urlPatterns = {"/cliente/perfil"})
public class ClientePerfilServlet extends HttpServlet {

    private static final String JSP_PERFIL = "/pages/cliente/perfil.jsp";
    private static final ZoneId ZONE_UY = ZoneId.of("America/Montevideo");
    private static final DateTimeFormatter DF_DDMMYYYY = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");

        Cliente usuario = (Cliente) req.getSession().getAttribute("usuario");
        if (usuario == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        try {
            LocalDate hoyUy = LocalDate.now(ZONE_UY);
            YearMonth ymUy = YearMonth.from(hoyUy);
            var fotoDao = new ClienteFotoDAO();
            var urlOpt  = fotoDao.obtenerUrlPorCliente(usuario.getCi());
            req.setAttribute("clienteFotoUrl", urlOpt.orElse(null));


            //reciclado de dashboard
            RegistroGymDAO registroDAO = new RegistroGymDAO();
            ProgresoEjercicioDAO progresoDAO = new ProgresoEjercicioDAO();

            String ci = usuario.getCi();

            Integer entrenosMes = registroDAO.contarDiasEntrenadosMes(ci, hoyUy);
            Integer minPromedioMes = registroDAO.minutosPromedioPorSesionMes(ci, hoyUy);

            BigDecimal kgMes = progresoDAO.obtenerKgLevantadosEnMes(ci, ymUy.getYear(), ymUy.getMonthValue());
            BigDecimal kgTotal = progresoDAO.obtenerKgLevantadosTotal(ci);

            req.setAttribute("kpiEntrenamientosMes", safe(entrenosMes));
            req.setAttribute("kpiMinutosPromedioMes", safe(minPromedioMes));
            req.setAttribute("kpiKgMes", safe(kgMes));
            req.setAttribute("kpiKgTotal", safe(kgTotal));

            // Antigüedad a partir de fecha_ingreso del Cliente
            LocalDate ingreso = extraerFechaIngreso(usuario);
            req.setAttribute("fechaIngresoFmt", ingreso != null
                    ? ingreso.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    : "—");
            req.setAttribute("antiguedadHumana", ingreso != null
                    ? humanizarAntiguedad(ingreso, hoyUy)
                    : "—");

            // Pasamos cliente al request por comodidad
            req.setAttribute("cliente", usuario);

            //ciudad y país
            String ciudad = usuario.getCiudad();
            String pais   = usuario.getPais();
            req.setAttribute("ciudad", (ciudad != null && !ciudad.isBlank()) ? ciudad : null);
            req.setAttribute("pais",   (pais != null && !pais.isBlank()) ? pais : null);

            StringBuilder meta = new StringBuilder();
            if (usuario.getCi() != null && !usuario.getCi().isBlank()) meta.append("CI ").append(usuario.getCi());
            if (usuario.getEmail() != null && !usuario.getEmail().isBlank()) {
                if (!meta.isEmpty()) meta.append(" · ");
                meta.append(usuario.getEmail());
            }
            if (ciudad != null || pais != null) {
                if (!meta.isEmpty()) meta.append(" · ");
                if (ciudad != null) meta.append(ciudad);
                if (ciudad != null && pais != null) meta.append(", ");
                if (pais != null) meta.append(pais);
            }
            req.setAttribute("metaLinea", meta.isEmpty() ? "—" : meta.toString());

            var clienteDao = new ClienteDAO();
            Optional<MembresiaPlanView> opt = clienteDao.buscarMembresiaActivaPorCi(ci);

            if (opt.isPresent()) {
                MembresiaPlanView m = opt.get();

                // Estado (1=Activa, 2=Inactiva)
                boolean activa = (m.getEstadoId() == 1);

                // Fechas
                String venceFmt = (m.getFechaFin() != null)
                        ? DF_DDMMYYYY.format(toLocalDateSafe(m.getFechaFin(), ZONE_UY))
                        : "—";


                // Imagen con fallback
                String img = (m.getUrlImagen() != null && !m.getUrlImagen().isBlank())
                        ? m.getUrlImagen()
                        : (req.getContextPath() + "/assets/img/plan-default.png");

                // Atributos para el JSP
                req.setAttribute("membresiaActiva", m); // por si usamos tod0 el objeto mas adelante
                req.setAttribute("membresiaPlanNombre", nullToDash(m.getPlanNombre()));
                req.setAttribute("membresiaEstadoId", m.getEstadoId());
                req.setAttribute("membresiaEstadoTexto", activa ? "Activa" : "Inactiva");
                req.setAttribute("membresiaVenceFmt", venceFmt);
                req.setAttribute("membresiaImg", img);

                Integer diasRestantes = null;
                if (m.getFechaFin() != null) {
                    LocalDate fin = toLocalDateSafe(m.getFechaFin(), ZONE_UY);
                    LocalDate hoy = LocalDate.now(ZONE_UY);
                    long diff = ChronoUnit.DAYS.between(hoy, fin); // puede ser 0 si vence hoy
                    // clamp mínimo 0 para no mostrar negativos en badge (por si marcan activa con fecha pasada)
                    diasRestantes = (int) Math.max(0, diff);
                }
                req.setAttribute("membresiaDiasRestantes", diasRestantes);

                String badgeTipo = "ok";
                if (activa && diasRestantes != null) {
                    if (diasRestantes <= 3) badgeTipo = "alerta";      // rojo/urgente
                    else if (diasRestantes <= 7) badgeTipo = "warning"; // amarillo/aviso
                }
                req.setAttribute("membresiaBadgeTipo", badgeTipo);
            } else {
                // valores por defecto para que el JSP muestre “sin membresía”
                req.setAttribute("membresiaActiva", null);
                req.setAttribute("membresiaPlanNombre", "—");
                req.setAttribute("membresiaEstadoId", 2);
                req.setAttribute("membresiaEstadoTexto", "Sin membresía activa");
                req.setAttribute("membresiaVenceFmt", "—");
                req.setAttribute("membresiaImg", req.getContextPath() + "/assets/img/plan-default.png");
            }


            // Forward al JSP visual
            req.getRequestDispatcher(JSP_PERFIL).forward(req, resp);

        } catch (Exception e) {
            throw new ServletException("Error al cargar el perfil del cliente", e);
        }


    }

    // Helpers

    private static String humanizarAntiguedad(LocalDate desde, LocalDate hasta) {
        long meses = ChronoUnit.MONTHS.between(YearMonth.from(desde), YearMonth.from(hasta));
        long anios = meses / 12;
        long mesRest = meses % 12;
        if (anios <= 0) return mesRest + (mesRest == 1 ? " mes" : " meses");
        if (mesRest == 0) return anios + (anios == 1 ? " año" : " años");
        return anios + (anios == 1 ? " año " : " años ") + mesRest + (mesRest == 1 ? " mes" : " meses");
    }

    private static String safe(Integer n) { return n == null ? "—" : String.valueOf(n); }

    private static String safe(BigDecimal n) {
        return (n == null) ? "—" : n.stripTrailingZeros().toPlainString();
    }
    private static String nullToDash(String s) { return (s == null || s.isBlank()) ? "—" : s; }

    private static LocalDate extraerFechaIngreso(Cliente c) {
        try {
            java.lang.reflect.Method m = c.getClass().getMethod("getFecha_ingreso");
            Object val = m.invoke(c);
            if (val instanceof LocalDate ld) return ld;
            if (val instanceof java.sql.Date sd) return sd.toLocalDate();
            if (val instanceof java.util.Date ud) return Instant.ofEpochMilli(ud.getTime()).atZone(ZONE_UY).toLocalDate();
            if (val instanceof String s && !s.isBlank()) return LocalDate.parse(s);
        } catch (Exception ignore) {}
        try {
            java.lang.reflect.Method m = c.getClass().getMethod("getFechaIngreso");
            Object val = m.invoke(c);
            if (val instanceof LocalDate ld) return ld;
            if (val instanceof java.sql.Date sd) return sd.toLocalDate();
            if (val instanceof java.util.Date ud) return Instant.ofEpochMilli(ud.getTime()).atZone(ZONE_UY).toLocalDate();
            if (val instanceof String s && !s.isBlank()) return LocalDate.parse(s);
        } catch (Exception ignore) {}
        return null;
    }
    private static LocalDate toLocalDateSafe(java.util.Date date, ZoneId zone) {
        if (date == null) return null;
        if (date instanceof java.sql.Date sqlDate) {
            return sqlDate.toLocalDate();
        }
        return Instant.ofEpochMilli(date.getTime()).atZone(zone).toLocalDate();
    }

}
