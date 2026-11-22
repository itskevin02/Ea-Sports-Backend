
package com.project.msvc_user.seguridad;

import com.project.msvc_user.model.Usuario;
import com.project.msvc_user.repository.UsuarioRepository;
import com.project.msvc_user.service.CuentaAuthValidador;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import org.springframework.test.web.servlet.MockMvc;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest(properties = "eureka.client.enabled=false")
@AutoConfigureMockMvc
class VinculacionAuthTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @MockitoBean
    private CuentaAuthValidador cuentaAuthValidador;

    @Value("${app.jwt.secreto}")
    private String secreto;

    private Usuario usuarioUno;
    private Usuario usuarioDos;
    private Usuario usuarioLibre;

    @BeforeEach
    void preparar() {

        usuarioRepository.deleteAll();

        usuarioUno = new Usuario(
                null,
                "Usuario Uno",
                "uno@ejemplo.com",
                "jugadorUno",
                "USUARIO",
                "ACTIVO"
        );

        usuarioUno.setAuthId(101L);
        usuarioUno = usuarioRepository.save(usuarioUno);

        usuarioDos = new Usuario(
                null,
                "Usuario Dos",
                "dos@ejemplo.com",
                "jugadorDos",
                "USUARIO",
                "ACTIVO"
        );

        usuarioDos.setAuthId(202L);
        usuarioDos = usuarioRepository.save(usuarioDos);

        usuarioLibre = new Usuario(
                null,
                "Usuario Libre",
                "libre@ejemplo.com",
                "jugadorLibre",
                "USUARIO",
                "ACTIVO"
        );

        usuarioLibre = usuarioRepository.save(usuarioLibre);
    }

    private String crearToken(String rol, Long authId) {

        SecretKey clave = Keys.hmacShaKeyFor(
                secreto.getBytes(StandardCharsets.UTF_8)
        );

        return Jwts.builder()
                .subject("cuentaPrueba")
                .claim("rol", rol)
                .claim("authId", authId)
                .issuedAt(new Date())
                .expiration(new Date(
                        System.currentTimeMillis() + 60000
                ))
                .signWith(clave)
                .compact();
    }

    @Test
    void usuarioConsultaSuPropioPerfil() throws Exception {

        String token = crearToken("USUARIO", 101L);

        mockMvc.perform(get("/users/mi-perfil")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authId").value(101))
                .andExpect(jsonPath("$.nickname")
                        .value("jugadorUno"));
    }

    @Test
    void segundaCuentaObtieneSuPerfil() throws Exception {

        String token = crearToken("USUARIO", 202L);

        mockMvc.perform(get("/users/mi-perfil")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authId").value(202))
                .andExpect(jsonPath("$.nickname")
                        .value("jugadorDos"));
    }

    @Test
    void cuentaSinPerfilDevuelve404() throws Exception {

        String token = crearToken("USUARIO", 999L);

        mockMvc.perform(get("/users/mi-perfil")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void usuarioNoPuedeListarTodosLosPerfiles() throws Exception {

        String token = crearToken("USUARIO", 101L);

        mockMvc.perform(get("/users")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void administradorPuedeVincularCuenta() throws Exception {

        String token = crearToken("ADMIN", 1L);

        mockMvc.perform(put(
                "/users/" + usuarioLibre.getId() +
                "/vincular-auth/303"
        ).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authId").value(303));

        verify(cuentaAuthValidador).validar(
                eq(303L),
                anyString()
        );
    }

    @Test
    void rechazaCuentaYaVinculada() throws Exception {

        String token = crearToken("ADMIN", 1L);

        mockMvc.perform(put(
                "/users/" + usuarioLibre.getId() +
                "/vincular-auth/202"
        ).header("Authorization", "Bearer " + token))
                .andExpect(status().isConflict());

        verify(cuentaAuthValidador, never())
                .validar(eq(202L), anyString());
    }
}
