package com.project.msvc_user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistroPerfilSolicitud(

        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no es valido")
        String correo,

        @NotBlank(message = "El nickname es obligatorio")
        @Size(min = 3, max = 30,
                message = "El nickname debe tener entre 3 y 30 caracteres")
        String nickname

) {
}
