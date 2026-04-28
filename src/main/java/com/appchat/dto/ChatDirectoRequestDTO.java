package com.appchat.dto;

import jakarta.validation.constraints.NotNull;

public class ChatDirectoRequestDTO {
    
    @NotNull
    private Long usuarioDestinoId;
    
    @NotNull
    private Long comunidadId;

    public Long getUsuarioDestinoId() {
        return usuarioDestinoId;
    }

    public void setUsuarioDestinoId(Long usuarioDestinoId) {
        this.usuarioDestinoId = usuarioDestinoId;
    }

    public Long getComunidadId() {
        return comunidadId;
    }

    public void setComunidadId(Long comunidadId) {
        this.comunidadId = comunidadId;
    }
    
}