package com.project.msvc_user.service;

import com.project.msvc_user.dto.RegistroPerfilSolicitud;
import com.project.msvc_user.model.Usuario;
import com.project.msvc_user.repository.UsuarioRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class RegistroPerfilService {

    private final UsuarioRepository usuarioRepository;

    public RegistroPerfilService(
            UsuarioRepository usuarioRepository
    ) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario registrar(
            RegistroPerfilSolicitud datos,
            Long authId
    ) {

        if (authId == null || authId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "La cuenta no es valida"
            );
        }

        if (usuarioRepository.findByAuthId(authId).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La cuenta ya tiene un perfil registrado"
            );
        }

        String nombre = datos.nombre().trim();
        String correo = datos.correo().trim();
        String nickname = datos.nickname().trim();

        if (nickname.contains(" ")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El nickname no puede contener espacios"
            );
        }

        if (usuarioRepository.existsByCorreoIgnoreCase(correo)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El correo ya esta registrado"
            );
        }

        if (usuarioRepository.existsByNicknameIgnoreCase(nickname)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El nickname ya esta registrado"
            );
        }

        Usuario usuario = new Usuario();

        usuario.setAuthId(authId);
        usuario.setNombre(nombre);
        usuario.setCorreo(correo);
        usuario.setNickname(nickname);
        usuario.setRol("USUARIO");
        usuario.setEstado("ACTIVO");

        return usuarioRepository.save(usuario);
    }
}
