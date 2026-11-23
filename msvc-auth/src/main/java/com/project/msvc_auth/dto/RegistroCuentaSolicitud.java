package com.project.msvc_auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistroCuentaSolicitud(

        @NotBlank(message = "El usuario es obligatorio")
        @Size(min = 4, max = 30,
                message = "El usuario debe tener entre 4 y 30 caracteres")
        String username,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, max = 72,
                message = "La contraseña debe tener entre 8 y 72 caracteres")
        String password

) {
}
