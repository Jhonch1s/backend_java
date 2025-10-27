package com.example.gymtrackerweb.servlet;

import com.cloudinary.utils.ObjectUtils;
import com.example.gymtrackerweb.dao.ClienteFotoDAO;
import com.example.gymtrackerweb.model.Cliente;
import com.example.gymtrackerweb.utils.CloudinaryConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

@WebServlet(name="SubirFotoPerfilServlet", urlPatterns={"/cliente/foto/subir"})
@MultipartConfig
public class SubirFotoPerfilServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        var usuario = (Cliente) req.getSession().getAttribute("usuario");
        if (usuario == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        Part filePart = req.getPart("imagen");
        if (filePart == null || filePart.getSize() == 0) {
            resp.sendError(400, "Falta archivo");
            return;
        }
        if (filePart.getContentType() == null || !filePart.getContentType().startsWith("image/")) {
            resp.sendError(415, "Formato no permitido");
            return;
        }

        File tmp = File.createTempFile("pfp_", ".bin");
        try (var in = filePart.getInputStream(); var out = new FileOutputStream(tmp)) {
            in.transferTo(out);
        }

        Map resCld;
        try {
            var cloud = CloudinaryConfig.getInstance();
            resCld = cloud.uploader().upload(tmp, ObjectUtils.asMap(
                    "folder", "gymtracker/perfiles",
                    "overwrite", true,
                    "resource_type", "image"
            ));
        } catch (Exception e) {
            throw new ServletException("Error al subir a Cloudinary", e);
        } finally {
            tmp.delete();
        }

        String secureUrl = (String) resCld.get("secure_url");

        try {
            new ClienteFotoDAO().upsertUrl(usuario.getCi(), secureUrl);
        } catch (SQLException e) {
            throw new ServletException("No se pudo guardar la URL de la foto", e);
        }

        resp.sendRedirect(req.getContextPath() + "/cliente/perfil");
    }
}
