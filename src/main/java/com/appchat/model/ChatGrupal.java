package com.appchat.model;

import com.appchat.model.enums.RolGrupo;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("GRUPAL")
public class ChatGrupal extends Chat {

    @Column(nullable = true)
    private String nombre;

    private String descripcion;
    private String fotoUrl;

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Participa> participantes = new ArrayList<>();

    @Override
    public List<Usuario> getParticipantes() {
        return participantes.stream().map(Participa::getUsuario).toList();
    }

    public void agregarParticipante(Usuario usuario, RolGrupo rol) {
        Participa p = new Participa();
        p.setUsuario(usuario);
        p.setChat(this);
        p.setRol(rol);
        participantes.add(p);
    }
    
    public boolean esAdmin(Long usuarioId) {
        return participantes.stream().anyMatch(p -> p.getUsuario().getId().equals(usuarioId)&& p.getRol() == RolGrupo.ADMIN);
    }

    public List<Participa> getMembresias(){ 
        return participantes; 
    }

    public String getNombre() {
        return nombre; 
    }
    
    public void setNombre(String nombre){ 
        this.nombre = nombre;
    }

    public String getDescripcion() { 
        return descripcion; 
    }
    public void setDescripcion(String descripcion) { 
        this.descripcion = descripcion;
    }

    public String getFotoUrl() { 
        return fotoUrl; 
    }
    
    public void setFotoUrl(String fotoUrl) { 
        this.fotoUrl = fotoUrl;
    }
}