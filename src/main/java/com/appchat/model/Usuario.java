package com.appchat.model;

public class Usuario {

    private Long id;
    private String email;
    private String password;
    private String nombre;
    private String apellido;

    public Usuario() {
    }

    public Usuario(Long id, String email, String password, String nombre, String apellido) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.nombre = nombre;
        this.apellido = apellido;
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }

    public void setId(Long id) { this.id = id; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setApellido(String apellido) { this.apellido = apellido; }
}