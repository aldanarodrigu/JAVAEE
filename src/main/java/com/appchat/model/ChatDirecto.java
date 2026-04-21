package com.appchat.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.List;

@Entity
@DiscriminatorValue("DIRECTO")
public class ChatDirecto extends Chat {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_uno_id", nullable = false)
    private Usuario usuarioUno;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_dos_id", nullable = false)
    private Usuario usuarioDos;

    public Usuario getUsuarioUno() {
        return usuarioUno;
    }

    public void setUsuarioUno(Usuario usuarioUno) {
        this.usuarioUno = usuarioUno;
    }

    public Usuario getUsuarioDos() {
        return usuarioDos;
    }

    public void setUsuarioDos(Usuario usuarioDos) {
        this.usuarioDos = usuarioDos;
    }

    @Override
    public List<Usuario> getParticipantes() {
        return List.of(usuarioUno, usuarioDos);
    }
}