
package com.project.msvc_tournament.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

@Entity
public class Tournament {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del torneo es obligatorio")
    private String nombre;

    @NotBlank(message = "La ubicacion es obligatoria")
    private String ubicacion;

    @Positive(message = "La cantidad de equipos debe ser mayor a 0")
    @NotNull(message = "La cantidad de equipos es obligatoria")
    private Integer cantidadEquipos;

    @NotBlank(message = "El estado es obligatorio")
    private String estado;

    @NotNull(message = "El juego es obligatorio")
    @Positive(message = "El ID del juego debe ser mayor a 0")
    private Long juegoId;

    @NotBlank(message = "La plataforma es obligatoria")
    private String plataforma;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de cierre es obligatoria")
    private LocalDate fechaCierre;

    @NotBlank(message = "La descripcion es obligatoria")
    private String descripcion;

    private String imagen;

    public Tournament() {
    }

    public Tournament(Long id, String nombre,
                      String ubicacion, Integer cantidadEquipos,
                      String estado) {
        this.id = id;
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.cantidadEquipos = cantidadEquipos;
        this.estado = estado;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public Integer getCantidadEquipos() {
        return cantidadEquipos;
    }

    public String getEstado() {
        return estado;
    }

    public Long getJuegoId() {
        return juegoId;
    }

    public String getPlataforma() {
        return plataforma;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public LocalDate getFechaCierre() {
        return fechaCierre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getImagen() {
        return imagen;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public void setCantidadEquipos(Integer cantidadEquipos) {
        this.cantidadEquipos = cantidadEquipos;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setJuegoId(Long juegoId) {
        this.juegoId = juegoId;
    }

    public void setPlataforma(String plataforma) {
        this.plataforma = plataforma;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public void setFechaCierre(LocalDate fechaCierre) {
        this.fechaCierre = fechaCierre;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
}
