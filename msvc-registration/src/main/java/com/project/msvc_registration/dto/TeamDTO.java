package com.project.msvc_registration.dto;

public class TeamDTO {

    private Long id;
    private String nombre;
    private String coach;
    private String estado;
    private Long authId;

    public TeamDTO() {
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCoach() {
        return coach;
    }

    public String getEstado() {
        return estado;
    }

    public Long getAuthId() {
        return authId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setCoach(String coach) {
        this.coach = coach;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setAuthId(Long authId) {
        this.authId = authId;
    }
}
