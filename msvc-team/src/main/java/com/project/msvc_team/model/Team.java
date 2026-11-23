package com.project.msvc_team.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del equipo es obligatorio")
    private String nombre;

    @NotBlank(message = "El coach es obligatorio")
    private String coach;

    @NotNull(message = "El juego asociado es obligatorio")
    private Long juegoId;

    @NotBlank(message = "El estado es obligatorio")
    private String estado;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long authId;

    public Team() {
    }

    public Team(
            Long id,
            String nombre,
            String coach,
            Long juegoId,
            String estado
    ) {
        this.id = id;
        this.nombre = nombre;
        this.coach = coach;
        this.juegoId = juegoId;
        this.estado = estado;
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

    public Long getJuegoId() {
        return juegoId;
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

    public void setJuegoId(Long juegoId) {
        this.juegoId = juegoId;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setAuthId(Long authId) {
        this.authId = authId;
    }
}
