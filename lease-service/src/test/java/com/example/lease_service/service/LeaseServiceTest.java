package com.example.lease_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.lease_service.model.Lease;
import com.example.lease_service.repository.LeaseRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class LeaseServiceTest {

    @Mock
    private LeaseRepository leaseRepository;

    @Mock
    private WebClient.Builder webClientBuilder;

    @InjectMocks
    private LeaseService leaseService;

    private Lease leaseBase;

    @BeforeEach
    void setUp() {
        leaseBase = new Lease();
        leaseBase.setId(1L);
        leaseBase.setLoftId(5L); // Rango válido (1 al 9)
        leaseBase.setTenantId(10L);
        leaseBase.setFechaInicio(LocalDate.now());
        leaseBase.setFechaTermino(LocalDate.now().plusMonths(6));
        leaseBase.setPrecioMensual(450000.0);
        leaseBase.setMontoGarantia(450000.0);
        leaseBase.setEstadoContrato("ACTIVO");
    }

    @Test
    void validarRangoLoft_ErrorInvalidoMenor() {
        // GIVEN (Un contrato con ID de loft inválido)
        leaseBase.setLoftId(0L);

        // WHEN & THEN (Debe lanzar la excepción de negocio sin llegar a persistir nada)
        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> {
            leaseService.saveLease(leaseBase);
        });

        assertTrue(excepcion.getMessage().contains("Error de negocio: El ID del loft"));
        verify(leaseRepository, never()).save(any(Lease.class));
    }

    @Test
    void validarRangoLoft_ErrorInvalidoMayor() {
        // GIVEN
        leaseBase.setLoftId(10L); // Fuera del rango 1-9

        // WHEN & THEN
        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> {
            leaseService.saveLease(leaseBase);
        });

        assertTrue(excepcion.getMessage().contains("no existe. Solo operamos con los lofts del 1 al 9"));
        verify(leaseRepository, never()).save(any(Lease.class));
    }

    @Test
    void validarConsistenciasFechas_ErrorFechaInicioPosterior() {
        // GIVEN (Fecha inicio es posterior a la de término)
        leaseBase.setFechaInicio(LocalDate.now().plusDays(10));
        leaseBase.setFechaTermino(LocalDate.now());

        // WHEN & THEN
        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> {
            leaseService.saveLease(leaseBase);
        });

        assertEquals("La fecha de inicio no puede ser posterior a la fecha de termino", excepcion.getMessage());
        verify(leaseRepository, never()).save(any(Lease.class));
    }

    @Test
    void validarDisponibilidad_ErrorLoftOcupadoEnFechas() {
        // GIVEN (El loft está en rango y las fechas son coherentes, pero chocan con otro contrato en BD)
        when(leaseRepository.existsOverlap(eq(leaseBase.getLoftId()), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(true);

        // WHEN & THEN
        IllegalStateException excepcion = assertThrows(IllegalStateException.class, () -> {
            leaseService.saveLease(leaseBase);
        });

        assertEquals("El Loft ya está ocupado en esas fechas.", excepcion.getMessage());
        verify(leaseRepository, never()).save(any(Lease.class));
    }
}
