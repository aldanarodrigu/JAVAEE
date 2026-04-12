package com.appchat.dto;

import com.appchat.model.EstadoUsuario;

public class CambiarEstadoDTO {

    private EstadoUsuario estado;

    public EstadoUsuario getEstado() {
        return estado;
    }

    public void setEstado(EstadoUsuario estado) {
        this.estado = estado;
    }
}