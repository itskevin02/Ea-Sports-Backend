package com.project.msvc_user.service;

import com.project.msvc_user.dto.SancionDTO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Service
public class SancionValidador {

    private final RestTemplate restTemplate;
    private final String urlSanction;

    public SancionValidador(
            RestTemplate restTemplate,
            @Value("${app.sanction.url}") String urlSanction
    ) {
        this.restTemplate = restTemplate;
        this.urlSanction = urlSanction;
    }

    public void validarSinSanciones(
            Long usuarioId,
            String authorization
    ) {

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, authorization);

        HttpEntity<Void> peticion = new HttpEntity<>(headers);

        ResponseEntity<SancionDTO[]> respuesta;

        try {

            respuesta = restTemplate.exchange(
                    urlSanction + "/sanctions",
                    HttpMethod.GET,
                    peticion,
                    SancionDTO[].class
            );

        } catch (RestClientException e) {

            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "No se pudo consultar el servicio Sanction"
            );
        }

        if (respuesta == null ||
                !respuesta.getStatusCode().is2xxSuccessful() ||
                respuesta.getBody() == null) {

            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "No se recibieron las sanciones del servidor"
            );
        }

        for (SancionDTO sancion : respuesta.getBody()) {

            if (sancion != null &&
                    usuarioId.equals(sancion.getUsuarioId())) {

                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "No se puede eliminar un usuario con sanciones"
                );
            }
        }
    }
}
