package com.appchat.model;

import com.appchat.model.enums.EstadoUsuario;
import com.appchat.model.enums.RolSistema;
import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private EstadoUsuario estado;
    
    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private RolSistema rolSistema;

    public Long getId(){ 
        return id; 
    }
    
    public void setId(Long id){ 
        this.id = id; 
    }
    
    public String getNombre(){ 
     return nombre; 
    }
    
    public void setNombre(String nombre){ 
        this.nombre = nombre; 
    }
    
    public String getApellido(){ 
        return apellido; 
    }
    
    public void setApellido(String apellido){ 
        this.apellido = apellido; 
    }
    
    public String getEmail(){ 
        return email; 
    }
    
    public void setEmail(String email){ 
        this.email = email; 
    }
    
    public String getPassword(){ 
        return password; 
    }
    
    public void setPassword(String password){ 
        this.password = password; 
    }
    
    public EstadoUsuario getEstado(){ 
        return estado; 
    }
    
    public void setEstado(EstadoUsuario estado){ 
        this.estado = estado; 
    }

    public RolSistema getRolSistema() {
        return rolSistema;
    }

    public void setRolSistema(RolSistema rolSistema) {
        this.rolSistema = rolSistema;
    }
    
}