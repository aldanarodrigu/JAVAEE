package com.appchat.dto;

import com.appchat.model.enums.EstadoMensaje;
import com.appchat.model.enums.TipoMensaje;
import java.util.Date;

public class MensajeDTO {

    private Long id;
    private Long chatId;
    private Long remitenteId;
    private Date fechaEnvio;
    private TipoMensaje tipo;
    private EstadoMensaje estado;
    private String contenido;

    public MensajeDTO() {
    }

    public MensajeDTO(Long id, Long chatId, Long remitenteId, Date fechaEnvio, TipoMensaje tipo, EstadoMensaje estado, String contenido) {
        this.id = id;
        this.chatId = chatId;
        this.remitenteId = remitenteId;
        this.fechaEnvio = fechaEnvio;
        this.tipo = tipo;
        this.estado = estado;
        this.contenido = contenido;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Long getRemitenteId() {
        return remitenteId;
    }

    public void setRemitenteId(Long remitenteId) {
        this.remitenteId = remitenteId;
    }

    public Date getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(Date fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    public TipoMensaje getTipo() {
        return tipo;
    }

    public void setTipo(TipoMensaje tipo) {
        this.tipo = tipo;
    }

    public EstadoMensaje getEstado() {
        return estado;
    }

    public void setEstado(EstadoMensaje estado) {
        this.estado = estado;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }
}
