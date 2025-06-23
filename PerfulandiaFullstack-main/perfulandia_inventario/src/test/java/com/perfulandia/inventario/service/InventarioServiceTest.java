package com.perfulandia.inventario.service;

import com.perfulandia.inventario.model.Inventario;
import com.perfulandia.inventario.repository.InventarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para el servicio de inventario.
 * Esta clase contiene pruebas que verifican la lógica de negocio del servicio de inventario,
 * incluyendo operaciones CRUD y casos de error.
 * 
 * Las pruebas utilizan:
 * - Mockito para simular el repositorio (evita dependencias de base de datos)
 * - JUnit 5 para la estructura de pruebas
 * - Assertions para verificar resultados esperados
 * - Patrón AAA (Arrange, Act, Assert) para organizar las pruebas
 */
@ExtendWith(MockitoExtension.class) // Habilita la integración de Mockito con JUnit 5
public class InventarioServiceTest {

    @Mock
    private InventarioRepository inventarioRepository; // Repositorio simulado para evitar dependencias reales

    @InjectMocks
    private InventarioService inventarioService; // Servicio real con dependencias simuladas inyectadas

    private Inventario inventario; // Datos de prueba para inventario

    /**
     * Configuración inicial que se ejecuta antes de cada prueba.
     * Este método crea los datos de prueba que se usarán en todas las pruebas.
     */
    @BeforeEach
    void setUp() {
        // Crea datos de prueba para un inventario
        inventario = new Inventario(1L, 1L, 100, "Almacén A");
    }

    /**
     * Prueba el método findAll() para obtener todos los inventarios.
     * Verifica que el servicio retorna correctamente todos los registros de inventario.
     */
    @Test
    void testFindAll() {
        // Arrange: Prepara los datos de prueba y configura el comportamiento del mock
        Inventario inventario2 = new Inventario(2L, 2L, 50, "Almacén B");
        given(inventarioRepository.findAll()).willReturn(Arrays.asList(inventario, inventario2));

        // Act: Ejecuta el método que se está probando
        List<Inventario> result = inventarioService.findAll();

        // Assert: Verifica que el resultado es el esperado
        assertNotNull(result); // Verifica que el resultado no sea null
        assertEquals(2, result.size()); // Verifica que se retornen 2 inventarios
        verify(inventarioRepository).findAll(); // Verifica que se llamó al método del repositorio
    }

    /**
     * Prueba el método findByProductoId() cuando el inventario existe.
     * Verifica que el servicio retorna correctamente el inventario encontrado.
     */
    @Test
    void testFindByProductoId_Success() {
        // Arrange: Configura el repositorio para retornar un inventario
        given(inventarioRepository.findByProductoId(1L)).willReturn(inventario);

        // Act: Ejecuta el método que se está probando
        Inventario result = inventarioService.findByProductoId(1L);

        // Assert: Verifica que el resultado es el esperado
        assertNotNull(result); // Verifica que el resultado no sea null
        assertEquals(1L, result.getProductoId()); // Verifica que el ID del producto sea correcto
        verify(inventarioRepository).findByProductoId(1L); // Verifica que se llamó al método del repositorio
    }

    /**
     * Prueba el método findByProductoId() cuando el inventario no existe.
     * Verifica que el servicio lanza una excepción cuando no encuentra el inventario.
     */
    @Test
    void testFindByProductoId_NotFound() {
        // Arrange: Configura el repositorio para retornar null (inventario no encontrado)
        given(inventarioRepository.findByProductoId(999L)).willReturn(null);

        // Act & Assert: Verifica que se lanza la excepción esperada
        assertThrows(RuntimeException.class, () -> {
            inventarioService.findByProductoId(999L); // Ejecuta el método que debería lanzar excepción
        });
        verify(inventarioRepository).findByProductoId(999L); // Verifica que se llamó al método del repositorio
    }

    /**
     * Prueba el método findById() cuando el inventario existe.
     * Verifica que el servicio retorna correctamente el inventario encontrado.
     */
    @Test
    void testFindById_Success() {
        // Arrange: Configura el repositorio para retornar un Optional con inventario
        given(inventarioRepository.findById(1L)).willReturn(Optional.of(inventario));

        // Act: Ejecuta el método que se está probando
        Inventario result = inventarioService.findById(1L);

        // Assert: Verifica que el resultado es el esperado
        assertNotNull(result); // Verifica que el resultado no sea null
        assertEquals(1L, result.getId()); // Verifica que el ID sea correcto
        verify(inventarioRepository).findById(1L); // Verifica que se llamó al método del repositorio
    }

    /**
     * Prueba el método findById() cuando el inventario no existe.
     * Verifica que el servicio lanza una excepción cuando no encuentra el inventario.
     */
    @Test
    void testFindById_NotFound() {
        // Arrange: Configura el repositorio para retornar un Optional vacío
        given(inventarioRepository.findById(999L)).willReturn(Optional.empty());

        // Act & Assert: Verifica que se lanza la excepción esperada
        assertThrows(RuntimeException.class, () -> {
            inventarioService.findById(999L); // Ejecuta el método que debería lanzar excepción
        });
        verify(inventarioRepository).findById(999L); // Verifica que se llamó al método del repositorio
    }

    /**
     * Prueba el método save() para guardar un inventario.
     * Verifica que el servicio guarda correctamente el inventario y retorna el resultado.
     */
    @Test
    void testSave() {
        // Arrange: Configura el repositorio para retornar el inventario guardado
        given(inventarioRepository.save(any(Inventario.class))).willReturn(inventario);

        // Act: Ejecuta el método que se está probando
        Inventario result = inventarioService.save(new Inventario());

        // Assert: Verifica que el resultado es el esperado
        assertNotNull(result); // Verifica que el resultado no sea null
        assertEquals(inventario.getProductoId(), result.getProductoId()); // Verifica que el ID del producto sea correcto
        verify(inventarioRepository).save(any(Inventario.class)); // Verifica que se llamó al método del repositorio
    }

    /**
     * Prueba el método ajustarCantidad() para aumentar el stock de un producto.
     * Verifica que el servicio ajusta correctamente la cantidad disponible.
     */
    @Test
    void testAjustarCantidad_Success() {
        // Arrange: Prepara datos de prueba para inventario ajustado
        Inventario inventarioAjustado = new Inventario(1L, 1L, 150, "Almacén A");
        given(inventarioRepository.findByProductoId(1L)).willReturn(inventario);
        given(inventarioRepository.save(any(Inventario.class))).willReturn(inventarioAjustado);

        // Act: Ejecuta el método que se está probando
        Inventario result = inventarioService.ajustarCantidad(1L, 50);

        // Assert: Verifica que el resultado es el esperado
        assertNotNull(result); // Verifica que el resultado no sea null
        assertEquals(150, result.getCantidadDisponible()); // Verifica que la cantidad sea correcta
        verify(inventarioRepository).findByProductoId(1L); // Verifica que se buscó el inventario
        verify(inventarioRepository).save(any(Inventario.class)); // Verifica que se guardó el inventario
    }

    /**
     * Prueba el método ajustarCantidad() cuando el inventario no existe.
     * Verifica que el servicio lanza una excepción cuando no encuentra el inventario.
     */
    @Test
    void testAjustarCantidad_NotFound() {
        // Arrange: Configura el repositorio para retornar null (inventario no encontrado)
        given(inventarioRepository.findByProductoId(999L)).willReturn(null);

        // Act & Assert: Verifica que se lanza la excepción esperada
        assertThrows(RuntimeException.class, () -> {
            inventarioService.ajustarCantidad(999L, 50); // Ejecuta el método que debería lanzar excepción
        });
        verify(inventarioRepository).findByProductoId(999L); // Verifica que se buscó el inventario
        verify(inventarioRepository, never()).save(any(Inventario.class)); // Verifica que NO se guardó nada
    }

    /**
     * Prueba el método ajustarCantidad() para reducir el stock de un producto.
     * Verifica que el servicio ajusta correctamente la cantidad disponible (reducción).
     */
    @Test
    void testAjustarCantidad_Reducir() {
        // Arrange: Prepara datos de prueba para inventario reducido
        Inventario inventarioReducido = new Inventario(1L, 1L, 50, "Almacén A");
        given(inventarioRepository.findByProductoId(1L)).willReturn(inventario);
        given(inventarioRepository.save(any(Inventario.class))).willReturn(inventarioReducido);

        // Act: Ejecuta el método que se está probando (cantidad negativa para reducir)
        Inventario result = inventarioService.ajustarCantidad(1L, -50);

        // Assert: Verifica que el resultado es el esperado
        assertNotNull(result); // Verifica que el resultado no sea null
        assertEquals(50, result.getCantidadDisponible()); // Verifica que la cantidad sea correcta
        verify(inventarioRepository).findByProductoId(1L); // Verifica que se buscó el inventario
        verify(inventarioRepository).save(any(Inventario.class)); // Verifica que se guardó el inventario
    }

    /**
     * Prueba el método deleteById() cuando el inventario existe.
     * Verifica que el servicio elimina correctamente el inventario.
     */
    @Test
    void testDeleteById_Success() {
        // Arrange: Configura el repositorio para indicar que el inventario existe
        given(inventarioRepository.existsById(1L)).willReturn(true);
        doNothing().when(inventarioRepository).deleteById(1L);

        // Act: Ejecuta el método que se está probando
        inventarioService.deleteById(1L);

        // Assert: Verifica que se ejecutaron las operaciones esperadas
        verify(inventarioRepository).existsById(1L); // Verifica que se verificó la existencia
        verify(inventarioRepository).deleteById(1L); // Verifica que se eliminó el inventario
    }

    /**
     * Prueba el método deleteById() cuando el inventario no existe.
     * Verifica que el servicio lanza una excepción cuando no encuentra el inventario.
     */
    @Test
    void testDeleteById_NotFound() {
        // Arrange: Configura el repositorio para indicar que el inventario no existe
        given(inventarioRepository.existsById(999L)).willReturn(false);

        // Act & Assert: Verifica que se lanza la excepción esperada
        assertThrows(RuntimeException.class, () -> {
            inventarioService.deleteById(999L); // Ejecuta el método que debería lanzar excepción
        });
        verify(inventarioRepository).existsById(999L); // Verifica que se verificó la existencia
        verify(inventarioRepository, never()).deleteById(anyLong()); // Verifica que NO se eliminó nada
    }
} 