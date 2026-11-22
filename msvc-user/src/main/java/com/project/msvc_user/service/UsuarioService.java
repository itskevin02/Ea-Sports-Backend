package com.project.msvc_user.service;

import com.project.msvc_user.model.Usuario;
import com.project.msvc_user.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class UsuarioService {

    private static final Logger logger =
            LoggerFactory.getLogger(UsuarioService.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private SancionValidador sancionValidador;

    public List<Usuario> listarUsuarios() {

        logger.info("Listando usuarios");

        return usuarioRepository.findAll();
    }

    public Usuario guardarUsuario(Usuario usuario) {

        logger.info("Guardando usuario: " + usuario.getNombre());

        return usuarioRepository.save(usuario);
    }

    public Usuario buscarPorId(Long id) {

        logger.info("Buscando usuario con id: " + id);

        return usuarioRepository.findById(id).orElse(null);
    }

    public Usuario actualizarUsuario(Long id, Usuario usuario) {

        logger.info("Actualizando usuario con id: " + id);

        Usuario usuarioExistente =
                usuarioRepository.findById(id).orElse(null);

        if (usuarioExistente != null) {

            usuarioExistente.setNombre(usuario.getNombre());
            usuarioExistente.setCorreo(usuario.getCorreo());
            usuarioExistente.setNickname(usuario.getNickname());
            usuarioExistente.setRol(usuario.getRol());
            usuarioExistente.setEstado(usuario.getEstado());

            return usuarioRepository.save(usuarioExistente);
        }

        return null;
    }

    public void eliminarUsuario(
            Long id,
            String authorization
    ) {

        logger.info("Validando eliminacion del usuario: " + id);

        if (!usuarioRepository.existsById(id)) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "El usuario no existe"
            );
        }

        sancionValidador.validarSinSanciones(
                id,
                authorization
        );

        logger.info("Eliminando usuario con id: " + id);

        usuarioRepository.deleteById(id);
    }
}
