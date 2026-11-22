package com.project.msvc_sanction.service;

import com.project.msvc_sanction.model.Sanction;
import com.project.msvc_sanction.repository.SanctionRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SanctionDuplicadosTest {

    @Mock
    private SanctionRepository sanctionRepository;

    @InjectMocks
    private SanctionService sanctionService;

    @Test
    void rechazaSancionActivaDuplicada() {

        Sanction nueva = new Sanction(
                null, 2L, "Conducta violenta", "LEVE", "ACTIVA"
        );

        when(sanctionRepository
                .existsByUsuarioIdAndEstadoIgnoreCase(2L, "ACTIVA"))
                .thenReturn(true);

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> sanctionService.guardarSanction(nueva)
        );

        assertEquals(HttpStatus.CONFLICT, error.getStatusCode());

        verify(sanctionRepository, never()).save(any(Sanction.class));
    }

    @Test
    void permitePrimeraSancionActiva() {

        Sanction nueva = new Sanction(
                null, 2L, "Conducta violenta", "LEVE", "ACTIVA"
        );

        when(sanctionRepository.save(nueva)).thenReturn(nueva);

        Sanction resultado = sanctionService.guardarSanction(nueva);

        assertNotNull(resultado);

        verify(sanctionRepository).save(nueva);
    }

    @Test
    void rechazaEdicionQueGeneraDuplicado() {

        Sanction existente = new Sanction(
                1L, 2L, "Motivo anterior", "LEVE", "LEVANTADA"
        );

        Sanction actualizada = new Sanction(
                null, 2L, "Nuevo motivo", "GRAVE", "ACTIVA"
        );

        when(sanctionRepository.findById(1L))
                .thenReturn(Optional.of(existente));

        when(sanctionRepository
                .existsByUsuarioIdAndEstadoIgnoreCaseAndIdNot(
                        2L, "ACTIVA", 1L
                ))
                .thenReturn(true);

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> sanctionService.actualizarSanction(
                        1L, actualizada
                )
        );

        assertEquals(HttpStatus.CONFLICT, error.getStatusCode());

        verify(sanctionRepository, never()).save(any(Sanction.class));
    }
}
