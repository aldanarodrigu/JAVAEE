package com.appchat.dto;

import jakarta.validation.constraints.NotBlank;

public class ActualizarEstadoDTO {
    
    @NotBlank
    private String estado;

    public ActualizarEstadoDTO() {
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}