package com.perfulandia.perfulandia_producto.service;

import com.perfulandia.perfulandia_producto.model.Producto;
import com.perfulandia.perfulandia_producto.repository.ProductoRepository;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoService productoService;

    private Producto producto;

    @BeforeEach
    void setUp() {
        producto = new Producto(1, "Test Producto", 10, 99.99);
    }

    @Test
    void testListarProductos() {
        // Arrange
        Producto producto2 = new Producto(2, "Test Producto 2", 20, 199.99);
        given(productoRepository.findAll()).willReturn(Arrays.asList(producto, producto2));

        // Act
        List<Producto> result = productoService.listarProductos();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(productoRepository).findAll();
    }

    @Test
    void testCrearProducto() {
        // Arrange
        given(productoRepository.save(any(Producto.class))).willReturn(producto);

        // Act
        Producto result = productoService.crearProducto(new Producto());

        // Assert
        assertNotNull(result);
        assertEquals(producto.getNombre(), result.getNombre());
        verify(productoRepository).save(any(Producto.class));
    }

    @Test
    void testBuscarProducto_Success() {
        // Arrange
        given(productoRepository.findById(1)).willReturn(Optional.of(producto));

        // Act
        Producto result = productoService.buscarProducto(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(productoRepository).findById(1);
    }

    @Test
    void testBuscarProducto_NotFound() {
        // Arrange
        given(productoRepository.findById(1)).willReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            productoService.buscarProducto(1);
        });
        verify(productoRepository).findById(1);
    }

    @Test
    void testActualizarProducto_Success() {
        // Arrange
        Producto productoActualizado = new Producto(1, "Updated Producto", 5, 49.99);

        given(productoRepository.existsById(1)).willReturn(true);
        given(productoRepository.save(any(Producto.class))).willReturn(productoActualizado);

        // Act
        Producto result = productoService.actualizarProducto(1, productoActualizado);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Producto", result.getNombre());
        verify(productoRepository).existsById(1);
        verify(productoRepository).save(any(Producto.class));
    }

    @Test
    void testActualizarProducto_NotFound() {
        // Arrange
        given(productoRepository.existsById(1)).willReturn(false);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            productoService.actualizarProducto(1, new Producto());
        });
        verify(productoRepository).existsById(1);
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    void testEliminarProducto_Success() {
        // Arrange
        given(productoRepository.existsById(1)).willReturn(true);
        doNothing().when(productoRepository).deleteById(1);

        // Act
        productoService.eliminarProducto(1);

        // Assert
        verify(productoRepository).existsById(1);
        verify(productoRepository).deleteById(1);
    }

    @Test
    void testEliminarProducto_NotFound() {
        // Arrange
        given(productoRepository.existsById(1)).willReturn(false);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            productoService.eliminarProducto(1);
        });
        verify(productoRepository).existsById(1);
        verify(productoRepository, never()).deleteById(anyInt());
    }
} 