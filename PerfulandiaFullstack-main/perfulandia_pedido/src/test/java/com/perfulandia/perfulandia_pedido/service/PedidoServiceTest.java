package com.perfulandia.perfulandia_pedido.service;

import com.perfulandia.perfulandia_pedido.model.Pedido;
import com.perfulandia.perfulandia_pedido.model.Producto;
import com.perfulandia.perfulandia_pedido.model.PagoRequest;
import com.perfulandia.perfulandia_pedido.repository.PedidoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para PedidoService
 * 
 * Esta clase contiene pruebas unitarias que verifican la lógica de negocio
 * del servicio de pedidos, utilizando mocks para simular las dependencias.
 */
@DisplayName("Pruebas Unitarias - PedidoService")
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private PedidoService pedidoService;

    private Pedido pedidoTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Configurar datos de prueba
        pedidoTest = new Pedido();
        pedidoTest.setId(1);
        pedidoTest.setCliente("1");
        pedidoTest.setProducto("1");
        pedidoTest.setCantidad(2);
        pedidoTest.setTotal(100.0);
        pedidoTest.setFecha(new Date());
    }

    @Test
    @DisplayName("Debería listar todos los pedidos correctamente")
    void testListarPedidos() {
        // Arrange
        List<Pedido> pedidosEsperados = Arrays.asList(pedidoTest);
        when(pedidoRepository.findAll()).thenReturn(pedidosEsperados);

        // Act
        List<Pedido> resultado = pedidoService.listarPedidos();

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(pedidoTest.getId(), resultado.get(0).getId());
        verify(pedidoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debería crear un pedido correctamente cuando el usuario existe")
    void testCrearPedidoExitoso() {
        // Arrange
        when(restTemplate.getForObject(anyString(), eq(Object.class))).thenReturn(new Object());
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoTest);
        doNothing().when(rabbitTemplate).convertAndSend(anyString(), anyString());

        // Act
        Pedido resultado = pedidoService.crearPedido(pedidoTest);

        // Assert
        assertNotNull(resultado);
        assertEquals(pedidoTest.getId(), resultado.getId());
        verify(pedidoRepository, times(1)).save(pedidoTest);
        verify(rabbitTemplate, times(1)).convertAndSend(anyString(), anyString());
    }

    @Test
    @DisplayName("Debería fallar al crear pedido cuando el usuario no existe")
    void testCrearPedidoUsuarioNoExiste() {
        // Arrange
        when(restTemplate.getForObject(anyString(), eq(Object.class))).thenReturn(null);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            pedidoService.crearPedido(pedidoTest);
        });
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    @DisplayName("Debería buscar un pedido por ID correctamente cuando existe")
    void testBuscarPedidoExiste() {
        // Arrange
        when(pedidoRepository.findById(1)).thenReturn(Optional.of(pedidoTest));

        // Act
        Pedido resultado = pedidoService.buscarPedido(1);

        // Assert
        assertNotNull(resultado);
        assertEquals(pedidoTest.getId(), resultado.getId());
        verify(pedidoRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Debería retornar null cuando busca un pedido que no existe")
    void testBuscarPedidoNoExiste() {
        // Arrange
        when(pedidoRepository.findById(999)).thenReturn(Optional.empty());

        // Act
        Pedido resultado = pedidoService.buscarPedido(999);

        // Assert
        assertNull(resultado);
        verify(pedidoRepository, times(1)).findById(999);
    }

    @Test
    @DisplayName("Debería actualizar un pedido correctamente")
    void testActualizarPedido() {
        // Arrange
        Pedido pedidoActualizado = new Pedido();
        pedidoActualizado.setId(1);
        pedidoActualizado.setCantidad(5);
        pedidoActualizado.setTotal(250.0);
        
        when(pedidoRepository.findById(1)).thenReturn(Optional.of(pedidoTest));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoActualizado);

        // Act
        Pedido resultado = pedidoService.actualizarPedido(1, pedidoActualizado);

        // Assert
        assertNotNull(resultado);
        assertEquals(5, resultado.getCantidad());
        assertEquals(250.0, resultado.getTotal());
        verify(pedidoRepository, times(1)).findById(1);
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
    }

    @Test
    @DisplayName("Debería retornar null al actualizar un pedido que no existe")
    void testActualizarPedidoNoExiste() {
        // Arrange
        when(pedidoRepository.findById(999)).thenReturn(Optional.empty());

        // Act
        Pedido resultado = pedidoService.actualizarPedido(999, pedidoTest);

        // Assert
        assertNull(resultado);
        verify(pedidoRepository, times(1)).findById(999);
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    @DisplayName("Debería eliminar un pedido correctamente")
    void testEliminarPedido() {
        // Arrange
        doNothing().when(pedidoRepository).deleteById(1);

        // Act
        pedidoService.eliminarPedido(1);

        // Assert
        verify(pedidoRepository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("Debería obtener productos externos correctamente")
    void testObtenerProductos() {
        // Arrange
        Producto[] productosArray = new Producto[1];
        productosArray[0] = new Producto();
        when(restTemplate.getForObject(anyString(), eq(Producto[].class))).thenReturn(productosArray);

        // Act
        List<Producto> resultado = pedidoService.obtenerProductos();

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(restTemplate, times(1)).getForObject(anyString(), eq(Producto[].class));
    }

    @Test
    @DisplayName("Debería verificar que un usuario existe correctamente")
    void testUsuarioExiste() {
        // Arrange
        when(restTemplate.getForObject(anyString(), eq(Object.class))).thenReturn(new Object());

        // Act
        boolean resultado = pedidoService.usuarioExiste(1);

        // Assert
        assertTrue(resultado);
        verify(restTemplate, times(1)).getForObject(anyString(), eq(Object.class));
    }

    @Test
    @DisplayName("Debería verificar que un usuario no existe correctamente")
    void testUsuarioNoExiste() {
        // Arrange
        when(restTemplate.getForObject(anyString(), eq(Object.class))).thenReturn(null);

        // Act
        boolean resultado = pedidoService.usuarioExiste(999);

        // Assert
        assertFalse(resultado);
        verify(restTemplate, times(1)).getForObject(anyString(), eq(Object.class));
    }

    @Test
    @DisplayName("Debería registrar un pago correctamente")
    void testRegistrarPago() {
        // Arrange
        PagoRequest pagoRequest = new PagoRequest();
        pagoRequest.setPedidoId(1L);
        pagoRequest.setMonto(100.0);
        when(restTemplate.postForObject(anyString(), any(PagoRequest.class), eq(Void.class))).thenReturn(null);

        // Act
        pedidoService.registrarPago(pagoRequest);

        // Assert
        verify(restTemplate, times(1)).postForObject(anyString(), any(PagoRequest.class), eq(Void.class));
    }

    @Test
    @DisplayName("Debería notificar inventario correctamente")
    void testNotificarInventario() {
        // Arrange
        Long productoId = 1L;
        int cantidad = 5;
        doNothing().when(rabbitTemplate).convertAndSend(anyString(), anyString());

        // Act
        pedidoService.notificarInventario(productoId, cantidad);

        // Assert
        verify(rabbitTemplate, times(1)).convertAndSend(anyString(), anyString());
    }
} 