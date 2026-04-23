package com.appchat.model;

import com.appchat.model.enums.RolGrupo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "participa",
    uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id", "chat_id"})
)
public class Participa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaUnion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RolGrupo rol;

    @PrePersist
    public void prePersist() {
        if (fechaUnion == null) fechaUnion = LocalDateTime.now();
        if (rol == null) rol = RolGrupo.MIEMBRO;
    }

    public Long getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Chat getChat() {   // 🔥 FIX
        return chat;
    }

    public void setChat(Chat chat) { // 🔥 FIX
        this.chat = chat;
    }

    public LocalDateTime getFechaUnion() {
        return fechaUnion;
    }

    public RolGrupo getRol() {
        return rol;
    }

    public void setRol(RolGrupo rol) {
        this.rol = rol;
    }
}