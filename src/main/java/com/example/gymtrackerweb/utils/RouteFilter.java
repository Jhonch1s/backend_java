package com.example.gymtrackerweb.utils;


import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.Normalizer;
import java.util.regex.Pattern;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.RequestDispatcher;

@WebFilter(
        urlPatterns = "/*",
        dispatcherTypes = { DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.INCLUDE, DispatcherType.ERROR }
)
public class RouteFilter implements Filter {

    private static class Rule {
        final Pattern p; final String key;
        Rule(String regex, String key) {
            this.p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            this.key = key;
        }
        boolean matches(String s){ return p.matcher(s).find(); }
    }

    private Rule[] rules;

    @Override
    public void init(FilterConfig filterConfig) {
        rules = new Rule[] {
                // INICIO
                new Rule("^/cliente/?$", "inicio"),

                // RUTINAS
                new Rule("^/cliente/listarutinas(?:/.*)?$", "rutinas"),
                new Rule("^/progreso-?ejercicios(?:/.*)?$", "rutinas"),

                // PROGRESOS
                new Rule("^/cliente/progreso(?:/.*)?$", "progresos"),

                // ESTADÍSTICAS (incluye /cliente/stats/...)
                new Rule("^/cliente/estadisticas(?:/.*)?$", "estadisticas"),
                new Rule("^/cliente/stats(?:/.*)?$", "estadisticas"),

                // PERFIL
                new Rule("^/cliente/perfil(?:/.*)?$", "perfil"),
        };
        System.out.println("[RouteFilter] init OK");
    }

    private static String stripAccentsLower(String s) {
        String n = Normalizer.normalize(s, Normalizer.Form.NFD);
        return n.replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase();
    }

    private String effectivePath(HttpServletRequest req) {
        // Si es FORWARD/INCLUDE, usamos la URI original del request
        String uri = (String) req.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI);
        if (uri == null) uri = (String) req.getAttribute(RequestDispatcher.INCLUDE_REQUEST_URI);
        if (uri == null) uri = req.getRequestURI(); // fallback

        String ctx  = req.getContextPath();
        String path = uri.substring(ctx.length());

        // Normalizamos: sin acentos, lowercase
        return stripAccentsLower(path);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;

        String norm = effectivePath(req); // <- clave
        String navActive = null;
        for (Rule r : rules) {
            if (r.matches(norm)) { navActive = r.key; break; }
        }
        if (navActive == null) navActive = "inicio";

        // Logs útiles
        String uriNow = req.getRequestURI();
        System.out.println("[RouteFilter] eff=" + norm + " (from uri=" + uriNow + ") -> navActive=" + navActive);

        request.setAttribute("navActive", navActive);
        chain.doFilter(request, response);
    }
}
