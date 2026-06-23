package com.loftmanager.loftservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.loftmanager.loftservice.model.LoftModel;
import com.loftmanager.loftservice.repository.LoftRepository;

@ExtendWith(MockitoExtension.class)
public class LoftServiceTest {

    @Mock
    private LoftRepository loftRepository;

    @InjectMocks
    private LoftService loftService;

    private LoftModel loftValido;

    @BeforeEach
    void setUp() {
        // Preparamos un objeto válido para usar en nuestras pruebas
        loftValido = new LoftModel();
        loftValido.setIdLoft(5L);
        loftValido.setNombreLoft("Loft de Prueba 5");
        loftValido.setIsOcupado(false);
    }

    // PRUEBA 1: Verificar que un Loft con ID válido (1-9) se guarda correctamente
    @Test
    void guardar_ConIdValido_DebeGuardarYRetornarLoft() {
        // Arrange (Preparar)
        when(loftRepository.save(any(LoftModel.class))).thenReturn(loftValido);

        // Act (Ejecutar)
        LoftModel resultado = loftService.guardar(loftValido);

        // Assert (Verificar)
        assertNotNull(resultado);
        assertEquals(5L, resultado.getIdLoft());
        assertEquals("Loft de Prueba 5", resultado.getNombreLoft());
        verify(loftRepository, times(1)).save(loftValido);
    }

    // PRUEBA 2: Verificar la regla de negocio (ID > 9 debe fallar)
    @Test
    void guardar_ConIdInvalido_DebeLanzarExcepcion() {
        // Arrange (Preparar)
        LoftModel loftInvalido = new LoftModel();
        loftInvalido.setIdLoft(10L); // ID fuera de rango
        loftInvalido.setNombreLoft("Loft Fuera de Rango");

        // Act & Assert (Ejecutar y Verificar Excepción)
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            loftService.guardar(loftInvalido);
        });

        // Verificar el mensaje de la excepción y que NO se llamó a la base de datos
        assertEquals("Error: Solo se pueden registrar Lofts en las casillas físicas del 1 al 9.", exception.getMessage());
        verify(loftRepository, never()).save(any(LoftModel.class));
    }

    // PRUEBA 3: Verificar que listarTodos retorna lo que tiene el repositorio
    @Test
    void listarTodos_DebeRetornarListaDeLofts() {
        // Arrange (Preparar)
        when(loftRepository.findAll()).thenReturn(Arrays.asList(loftValido));

        // Act (Ejecutar)
        List<LoftModel> lista = loftService.listarTodos();

        // Assert (Verificar)
        assertFalse(lista.isEmpty());
        assertEquals(1, lista.size());
        assertEquals(5L, lista.get(0).getIdLoft());
        verify(loftRepository, times(1)).findAll();
    }

    // PRUEBA 4: Verificar que existePorId funciona correctamente
    @Test
    void existePorId_DebeRetornarVerdaderoSiExiste() {
        // Arrange (Preparar)
        when(loftRepository.existsById(5L)).thenReturn(true);

        // Act (Ejecutar)
        boolean existe = loftService.existePorId(5L);

        // Assert (Verificar)
        assertTrue(existe);
        verify(loftRepository, times(1)).existsById(5L);
    }
}