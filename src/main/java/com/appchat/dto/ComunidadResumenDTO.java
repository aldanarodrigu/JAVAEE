package com.appchat.dto;

public class ComunidadResumenDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private String fotoUrl;

    public ComunidadResumenDTO() {}

    public ComunidadResumenDTO(Long id, String nombre, String descripcion, String fotoUrl) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fotoUrl = fotoUrl;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getFotoUrl() { return fotoUrl; }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }
}