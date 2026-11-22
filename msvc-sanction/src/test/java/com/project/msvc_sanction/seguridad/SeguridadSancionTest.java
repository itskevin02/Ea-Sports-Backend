package com.project.msvc_sanction.seguridad;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = "eureka.client.enabled=false"
)
class SeguridadSancionTest {

    @Autowired
    private Environment environment;

    @Value("${app.jwt.secreto}")
    private String secreto;

    private final RestTemplate restTemplate = new RestTemplate();

    private String urlSanciones() {

        String puerto = environment.getProperty(
                "local.server.port"
        );

        return "http://localhost:" + puerto + "/sanctions";
    }

    private String crearToken(String rol) {

        SecretKey clave = Keys.hmacShaKeyFor(
                secreto.getBytes(StandardCharsets.UTF_8)
        );

        return Jwts.builder()
                .subject("usuarioPrueba")
                .claim("rol", rol)
                .issuedAt(new Date())
                .expiration(
                        new Date(System.currentTimeMillis() + 60000)
                )
                .signWith(clave)
                .compact();
    }

    private HttpHeaders headersConRol(String rol) {

        HttpHeaders headers = new HttpHeaders();

        headers.setBearerAuth(crearToken(rol));

        return headers;
    }

    private String datosSancion() {

        return """
                {
                    "usuarioId": 1,
                    "motivo": "Conducta de prueba",
                    "severidad": "LEVE",
                    "estado": "ACTIVA"
                }
                """;
    }

    @Test
    void consultarSinTokenDevuelve401() {

        HttpClientErrorException error = assertThrows(
                HttpClientErrorException.class,
                () -> restTemplate.getForEntity(
                        urlSanciones(),
                        String.class
                )
        );

        assertEquals(
                HttpStatus.UNAUTHORIZED,
                error.getStatusCode()
        );
    }

    @Test
    void consultarConTokenInvalidoDevuelve401() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth("token-invalido");

        HttpEntity<Void> peticion = new HttpEntity<>(headers);

        HttpClientErrorException error = assertThrows(
                HttpClientErrorException.class,
                () -> restTemplate.exchange(
                        urlSanciones(),
                        HttpMethod.GET,
                        peticion,
                        String.class
                )
        );

        assertEquals(
                HttpStatus.UNAUTHORIZED,
                error.getStatusCode()
        );
    }

    @Test
    void consultarComoUsuarioDevuelve403() {

        HttpEntity<Void> peticion = new HttpEntity<>(
                headersConRol("USUARIO")
        );

        HttpClientErrorException error = assertThrows(
                HttpClientErrorException.class,
                () -> restTemplate.exchange(
                        urlSanciones(),
                        HttpMethod.GET,
                        peticion,
                        String.class
                )
        );

        assertEquals(
                HttpStatus.FORBIDDEN,
                error.getStatusCode()
        );
    }

    @Test
    void consultarComoAdminDevuelve200() {

        HttpEntity<Void> peticion = new HttpEntity<>(
                headersConRol("ADMIN")
        );

        var respuesta = restTemplate.exchange(
                urlSanciones(),
                HttpMethod.GET,
                peticion,
                String.class
        );

        assertEquals(
                HttpStatus.OK,
                respuesta.getStatusCode()
        );
    }

    @Test
    void crearSinTokenDevuelve401() {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> peticion = new HttpEntity<>(
                datosSancion(),
                headers
        );

        HttpClientErrorException error = assertThrows(
                HttpClientErrorException.class,
                () -> restTemplate.postForEntity(
                        urlSanciones(),
                        peticion,
                        String.class
                )
        );

        assertEquals(
                HttpStatus.UNAUTHORIZED,
                error.getStatusCode()
        );
    }

    @Test
    void crearComoUsuarioDevuelve403() {

        HttpHeaders headers = headersConRol("USUARIO");
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> peticion = new HttpEntity<>(
                datosSancion(),
                headers
        );

        HttpClientErrorException error = assertThrows(
                HttpClientErrorException.class,
                () -> restTemplate.postForEntity(
                        urlSanciones(),
                        peticion,
                        String.class
                )
        );

        assertEquals(
                HttpStatus.FORBIDDEN,
                error.getStatusCode()
        );
    }

    @Test
    void crearComoAdminDevuelve201() {

        HttpHeaders headers = headersConRol("ADMIN");
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> peticion = new HttpEntity<>(
                datosSancion(),
                headers
        );

        var respuesta = restTemplate.postForEntity(
                urlSanciones(),
                peticion,
                String.class
        );

        assertEquals(
                HttpStatus.CREATED,
                respuesta.getStatusCode()
        );
    }
}
