package com.loftmanager.utility_service.service;

import com.loftmanager.utility_service.model.Lectura;
import com.loftmanager.utility_service.repository.LecturaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LecturaServiceTest {

    @Mock
    private LecturaRepository lecturaRepository;

    @InjectMocks
    private LecturaService lecturaService;

    @BeforeEach
    void setUp() {
        // TRUCO TÉCNICO: Como usas @Value("${utility.precio-kwh}") en tu servicio, 
        // Mockito por sí solo no inyecta ese valor y lanzaría NullPointerException al calcular.
        // Usamos ReflectionTestUtils para simular el valor de application.properties.
        ReflectionTestUtils.setField(lecturaService, "precioKwhConfig", 200.0);
    }

    // PRUEBA 1: Flujo feliz con cálculo exitoso (Restando lectura anterior)
    @Test
    void testCreateLectura_Success_ConLecturaAnterior() {
        // Arrange (Preparación)
        Lectura nuevaLectura = new Lectura();
        nuevaLectura.setIdLoftFk(1L);
        nuevaLectura.setLecturaMedidor(1340.0);

        Lectura lecturaAnterior = new Lectura();
        lecturaAnterior.setLecturaMedidor(1250.0);

        when(lecturaRepository.findFirstByIdLoftFkOrderByFechaLecturaDesc(1L))
                .thenReturn(Optional.of(lecturaAnterior));
        when(lecturaRepository.save(any(Lectura.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act (Ejecución)
        Lectura resultado = lecturaService.createLectura(nuevaLectura);

        // Assert (Validación)
        assertNotNull(resultado);
        assertEquals(90.0, resultado.getConsumoPeriodoKwh(), "El consumo neto debe ser 90 (1340 - 1250)");
        assertEquals(18000.0, resultado.getMontoCobro(), "El monto total debe ser 18000 (90 * 200)");
        verify(lecturaRepository, times(1)).save(nuevaLectura);
    }

    // PRUEBA 2: Flujo feliz de un Loft nuevo (El medidor inicia en 0)
    @Test
    void testCreateLectura_Success_SinLecturaAnterior() {
        // Arrange
        Lectura nuevaLectura = new Lectura();
        nuevaLectura.setIdLoftFk(5L);
        nuevaLectura.setLecturaMedidor(100.0);

        // Simulamos que el repositorio devuelve vacío (No hay mes anterior)
        when(lecturaRepository.findFirstByIdLoftFkOrderByFechaLecturaDesc(5L))
                .thenReturn(Optional.empty());
        when(lecturaRepository.save(any(Lectura.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        Lectura resultado = lecturaService.createLectura(nuevaLectura);

        // Assert
        assertEquals(100.0, resultado.getConsumoPeriodoKwh(), "El consumo neto debe ser 100 (100 - 0)");
        assertEquals(20000.0, resultado.getMontoCobro(), "El monto total debe ser 20000 (100 * 200)");
        verify(lecturaRepository, times(1)).save(nuevaLectura);
    }

    // PRUEBA 3: Excepción al enviar un Loft inválido (Fuera del rango 1 al 9)
    @Test
    void testCreateLectura_Error_IdLoftInvalido() {
        // Arrange
        Lectura nuevaLectura = new Lectura();
        nuevaLectura.setIdLoftFk(10L); // Loft 10 no existe

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            lecturaService.createLectura(nuevaLectura);
        });

        assertEquals("Operación inválida: El sistema solo administra los Lofts del 1 al 9.", exception.getMessage());
        verify(lecturaRepository, never()).save(any()); // Verificamos que NUNCA intente guardar en la BD
    }

    // PRUEBA 4: Excepción por inconsistencia física (El medidor corre hacia atrás)
    @Test
    void testCreateLectura_Error_LecturaMenorQueAnterior() {
        // Arrange
        Lectura nuevaLectura = new Lectura();
        nuevaLectura.setIdLoftFk(2L);
        nuevaLectura.setLecturaMedidor(1200.0); // Nueva lectura más baja que la anterior

        Lectura lecturaAnterior = new Lectura();
        lecturaAnterior.setLecturaMedidor(1250.0);

        when(lecturaRepository.findFirstByIdLoftFkOrderByFechaLecturaDesc(2L))
                .thenReturn(Optional.of(lecturaAnterior));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            lecturaService.createLectura(nuevaLectura);
        });

        assertTrue(exception.getMessage().contains("no puede ser menor a la lectura anterior"));
        verify(lecturaRepository, never()).save(any());
    }
}