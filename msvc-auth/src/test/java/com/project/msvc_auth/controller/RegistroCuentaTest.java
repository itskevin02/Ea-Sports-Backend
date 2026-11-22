package com.project.msvc_auth.controller;

import com.project.msvc_auth.model.Auth;
import com.project.msvc_auth.repository.AuthRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "eureka.client.enabled=false")
@AutoConfigureMockMvc
class RegistroCuentaTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void preparar() {
        authRepository.deleteAll();
    }

    private String datosRegistro() {
        return """
                {
                    "username": "jugadorNuevoEP3",
                    "password": "ClaveJugador123"
                }
                """;
    }

    @Test
    void registroCorrecto() throws Exception {

        mockMvc.perform(post("/auth/registro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(datosRegistro()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.authId").isNumber())
                .andExpect(jsonPath("$.username")
                        .value("jugadorNuevoEP3"))
                .andExpect(jsonPath("$.rol")
                        .value("USUARIO"))
                .andExpect(jsonPath("$.password")
                        .doesNotExist());

        Auth guardado = authRepository
                .findByUsername("jugadorNuevoEP3")
                .orElseThrow();

        assertEquals("USUARIO", guardado.getRol());

        assertTrue(passwordEncoder.matches(
                "ClaveJugador123",
                guardado.getPassword()
        ));
    }

    @Test
    void rechazaUsuarioDuplicado() throws Exception {

        mockMvc.perform(post("/auth/registro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(datosRegistro()))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/auth/registro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(datosRegistro()))
                .andExpect(status().isConflict());
    }

    @Test
    void rechazaPasswordCorta() throws Exception {

        mockMvc.perform(post("/auth/registro")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "username": "jugadorNuevoEP3",
                            "password": "123"
                        }
                        """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void nuncaPermiteRegistrarAdmin() throws Exception {

        mockMvc.perform(post("/auth/registro")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "username": "jugadorNuevoEP3",
                            "password": "ClaveJugador123",
                            "rol": "ADMIN"
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rol")
                        .value("USUARIO"));
    }

    @Test
    void puedeIniciarSesionDespuesDeRegistrarse()
            throws Exception {

        mockMvc.perform(post("/auth/registro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(datosRegistro()))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(datosRegistro()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.usuario.rol")
                        .value("USUARIO"));
    }
}
