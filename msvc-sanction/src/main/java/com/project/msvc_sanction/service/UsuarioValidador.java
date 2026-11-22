
package com.project.msvc_sanction.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UsuarioValidador {

    private final RestTemplate restTemplate;
    private final String urlUser;

    public UsuarioValidador(
            RestTemplate restTemplate,
            @Value("${app.user.url}") String urlUser
    ) {
        this.restTemplate = restTemplate;
        this.urlUser = urlUser;
    }

    public void validar(Long usuarioId, String authorization) {

        if (usuarioId == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debes indicar un usuario"
            );
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, authorization);

        HttpEntity<Void> peticion = new HttpEntity<>(headers);

        try {

            ResponseEntity<String> respuesta =
                    restTemplate.exchange(
                            urlUser + "/users/" + usuarioId,
                            HttpMethod.GET,
                            peticion,
                            String.class
                    );

            if (!respuesta.getStatusCode().is2xxSuccessful()) {
                throw new ResponseStatusException(
                        HttpStatus.SERVICE_UNAVAILABLE,
                        "No se pudo validar el usuario"
                );
            }

        } catch (HttpClientErrorException e) {

            if (e.getStatusCode().value() == 404) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "El usuario no existe"
                );
            }

            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "No se pudo consultar el servicio User"
            );

        } catch (RestClientException e) {

            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "El servicio User no esta disponible"
            );
        }
    }
}
