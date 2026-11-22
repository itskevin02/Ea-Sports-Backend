package com.project.msvc_user.service;

import com.project.msvc_user.model.Usuario;
import com.project.msvc_user.repository.UsuarioRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private SancionValidador sancionValidador;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void listarUsuariosTest() {

        Usuario usuario = new Usuario();
        usuario.setNombre("Usuario 1");

        when(usuarioRepository.findAll())
                .thenReturn(Arrays.asList(usuario));

        assertEquals(
                1,
                usuarioService.listarUsuarios().size()
        );

        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    void buscarUsuarioPorIdTest() {

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Usuario 1");

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        Usuario resultado = usuarioService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals("Usuario 1", resultado.getNombre());

        verify(usuarioRepository, times(1)).findById(1L);
    }

    @Test
    void guardarUsuarioTest() {

        Usuario usuario = new Usuario();
        usuario.setNombre("Usuario Nuevo");

        when(usuarioRepository.save(usuario))
                .thenReturn(usuario);

        Usuario resultado = usuarioService.guardarUsuario(usuario);

        assertNotNull(resultado);
        assertEquals("Usuario Nuevo", resultado.getNombre());

        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    void actualizarUsuarioTest() {

        Usuario usuarioExistente = new Usuario();
        usuarioExistente.setId(1L);
        usuarioExistente.setNombre("Usuario 1");
        usuarioExistente.setCorreo("correo1@correo.com");
        usuarioExistente.setNickname("usuario1");
        usuarioExistente.setRol("Jugador");
        usuarioExistente.setEstado("Activo");

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre("Usuario Actualizado");
        nuevoUsuario.setCorreo("correo2@correo.com");
        nuevoUsuario.setNickname("usuario2");
        nuevoUsuario.setRol("Administrador");
        nuevoUsuario.setEstado("Inactivo");

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuarioExistente));

        when(usuarioRepository.save(any(Usuario.class)))
                .thenReturn(usuarioExistente);

        Usuario resultado = usuarioService.actualizarUsuario(
                1L,
                nuevoUsuario
        );

        assertNotNull(resultado);

        assertEquals(
                "Usuario Actualizado",
                resultado.getNombre()
        );

        assertEquals(
                "correo2@correo.com",
                resultado.getCorreo()
        );

        assertEquals(
                "usuario2",
                resultado.getNickname()
        );

        assertEquals(
                "Administrador",
                resultado.getRol()
        );

        assertEquals(
                "Inactivo",
                resultado.getEstado()
        );

        verify(usuarioRepository, times(1)).findById(1L);

        verify(usuarioRepository, times(1))
                .save(any(Usuario.class));
    }

    @Test
    void eliminarUsuarioTest() {

        // El usuario existe
        when(usuarioRepository.existsById(1L))
                .thenReturn(true);

        usuarioService.eliminarUsuario(
                1L,
                "Bearer prueba"
        );

        verify(sancionValidador, times(1))
                .validarSinSanciones(
                        1L,
                        "Bearer prueba"
                );

        verify(usuarioRepository, times(1))
                .deleteById(1L);
    }
}
