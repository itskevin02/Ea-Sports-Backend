package com.project.msvc_auth.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginSolicitud(
        @NotBlank String username,
        @NotBlank String password
) {
}
