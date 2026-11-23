package com.project.msvc_game.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Juego {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El genero es obligatorio")
    private String genero;

    @NotBlank(message = "La modalidad es obligatoria")
    private String modalidad;

    @NotNull(message = "Jugadores por equipo obligatorio")
    @Min(value = 1, message = "Debe ser mayor a 0")
    private Integer jugadoresPorEquipo;

    @NotBlank(message = "El estado es obligatorio")
    private String estado;

    @Column(length = 1000)
    private String imagen;

    public Juego() {
    }

    public Juego(Long id, String nombre, String genero,
                 String modalidad, Integer jugadoresPorEquipo,
                 String estado) {
        this.id = id;
        this.nombre = nombre;
        this.genero = genero;
        this.modalidad = modalidad;
        this.jugadoresPorEquipo = jugadoresPorEquipo;
        this.estado = estado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getModalidad() {
        return modalidad;
    }

    public void setModalidad(String modalidad) {
        this.modalidad = modalidad;
    }

    public Integer getJugadoresPorEquipo() {
        return jugadoresPorEquipo;
    }

    public void setJugadoresPorEquipo(Integer jugadoresPorEquipo) {
        this.jugadoresPorEquipo = jugadoresPorEquipo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
}
