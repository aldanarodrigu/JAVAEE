package com.appchat.dto;

public class MensajeWSDTO {
    private Long chatId;
    private String contenido;

    public Long getChatId(){ 
        return chatId; 
    }
    
    public void setChatId(Long chatId){ 
        this.chatId = chatId; 
    }

    public String getContenido() { 
        return contenido; 
    }
    public void setContenido(String contenido) { 
        this.contenido = contenido; 
    }
}