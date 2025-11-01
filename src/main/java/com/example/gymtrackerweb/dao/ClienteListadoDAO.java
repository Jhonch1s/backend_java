package com.example.gymtrackerweb.dao;

import com.example.gymtrackerweb.db.databaseConection;
import com.example.gymtrackerweb.dto.ClienteListadoDTO;
import com.example.gymtrackerweb.utils.MembresiaHelper;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class ClienteListadoDAO {

    public static class ListarClientesParams {
        public String q;
        public String ciudad;
        public String pais;
        public Date fiFrom;
        public Date fiTo;
        public String estado;

        public String sort = "fecha_ingreso";
        public String dir  = "DESC";
        public int page = 1;
        public int size = 20;
    }

    public static class PageResult<T> {
        public final List<T> items;
        public final int total;
        public final int page;
        public final int size;
        public final int pages;
        public PageResult(List<T> items, int total, int page, int size) {
            this.items = items;
            this.total = total;
            this.page = page;
            this.size = size;
            this.pages = (int) Math.ceil(total / (double) size);
        }
    }

    public PageResult<ClienteListadoDTO> listar(ListarClientesParams p) throws SQLException {
        validarYNormalizar(p);

        StringBuilder baseSql = new StringBuilder();
        List<Object> args = new ArrayList<>();

        String innerSelect = """
            SELECT
                c.ci,
                c.nombre,
                c.apellido,
                c.email,
                c.ciudad,
                c.pais,
                c.fecha_ingreso,
                c.tel,
                m.estado_id,
                em.nombre AS estado_nombre,
                m.fecha_fin AS fecha_fin_membresia,
                CASE
                       WHEN LOWER(COALESCE(em.nombre,'')) IN ('pausada','cancelada','baja','inactivo') THEN 'SIN'
                       WHEN m.fecha_fin IS NULL THEN 'SIN'
                       WHEN LOWER(COALESCE(em.nombre,'')) IN ('activo','trial') AND m.fecha_fin >  CURDATE() THEN 'ACTIVA'
                       WHEN LOWER(COALESCE(em.nombre,'')) IN ('activo','trial') AND m.fecha_fin =  CURDATE() THEN 'VENCE_HOY'
                       WHEN LOWER(COALESCE(em.nombre,'')) IN ('activo','trial') AND DATEDIFF(CURDATE(), m.fecha_fin) BETWEEN 1 AND 10 THEN 'VENCIDA_LT10'
                       WHEN LOWER(COALESCE(em.nombre,'')) IN ('activo ','trial') AND DATEDIFF(CURDATE(), m.fecha_fin) > 10 THEN 'VENCIDA_GTE10'
                       ELSE 'SIN'
                END AS bucket_temporal
            FROM cliente c
            LEFT JOIN (
                SELECT x.*
                FROM membresia x
                JOIN (
                    SELECT id_cliente, MAX(fecha_fin) AS max_fin
                    FROM membresia
                    GROUP BY id_cliente
                ) u ON u.id_cliente = x.id_cliente AND u.max_fin = x.fecha_fin
            ) m ON m.id_cliente = c.ci
            LEFT JOIN estado_membresia em ON em.id = m.estado_id
        """;
        StringBuilder whereClause = new StringBuilder(" WHERE 1=1 ");
        List<Object> whereArgs = new ArrayList<>();

        if (notBlank(p.q)) {
            whereClause.append("""
                AND (
                      t.ci = ?
                   OR LOWER(t.email) LIKE ?
                   OR LOWER(CONCAT(COALESCE(t.nombre,''),' ',COALESCE(t.apellido,''))) LIKE ?
                )
            """);
            whereArgs.add(p.q.trim());
            String like = "%" + p.q.trim().toLowerCase(Locale.ROOT) + "%";
            whereArgs.add(like);
            whereArgs.add(like);
        }

        if (notBlank(p.ciudad)) { whereClause.append(" AND t.ciudad = ? "); whereArgs.add(p.ciudad.trim()); }
        if (notBlank(p.pais))   { whereClause.append(" AND t.pais   = ? "); whereArgs.add(p.pais.trim()); }
        if (p.fiFrom != null)   { whereClause.append(" AND t.fecha_ingreso >= ? "); whereArgs.add(p.fiFrom); }
        if (p.fiTo != null)     { whereClause.append(" AND t.fecha_ingreso <= ? "); whereArgs.add(p.fiTo); }

        switch (p.estado) {
            case "activos"        -> whereClause.append(" AND (t.bucket_temporal IN ('ACTIVA','VENCE_HOY')) ");
            case "vencidos_lt10"  -> whereClause.append(" AND t.bucket_temporal = 'VENCIDA_LT10' ");
            case "vencidos_gte10" -> whereClause.append(" AND t.bucket_temporal = 'VENCIDA_GTE10' ");
            case "todos" -> {}
            default -> {}
        }

        String orderBy = mapSort(p.sort); // (ver función actualizada abajo)
        String dir = "ASC".equalsIgnoreCase(p.dir) ? "ASC" : "DESC";

        String listSql =
                "SELECT * FROM (" + innerSelect + ") t" +
                        whereClause +
                        " ORDER BY " + orderBy + " " + dir + ", t.ci ASC " +
                        " LIMIT ? OFFSET ? ";

        List<Object> listArgs = new ArrayList<>(whereArgs);
        int offset = (p.page - 1) * p.size;
        listArgs.add(p.size);
        listArgs.add(offset);

        String countSql = "SELECT COUNT(1) FROM (" + innerSelect + ") t" + whereClause;


        int total;
        List<ClienteListadoDTO> items = new ArrayList<>();
        Connection cn = databaseConection.getInstancia().getConnection();
        try (
             PreparedStatement psCount = cn.prepareStatement(countSql.toString());
             PreparedStatement psList  = cn.prepareStatement(listSql.toString())) {

            setArgs(psCount, whereArgs);
            try (ResultSet rs = psCount.executeQuery()) {
                total = rs.next() ? rs.getInt(1) : 0;
            }

            setArgs(psList, listArgs);
            try (ResultSet rs = psList.executeQuery()) {
                while (rs.next()) {
                    ClienteListadoDTO dto = new ClienteListadoDTO();
                    dto.setCi(rs.getString("ci"));
                    dto.setNombre(rs.getString("nombre"));
                    dto.setApellido(rs.getString("apellido"));
                    dto.setEmail(rs.getString("email"));
                    dto.setCiudad(rs.getString("ciudad"));
                    dto.setPais(rs.getString("pais"));
                    dto.setFechaIngreso(rs.getDate("fecha_ingreso"));
                    dto.setEstadoId((Integer) rs.getObject("estado_id"));
                    dto.setEstadoNombre(rs.getString("estado_nombre"));
                    Date fin = rs.getDate("fecha_fin_membresia");
                    String tel = rs.getString("tel");

                    LocalDate hoyUy = LocalDate.now(ZoneId.of("America/Montevideo"));
                    MembresiaHelper.rellenarCamposMembresia(dto, dto.getEstadoNombre(), fin, tel, hoyUy);
                    System.out.printf("CI=%s estado=%s fin=%s bucket=%s semaforo=%s tel=%s dias=%s%n",
                            dto.getCi(),
                            dto.getEstadoNombre(),
                            dto.getFechaFinMembresia(),
                            dto.getBucketTemporalStr(),
                            dto.getWhatsappSemaforoStr(),
                            dto.getTelNormalizado(),
                            dto.getDiasDesdeVencimiento()
                    );

                    items.add(dto);
                }
            }
        }

        return new PageResult<>(items, total, p.page, p.size);
    }


    private static boolean notBlank(String s) {
        return s != null && !s.trim().isEmpty();
    }

    private static String mapSort(String sort) {
        Map<String, String> map = Map.ofEntries(
                Map.entry("ci", "t.ci"),
                Map.entry("nombre", "t.nombre"),
                Map.entry("apellido", "t.apellido"),
                Map.entry("email", "t.email"),
                Map.entry("ciudad", "t.ciudad"),
                Map.entry("pais", "t.pais"),
                Map.entry("fecha_ingreso", "t.fecha_ingreso"),
                Map.entry("fecha_fin_membresia", "t.fecha_fin_membresia"),
                Map.entry("estado_membresia",
                        "CASE LOWER(COALESCE(t.estado_nombre,'')) " +
                                " WHEN 'activa' THEN 0 " +
                                " WHEN 'trial' THEN 1 " +
                                " WHEN 'pausada' THEN 2 " +
                                " WHEN 'inactiva' THEN 3 " +
                                " WHEN 'cancelada' THEN 4 " +
                                " WHEN 'baja' THEN 5 " +
                                " ELSE 9 END"
                )
        );
        return map.getOrDefault(sort, "t.fecha_ingreso");
    }


    private static void setArgs(PreparedStatement ps, List<Object> args) throws SQLException {
        for (int i = 0; i < args.size(); i++) {
            Object a = args.get(i);
            if (a instanceof java.util.Date && !(a instanceof java.sql.Date)) {
                ps.setDate(i + 1, new java.sql.Date(((java.util.Date) a).getTime()));
            } else if (a instanceof Integer) {
                ps.setInt(i + 1, (Integer) a);
            } else {
                ps.setObject(i + 1, a);
            }
        }
    }

    private void validarYNormalizar(ListarClientesParams p) {
        if (p == null) p = new ListarClientesParams();

        if (p.q != null)      p.q = p.q.trim();
        if (p.ciudad != null) p.ciudad = p.ciudad.trim();
        if (p.pais != null)   p.pais = p.pais.trim();

        if (p.estado == null) p.estado = "todos";
        else {
            String e = p.estado.trim().toLowerCase(Locale.ROOT);
            switch (e) {
                case "activos", "vencidos_lt10", "vencidos_gte10", "todos" -> p.estado = e;
                default -> p.estado = "todos";
            }
        }

        if (p.sort == null || p.sort.isBlank()) p.sort = "fecha_ingreso";
        p.dir = (p.dir != null && p.dir.equalsIgnoreCase("ASC")) ? "ASC" : "DESC";

        if (p.page < 1) p.page = 1;
        if (p.size < 1) p.size = 20;
        if (p.size > 100) p.size = 100;

        if (p.fiFrom != null && p.fiTo != null && p.fiFrom.after(p.fiTo)) {
            Date aux = p.fiFrom; p.fiFrom = p.fiTo; p.fiTo = aux;
        }
    }
}
