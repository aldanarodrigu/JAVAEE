package com.appchat.model;

import com.appchat.model.enums.EstadoInvitacion;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class InvitacionComunidad {

    @Id
    @GeneratedValue
    private Long id;

    private Long comunidadId;
    private Long usuarioInvitadoId;
    private Long ownerId;

    @Enumerated(EnumType.STRING)
    private EstadoInvitacion estado;
    
}