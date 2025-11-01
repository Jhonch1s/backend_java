package com.example.gymtrackerweb.servlet.staff;

import com.example.gymtrackerweb.dao.ClienteDAO;
import com.example.gymtrackerweb.dto.ClienteConRutinasDTO;
import com.example.gymtrackerweb.dto.ClienteRutinaDTO;
import com.example.gymtrackerweb.dto.RutinaAsignadaDTO;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/admin/asignar-rutina-cliente")
public class CargarAsignacionesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        ClienteDAO clienteDAO = new ClienteDAO();
        List<ClienteRutinaDTO> filas = clienteDAO.listarFilasClienteRutinaActiva();

        Map<String, ClienteConRutinasDTO> clientesMap = new LinkedHashMap<>();

        for (ClienteRutinaDTO fila : filas) {
            String ci = fila.getClienteCi();

            ClienteConRutinasDTO clienteCard = clientesMap.get(ci);

            if (clienteCard == null) {
                // Si es la primera vez que vemos este cliente, creamos su tarjeta
                clienteCard = new ClienteConRutinasDTO(
                        ci,
                        fila.getClienteNombre(),
                        fila.getClienteApellido()
                );
                clientesMap.put(ci, clienteCard); // Lo agregamos al Map
            }

            if (fila.getRutinaNombre() != null) {
                clienteCard.addRutina(new RutinaAsignadaDTO(
                        fila.getRutinaNombre(),
                        (fila.getFechaAsignacion() != null) ? java.sql.Date.valueOf(fila.getFechaAsignacion()) : null,
                        fila.getEstadoRutina()
                ));
            }
        }

        List<ClienteConRutinasDTO> listaAgrupada = new ArrayList<>(clientesMap.values());
        request.setAttribute("listaClientesRutinas", listaAgrupada);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/pages/staff/rutina/asignarClientes.jsp");
        dispatcher.forward(request, response);
    }
}
