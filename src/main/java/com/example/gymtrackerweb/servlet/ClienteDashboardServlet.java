package com.example.gymtrackerweb.servlet;

import com.example.gymtrackerweb.dao.ProgresoEjercicioDAO;
import com.example.gymtrackerweb.dao.RegistroGymDAO;
import com.example.gymtrackerweb.dao.RutinaDAO;
import com.example.gymtrackerweb.model.Cliente;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;

@WebServlet(name = "ClienteDashboardServlet", urlPatterns = {"/cliente"})
public class ClienteDashboardServlet extends HttpServlet {

    private static final String JSP_INDEX = "/pages/cliente/index.jsp";


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // UTF-8 por las dudas (acentos/emojis)
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");

        //chequeamos que este seteado el usuario, sino chau
        Cliente usuario = (Cliente) req.getSession().getAttribute("usuario");
        if (usuario == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        //depurando
        //System.out.println("CI en sesión = [" + usuario.getCi() + "]");

        try{
            ProgresoEjercicioDAO progresoEjercicioDAO = new ProgresoEjercicioDAO();
            //zona horaria local
            ZoneId zoneId = ZoneId.of("America/Montevideo");
            YearMonth ym = YearMonth.now(zoneId);
            //obtenemos los datos
            BigDecimal kgLevantadosMes =  progresoEjercicioDAO.obtenerKgLevantadosEnMes(usuario.getCi(), ym.getYear(), ym.getMonthValue());
            BigDecimal kgLevantadosTotal =  progresoEjercicioDAO.obtenerKgLevantadosTotal(usuario.getCi());

            //depurando...
            //System.out.println("Kg levantados (mes): " + kgLevantadosMes);
            //System.out.println("Kg levantados (total): " + kgLevantadosTotal);

            var dao = new RegistroGymDAO();
            LocalDate hoyUy = LocalDate.now(ZoneId.of("America/Montevideo"));
            String ci = usuario.getCi();

            req.setAttribute("diasEntrenadosMes", dao.contarDiasEntrenadosMes(ci, hoyUy));
            req.setAttribute("totalSesionesTotal", dao.totalSesionesHistorico(ci));
            req.setAttribute("minPromedioSesion", dao.minutosPromedioPorSesionMes(ci, hoyUy));

            //card Rutinas
            RutinaDAO rutinaDAO = new RutinaDAO();
            req.setAttribute("rutinasTop", rutinaDAO.ultimas3PorCliente(usuario.getCi()));

            //card progresos
            ProgresoEjercicioDAO progresoDAO = new ProgresoEjercicioDAO();
            req.setAttribute("progresosTop", progresoDAO.ultimos3Progresos(usuario.getCi()));

            //mandamos los datos
            req.setAttribute("kgLevantadosMes", kgLevantadosMes);
            req.setAttribute("kgLevantadosTotal", kgLevantadosTotal);
            req.getRequestDispatcher(JSP_INDEX).forward(req, resp);

        }catch(Exception e){
            throw new ServletException("Error al procesar los datos", e);
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // si se llega con POST lo tratamos igual que GET
        doGet(req, resp);
    }
}
