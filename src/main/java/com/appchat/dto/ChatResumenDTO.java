package com.appchat.dto;

import java.util.Date;

public class ChatResumenDTO {

    private Long chatId;
    private String ultimoMensaje;
    private Date fechaUltimoMensaje;

    public ChatResumenDTO() {
    }

    public ChatResumenDTO(Long chatId, String ultimoMensaje, Date fechaUltimoMensaje) {
        this.chatId = chatId;
        this.ultimoMensaje = ultimoMensaje;
        this.fechaUltimoMensaje = fechaUltimoMensaje;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getUltimoMensaje() {
        return ultimoMensaje;
    }

    public void setUltimoMensaje(String ultimoMensaje) {
        this.ultimoMensaje = ultimoMensaje;
    }

    public Date getFechaUltimoMensaje() {
        return fechaUltimoMensaje;
    }

    public void setFechaUltimoMensaje(Date fechaUltimoMensaje) {
        this.fechaUltimoMensaje = fechaUltimoMensaje;
    }
}
