package com.project.msvc_auth.dto;

public record LoginRespuesta(
        String token,
        UsuarioDTO usuario
) {
}
