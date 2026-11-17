
package com.project.msvc_tournament.controller;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TournamentControllerSeguridadTest {

    @Autowired
    private MockMvc mockMvc;

    @Value("${app.jwt.secreto}")
    private String secreto;

    // Generar un token para probar los permisos
    private String generarToken(String rol) {

        var clave = Keys.hmacShaKeyFor(
                secreto.getBytes(StandardCharsets.UTF_8)
        );

        return Jwts.builder()
                .subject("usuarioPrueba")
                .claim("rol", rol)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 60000))
                .signWith(clave)
                .compact();
    }

    // Datos válidos de un torneo
    private String torneoJson() {

        return """
                {
                    "nombre": "Copa eSports EP3",
                    "ubicacion": "Santiago",
                    "cantidadEquipos": 8,
                    "estado": "Abierto",
                    "juegoId": 1,
                    "plataforma": "PC",
                    "fechaInicio": "2026-12-20",
                    "fechaCierre": "2026-12-15",
                    "descripcion": "Torneo de prueba",
                    "imagen": "torneos/valorant.jpg"
                }
                """;
    }

    @Test
    void listarSinTokenTest() throws Exception {

        mockMvc.perform(get("/tournaments"))
                .andExpect(status().isOk());
    }

    @Test
    void guardarSinTokenTest() throws Exception {

        mockMvc.perform(post("/tournaments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(torneoJson()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void guardarConRolUserTest() throws Exception {

        mockMvc.perform(post("/tournaments")
                .header(
                        "Authorization",
                        "Bearer " + generarToken("USER")
                )
                .contentType(MediaType.APPLICATION_JSON)
                .content(torneoJson()))
                .andExpect(status().isForbidden());
    }

    @Test
    void guardarConRolAdminTest() throws Exception {

        mockMvc.perform(post("/tournaments")
                .header(
                        "Authorization",
                        "Bearer " + generarToken("ADMIN")
                )
                .contentType(MediaType.APPLICATION_JSON)
                .content(torneoJson()))
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.nombre").value("Copa eSports EP3")
                );
    }

    @Test
    void tokenInvalidoTest() throws Exception {

        mockMvc.perform(post("/tournaments")
                .header(
                        "Authorization",
                        "Bearer token-invalido"
                )
                .contentType(MediaType.APPLICATION_JSON)
                .content(torneoJson()))
                .andExpect(status().isUnauthorized());
    }
}
