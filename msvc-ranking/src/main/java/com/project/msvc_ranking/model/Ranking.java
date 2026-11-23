package com.project.msvc_ranking.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Ranking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El torneo es obligatorio")
    private Long torneoId;

    @NotBlank(message = "El nombre del equipo es obligatorio")
    private String nombreEquipo;

    @NotNull(message = "Los puntos son obligatorios")
    private Integer puntos;

    @NotNull(message = "La posicion es obligatoria")
    private Integer posicion;

    @NotBlank(message = "El estado es obligatorio")
    private String estado;

    public Ranking() {
    }

    public Ranking(Long id, String nombreEquipo,
                   Integer puntos, Integer posicion,
                   String estado) {

        this.id = id;
        this.nombreEquipo = nombreEquipo;
        this.puntos = puntos;
        this.posicion = posicion;
        this.estado = estado;
    }

    public Long getId() {
        return id;
    }

    public Long getTorneoId() {
        return torneoId;
    }

    public String getNombreEquipo() {
        return nombreEquipo;
    }

    public Integer getPuntos() {
        return puntos;
    }

    public Integer getPosicion() {
        return posicion;
    }

    public String getEstado() {
        return estado;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTorneoId(Long torneoId) {
        this.torneoId = torneoId;
    }

    public void setNombreEquipo(String nombreEquipo) {
        this.nombreEquipo = nombreEquipo;
    }

    public void setPuntos(Integer puntos) {
        this.puntos = puntos;
    }

    public void setPosicion(Integer posicion) {
        this.posicion = posicion;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}