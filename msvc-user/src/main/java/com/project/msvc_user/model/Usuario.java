package com.project.msvc_user.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.validation.constraints.NotBlank;

@Entity
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long authId;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El correo es obligatorio")
    private String correo;

    @NotBlank(message = "El nickname es obligatorio")
    private String nickname;

    @NotBlank(message = "El rol es obligatorio")
    private String rol;

    @NotBlank(message = "El estado es obligatorio")
    private String estado;

    public Usuario() {
    }

    public Usuario(
            Long id,
            String nombre,
            String correo,
            String nickname,
            String rol,
            String estado
    ) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.nickname = nickname;
        this.rol = rol;
        this.estado = estado;
    }

    public Long getId() {
        return id;
    }

    public Long getAuthId() {
        return authId;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public String getNickname() {
        return nickname;
    }

    public String getRol() {
        return rol;
    }

    public String getEstado() {
        return estado;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setAuthId(Long authId) {
        this.authId = authId;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
