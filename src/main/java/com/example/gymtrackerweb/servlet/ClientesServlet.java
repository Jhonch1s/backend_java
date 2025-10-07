package com.example.gymtrackerweb.servlet;

import com.example.gymtrackerweb.dao.ClienteDAO;
import com.example.gymtrackerweb.model.Cliente;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Date;

@WebServlet(name = "ClientesServlet", value = "/clientes")
public class ClientesServlet extends HttpServlet {

    ClienteDAO clienteDAO = new ClienteDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Crear objeto Cliente con datos del formulario
        Cliente c = new Cliente();
        c.setCi(request.getParameter("ci"));
        c.setEmail(request.getParameter("email"));
        c.setNombre(request.getParameter("nombre"));
        c.setApellido(request.getParameter("apellido"));
        c.setCiudad(request.getParameter("ciudad"));
        c.setDireccion(request.getParameter("direccion"));
        c.setTel(request.getParameter("tel"));
        c.setPais(request.getParameter("pais"));

        // Convertir fecha de String a java.sql.Date me dio mil dramas esta porqueria hdp
        String fechaStr = request.getParameter("fecha_ingreso"); // yyyy-MM-dd
        if(fechaStr != null && !fechaStr.isEmpty()){
            c.setFechaIngreso(Date.valueOf(fechaStr));
        }

        try {
            clienteDAO.agregarCliente(c);
            // Redirigir al listado de clientes aunque no los mostremos aún
            response.sendRedirect(request.getContextPath() + "/pages/dashboard.jsp#/clientes");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "No se pudo crear el cliente: " + e.getMessage()); //esto muestra por consola
            request.getRequestDispatcher("/pages/dashboard.jsp#/clientes").forward(request, response); //esto me mantiene en el mismo form
        }
    }
}


