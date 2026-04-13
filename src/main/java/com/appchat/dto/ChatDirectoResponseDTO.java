package com.appchat.dto;

public class ChatDirectoResponseDTO {

    private boolean creado;
    private ChatResumenDTO chat;

    public ChatDirectoResponseDTO() {
    }

    public ChatDirectoResponseDTO(boolean creado, ChatResumenDTO chat) {
        this.creado = creado;
        this.chat = chat;
    }

    public boolean isCreado() {
        return creado;
    }

    public void setCreado(boolean creado) {
        this.creado = creado;
    }

    public ChatResumenDTO getChat() {
        return chat;
    }

    public void setChat(ChatResumenDTO chat) {
        this.chat = chat;
    }
}
