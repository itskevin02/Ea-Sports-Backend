package com.project.msvc_user.seguridad;

import com.project.msvc_user.repository.UsuarioRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "eureka.client.enabled=false")
@AutoConfigureMockMvc
class RegistroPerfilTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Value("${app.jwt.secreto}")
    private String secreto;

    @BeforeEach
    void preparar() {
        usuarioRepository.deleteAll();
    }

    private String crearToken(String rol, Long authId) {

        SecretKey clave = Keys.hmacShaKeyFor(
                secreto.getBytes(StandardCharsets.UTF_8)
        );

        return Jwts.builder()
                .subject("jugadorPrueba")
                .claim("rol", rol)
                .claim("authId", authId)
                .issuedAt(new Date())
                .expiration(new Date(
                        System.currentTimeMillis() + 60000
                ))
                .signWith(clave)
                .compact();
    }

    private String datosPerfil() {
        return """
                {
                    "nombre": "Jugador Nuevo",
                    "correo": "nuevo@ejemplo.com",
                    "nickname": "jugadorNuevo"
                }
                """;
    }

    @Test
    void usuarioRegistraSuPerfil() throws Exception {

        String token = crearToken("USUARIO", 501L);

        mockMvc.perform(post("/users/registro-perfil")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(datosPerfil()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.authId").value(501))
                .andExpect(jsonPath("$.rol").value("USUARIO"))
                .andExpect(jsonPath("$.estado").value("ACTIVO"));
    }

    @Test
    void noPermiteRegistrarDosPerfiles() throws Exception {

        String token = crearToken("USUARIO", 502L);

        mockMvc.perform(post("/users/registro-perfil")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(datosPerfil()))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/users/registro-perfil")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(datosPerfil()))
                .andExpect(status().isConflict());
    }

    @Test
    void noPermiteRegistrarSinToken() throws Exception {

        mockMvc.perform(post("/users/registro-perfil")
                .contentType(MediaType.APPLICATION_JSON)
                .content(datosPerfil()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void administradorNoUtilizaRegistroPublico() throws Exception {

        String token = crearToken("ADMIN", 1L);

        mockMvc.perform(post("/users/registro-perfil")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(datosPerfil()))
                .andExpect(status().isForbidden());
    }
}
