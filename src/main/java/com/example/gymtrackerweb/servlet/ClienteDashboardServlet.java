package com.example.gymtrackerweb.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "ClienteDashboardServlet", urlPatterns = {"/modulos", "/cliente", "/dashboard"})
public class ClienteDashboardServlet extends HttpServlet {

    private static final String JSP_INDEX = "/pages/cliente/index.jsp";


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // UTF-8 por las dudas (acentos/emojis)
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");

        req.getRequestDispatcher(JSP_INDEX).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // si se llega con POST lo tratamos igual que GET
        doGet(req, resp);
    }
}
