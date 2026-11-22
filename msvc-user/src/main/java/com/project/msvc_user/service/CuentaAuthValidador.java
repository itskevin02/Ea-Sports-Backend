package com.project.msvc_user.service;

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
public class CuentaAuthValidador {

    private final RestTemplate restTemplate;
    private final String urlAuth;

    public CuentaAuthValidador(
            RestTemplate restTemplate,
            @Value("${app.auth.url}") String urlAuth
    ) {
        this.restTemplate = restTemplate;
        this.urlAuth = urlAuth;
    }

    public void validar(Long authId, String authorization) {

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, authorization);

        HttpEntity<Void> peticion = new HttpEntity<>(headers);

        try {

            ResponseEntity<String> respuesta =
                    restTemplate.exchange(
                            urlAuth + "/auth/" + authId,
                            HttpMethod.GET,
                            peticion,
                            String.class
                    );

            if (!respuesta.getStatusCode().is2xxSuccessful()) {
                throw new ResponseStatusException(
                        HttpStatus.SERVICE_UNAVAILABLE,
                        "No se pudo validar la cuenta Auth"
                );
            }

        } catch (HttpClientErrorException e) {

            if (e.getStatusCode().value() == 404) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "La cuenta Auth no existe"
                );
            }

            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Auth rechazo la consulta"
            );

        } catch (RestClientException e) {

            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "El servicio Auth no esta disponible"
            );
        }
    }
}
