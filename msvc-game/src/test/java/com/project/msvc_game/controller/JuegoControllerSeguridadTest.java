
package com.project.msvc_game.controller;

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
class JuegoControllerSeguridadTest {

    @Autowired
    private MockMvc mockMvc;

    @Value("${app.jwt.secreto}")
    private String secreto;

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

    private String juegoJson() {
        return """
                {
                    "nombre": "Valorant",
                    "genero": "Shooter",
                    "modalidad": "Online",
                    "jugadoresPorEquipo": 5,
                    "estado": "ACTIVO"
                }
                """;
    }

    @Test
    void listarSinTokenTest() throws Exception {

        mockMvc.perform(get("/games"))
                .andExpect(status().isOk());
    }

    @Test
    void guardarSinTokenTest() throws Exception {

        mockMvc.perform(post("/games")
                .contentType(MediaType.APPLICATION_JSON)
                .content(juegoJson()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void guardarConRolUserTest() throws Exception {

        mockMvc.perform(post("/games")
                .header("Authorization", "Bearer " + generarToken("USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(juegoJson()))
                .andExpect(status().isForbidden());
    }

    @Test
    void guardarConRolAdminTest() throws Exception {

        mockMvc.perform(post("/games")
                .header("Authorization", "Bearer " + generarToken("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(juegoJson()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Valorant"));
    }

    @Test
    void tokenInvalidoTest() throws Exception {

        mockMvc.perform(post("/games")
                .header("Authorization", "Bearer token-invalido")
                .contentType(MediaType.APPLICATION_JSON)
                .content(juegoJson()))
                .andExpect(status().isUnauthorized());
    }
}
