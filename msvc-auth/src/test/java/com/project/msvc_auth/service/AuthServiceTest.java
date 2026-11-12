package com.project.msvc_auth.service;

import com.project.msvc_auth.model.Auth;
import com.project.msvc_auth.repository.AuthRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private AuthRepository authRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void listarAuthTest() {

        Auth auth = new Auth();
        auth.setUsername("usuario1");

        when(authRepository.findAll()).thenReturn(Arrays.asList(auth));

        assertEquals(1, authService.listarAuth().size());

        verify(authRepository, times(1)).findAll();
    }

    @Test
    void buscarAuthPorIdTest() {

        Auth auth = new Auth();
        auth.setId(1L);
        auth.setUsername("usuario1");

        when(authRepository.findById(1L)).thenReturn(Optional.of(auth));

        Auth resultado = authService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals("usuario1", resultado.getUsername());

        verify(authRepository, times(1)).findById(1L);
    }

    @Test
    void guardarAuthTest() {

        Auth auth = new Auth();
        auth.setUsername("usuarioNuevo");
        auth.setPassword("1234");

        when(passwordEncoder.encode("1234")).thenReturn("hash1234");
        when(authRepository.save(auth)).thenReturn(auth);

        Auth resultado = authService.guardarAuth(auth);

        assertNotNull(resultado);
        assertEquals("usuarioNuevo", resultado.getUsername());
        assertEquals("hash1234", resultado.getPassword());

        verify(passwordEncoder, times(1)).encode("1234");
        verify(authRepository, times(1)).save(auth);
    }

    @Test
    void actualizarAuthTest() {

        Auth authExistente = new Auth();
        authExistente.setId(1L);
        authExistente.setUsername("usuario1");
        authExistente.setPassword("hashAnterior");
        authExistente.setRol("USER");

        Auth nuevoAuth = new Auth();
        nuevoAuth.setUsername("usuarioActualizado");
        nuevoAuth.setPassword("5678");
        nuevoAuth.setRol("ADMIN");

        when(authRepository.findById(1L)).thenReturn(Optional.of(authExistente));
        when(passwordEncoder.encode("5678")).thenReturn("hash5678");
        when(authRepository.save(any(Auth.class))).thenReturn(authExistente);

        Auth resultado = authService.actualizarAuth(1L, nuevoAuth);

        assertNotNull(resultado);
        assertEquals("usuarioActualizado", resultado.getUsername());
        assertEquals("hash5678", resultado.getPassword());
        assertEquals("ADMIN", resultado.getRol());

        verify(passwordEncoder, times(1)).encode("5678");
        verify(authRepository, times(1)).findById(1L);
        verify(authRepository, times(1)).save(any(Auth.class));
    }

    @Test
    void eliminarAuthTest() {

        authService.eliminarAuth(1L);

        verify(authRepository, times(1)).deleteById(1L);
    }
}