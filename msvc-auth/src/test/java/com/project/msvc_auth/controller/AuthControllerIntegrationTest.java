package com.project.msvc_auth.controller;

import com.project.msvc_auth.model.Auth;
import com.project.msvc_auth.repository.AuthRepository;
import com.project.msvc_auth.service.JwtServicio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtServicio jwtServicio;

    @BeforeEach
    void prepararDatos() {

        authRepository.deleteAll();

        Auth usuario = new Auth();
        usuario.setUsername("adminPrueba");
        usuario.setPassword(passwordEncoder.encode("Clave1234"));
        usuario.setRol("ADMIN");

        authRepository.save(usuario);
    }

    @Test
    void loginCorrectoTest() throws Exception {

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "username": "adminPrueba",
                            "password": "Clave1234"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.usuario.username").value("adminPrueba"))
                .andExpect(jsonPath("$.usuario.rol").value("ADMIN"))
                .andExpect(jsonPath("$.usuario.password").doesNotExist());
    }

    @Test
    void loginIncorrectoTest() throws Exception {

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "username": "adminPrueba",
                            "password": "Incorrecta"
                        }
                        """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensaje")
                        .value("Usuario o contraseña incorrectos"));
    }

    @Test
    void accesoSinTokenTest() throws Exception {

        mockMvc.perform(get("/auth"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void accesoSinPermisosTest() throws Exception {

        String token = jwtServicio.generar("usuarioPrueba", "USER");

        mockMvc.perform(get("/auth")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

@Test
void listadoNoMuestraPasswordTest() throws Exception {

    String token = jwtServicio.generar("adminPrueba", "ADMIN");

    mockMvc.perform(get("/auth")
            .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].username").value("adminPrueba"))
            .andExpect(jsonPath("$[0].password").doesNotExist());
}

@Test
void guardarNoMuestraPasswordTest() throws Exception {

    String token = jwtServicio.generar("adminPrueba", "ADMIN");

    mockMvc.perform(post("/auth")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                    {
                        "username": "usuarioNuevo",
                        "password": "Clave5678",
                        "rol": "USER"
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.username").value("usuarioNuevo"))
            .andExpect(jsonPath("$.password").doesNotExist());
    }
}