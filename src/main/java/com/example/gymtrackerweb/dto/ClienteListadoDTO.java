package com.example.gymtrackerweb.dto;

import java.sql.Date;
import java.util.Objects;

/**
 * DTO para la vista mostrarClientes.jsp
 * Trae datos básicos del cliente + estado resumido de su membresía
 * ya listos para renderizar en la tabla (sin recalcular en JSP).
 */
public class ClienteListadoDTO {

    private String ci;
    private String nombre;
    private String apellido;
    private String email;
    private String ciudad;
    private String pais;
    private Date   fechaIngreso;

    // ===== Membresía (última conocida) =====
    private Integer estadoId;           // FK a estado_membresia.id (nullable si no hay membresía)
    private String  estadoNombre;       // "activa", "pausada", "cancelada", "baja", "inactiva", "trial" (nullable)
    private Date    fechaFinMembresia;  // nullable

    // ===== Estado temporal (calculado en capa servicio/DAO) =====
    private EstadoTemporal bucketTemporal; // ACTIVA, VENCE_HOY, VENCIDA_LT10, VENCIDA_GTE10, SIN
    private Integer diasDesdeVencimiento;  // null si ACTIVA o SIN

    // ===== Contacto / acciones =====
    private String telNormalizado;      // para wa.me (ej: +5989XXXXXXX), nullable si no hay
    private String mensajeReenganche;   // ya listo para URL-encode en capa web, si corresponde

    // ===== Opcional: semáforo directo para la UI (evita lógica en JSP) =====
    private SemaforoWhatsapp whatsappSemaforo; // VERDE / ROJO / GRIS

    // ---- Enums internos para evitar más archivos por ahora ----
    public enum EstadoTemporal {
        ACTIVA, VENCE_HOY, VENCIDA_LT10, VENCIDA_GTE10, SIN
    }

    public enum SemaforoWhatsapp {
        VERDE, ROJO, GRIS, AMARILLO
    }
    private String bucketTemporalStr;     // "ACTIVA", "VENCE_HOY", ...
    private String whatsappSemaforoStr;   // "VERDE", "ROJO", "GRIS"

    public String getBucketTemporalStr() { return bucketTemporalStr; }
    public void setBucketTemporalStr(String s) { this.bucketTemporalStr = s; }

    public String getWhatsappSemaforoStr() { return whatsappSemaforoStr; }
    public void setWhatsappSemaforoStr(String s) { this.whatsappSemaforoStr = s; }


    // ===== Getters/Setters =====
    public String getCi() { return ci; }
    public void setCi(String ci) { this.ci = ci; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    public Date getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(Date fechaIngreso) { this.fechaIngreso = fechaIngreso; }

    public Integer getEstadoId() { return estadoId; }
    public void setEstadoId(Integer estadoId) { this.estadoId = estadoId; }

    public String getEstadoNombre() { return estadoNombre; }
    public void setEstadoNombre(String estadoNombre) { this.estadoNombre = estadoNombre; }

    public Date getFechaFinMembresia() { return fechaFinMembresia; }
    public void setFechaFinMembresia(Date fechaFinMembresia) { this.fechaFinMembresia = fechaFinMembresia; }

    public EstadoTemporal getBucketTemporal() { return bucketTemporal; }
    public void setBucketTemporal(EstadoTemporal bucketTemporal) { this.bucketTemporal = bucketTemporal; }

    public Integer getDiasDesdeVencimiento() { return diasDesdeVencimiento; }
    public void setDiasDesdeVencimiento(Integer diasDesdeVencimiento) { this.diasDesdeVencimiento = diasDesdeVencimiento; }

    public String getTelNormalizado() { return telNormalizado; }
    public void setTelNormalizado(String telNormalizado) { this.telNormalizado = telNormalizado; }

    public String getMensajeReenganche() { return mensajeReenganche; }
    public void setMensajeReenganche(String mensajeReenganche) { this.mensajeReenganche = mensajeReenganche; }

    public SemaforoWhatsapp getWhatsappSemaforo() { return whatsappSemaforo; }
    public void setWhatsappSemaforo(SemaforoWhatsapp whatsappSemaforo) { this.whatsappSemaforo = whatsappSemaforo; }

    // helpers que no se si usaremos najsdkdkasn

    /** Nombre completo ya listo para pintar en la tabla. */
    public String getNombreCompleto() {
        String n = nombre == null ? "" : nombre.trim();
        String a = apellido == null ? "" : apellido.trim();
        String s = (n + " " + a).trim();
        return s.isEmpty() ? (email != null ? email : ci) : s;
    }

    /** Conveniente para ordenar estable cuando falte algo. */
    public String getOrdenSecundario() {
        return Objects.toString(ci, "");
    }
}
