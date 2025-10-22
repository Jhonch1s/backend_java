package com.example.gymtrackerweb.servlet;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.gymtrackerweb.utils.CloudinaryConfig;
import jakarta.servlet.*;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.util.Map;

@WebServlet(name = "UploadImagenServlet", urlPatterns = {"/uploadImagen"})
@MultipartConfig
public class UploadImagenServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Part filePart = request.getPart("imagen"); // <- name del <input>
        if (filePart == null || filePart.getSize() == 0) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No se recibió archivo");
            return;
        }

        File tmp = File.createTempFile("upload_", ".bin");
        try (InputStream in = filePart.getInputStream();
             OutputStream out = new FileOutputStream(tmp)) {
            in.transferTo(out);
        }

        Cloudinary cloud = CloudinaryConfig.getInstance();
        Map res = cloud.uploader().upload(tmp, ObjectUtils.emptyMap());
        tmp.delete();

        String url = (String) res.get("secure_url");
        response.setContentType("text/plain; charset=UTF-8");
        response.getWriter().println("OK -> " + url);
    }
}
