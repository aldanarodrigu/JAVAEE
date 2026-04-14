package com.appchat.dto;

import java.util.Date;

public class ChatResumenDTO {

    private Long id;
    private Date fechaCreacion;
    private Long usuarioInterlocutorId;
    private String usuarioInterlocutorNombre;
    private String ultimoMensajeContenido;
    private Date ultimoMensajeFecha;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Long getUsuarioInterlocutorId() {
        return usuarioInterlocutorId;
    }

    public void setUsuarioInterlocutorId(Long usuarioInterlocutorId) {
        this.usuarioInterlocutorId = usuarioInterlocutorId;
    }

    public String getUsuarioInterlocutorNombre() {
        return usuarioInterlocutorNombre;
    }

    public void setUsuarioInterlocutorNombre(String usuarioInterlocutorNombre) {
        this.usuarioInterlocutorNombre = usuarioInterlocutorNombre;
    }

    public String getUltimoMensajeContenido() {
        return ultimoMensajeContenido;
    }

    public void setUltimoMensajeContenido(String ultimoMensajeContenido) {
        this.ultimoMensajeContenido = ultimoMensajeContenido;
    }

    public Date getUltimoMensajeFecha() {
        return ultimoMensajeFecha;
    }

    public void setUltimoMensajeFecha(Date ultimoMensajeFecha) {
        this.ultimoMensajeFecha = ultimoMensajeFecha;
    }
}