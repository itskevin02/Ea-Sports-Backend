package com.project.msvc_auth.dto;

public record UsuarioDTO(
        Long authId,
        String username,
        String rol
) {
}
