package com.appchat.model;

import com.appchat.model.enums.RolGrupo;
import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(
    name = "participa",
    uniqueConstraints = @UniqueConstraint(columnNames = {"chat_id", "usuario_id"})
)
public class Participa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date fechaUnion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RolGrupo rol;

    public Participa() {
    }

    public Participa(Chat chat, Usuario usuario, Date fechaUnion, RolGrupo rol) {
        this.chat = chat;
        this.usuario = usuario;
        this.fechaUnion = fechaUnion;
        this.rol = rol;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Date getFechaUnion() {
        return fechaUnion;
    }

    public void setFechaUnion(Date fechaUnion) {
        this.fechaUnion = fechaUnion;
    }

    public RolGrupo getRol() {
        return rol;
    }

    public void setRol(RolGrupo rol) {
        this.rol = rol;
    }
}
