package com.appchat.dto;

import com.appchat.model.enums.EstadoUsuario;
import jakarta.validation.constraints.NotBlank;

public class UsuarioDTO{
    
    @NotBlank
    private String nombre;
    
    @NotBlank
    private String apellido;
    
    @NotBlank
    private String email;
    
    @NotBlank
    private String userName;
    
    @NotBlank
    private String password;
    
    private EstadoUsuario estado;
    
    private String fotoPerfil;

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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public EstadoUsuario getEstado() {
        return estado;
    }

    public void setEstado(EstadoUsuario estado) {
        this.estado = estado;
    }

    public String getFotoPerfil() { return fotoPerfil; }

    public void setFotoPerfil(String fotoPerfil) { this.fotoPerfil = fotoPerfil;}

    
}
