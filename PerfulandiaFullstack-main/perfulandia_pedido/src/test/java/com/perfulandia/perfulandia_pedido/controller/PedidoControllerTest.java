package com.perfulandia.perfulandia_pedido.controller;

import com.perfulandia.perfulandia_pedido.model.Pedido;
import com.perfulandia.perfulandia_pedido.model.PedidoModel;
import com.perfulandia.perfulandia_pedido.model.PagoRequest;
import com.perfulandia.perfulandia_pedido.service.PedidoService;
import com.perfulandia.perfulandia_pedido.service.PedidoHateoasService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para PedidoController
 * Esta clase contiene pruebas unitarias que verifican el comportamiento
 * del controlador de pedidos de manera aislada, utilizando mocks para
 * simular las dependencias del servicio.
 */

@DisplayName("Pruebas Unitarias - PedidoController")
class PedidoControllerTest {

    @Mock
    private PedidoService pedidoService;

    @Mock
    private PedidoHateoasService pedidoHateoasService;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private PedidoController pedidoController;

    private Pedido pedidoTest;
    private PedidoModel pedidoModelTest;
    private List<Pedido> listaPedidosTest;

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

        pedidoModelTest = new PedidoModel(pedidoTest);
        listaPedidosTest = Arrays.asList(pedidoTest);
    }

    @Test
    @DisplayName("Debería listar todos los pedidos correctamente")
    void testListarPedidos() {
        // Arrange (Preparar)
        when(pedidoService.listarPedidos()).thenReturn(listaPedidosTest);
        CollectionModel<PedidoModel> mockCollection = CollectionModel.of(Arrays.asList(pedidoModelTest));
        mockCollection.add(Link.of("/api/v1/pedido").withSelfRel());
        when(pedidoHateoasService.toCollectionModel(listaPedidosTest)).thenReturn(mockCollection);

        // Act (Actuar)
        CollectionModel<PedidoModel> resultado = pedidoController.listarPedidos();

        // Assert (Verificar)
        assertNotNull(resultado);
        assertTrue(resultado.hasLinks());
        verify(pedidoService, times(1)).listarPedidos();
        verify(pedidoHateoasService, times(1)).toCollectionModel(listaPedidosTest);
    }

    @Test
    @DisplayName("Debería crear un pedido exitosamente cuando el usuario existe y hay stock suficiente")
    void testCrearPedidoExitoso() {
        // Arrange
        when(pedidoService.usuarioExiste(1)).thenReturn(true);
        when(pedidoService.consultarStockProducto(1L)).thenReturn(10);
        when(pedidoService.crearPedido(any(Pedido.class))).thenReturn(pedidoTest);
        when(pedidoHateoasService.toModel(pedidoTest)).thenReturn(pedidoModelTest);
        doNothing().when(pedidoService).registrarPago(any(PagoRequest.class));

        // Act
        ResponseEntity<?> respuesta = pedidoController.crearPedido(pedidoTest);

        // Assert
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertTrue(respuesta.getBody() instanceof PedidoModel);
        verify(pedidoService, times(1)).usuarioExiste(1);
        verify(pedidoService, times(1)).consultarStockProducto(1L);
        verify(pedidoService, times(1)).crearPedido(pedidoTest);
        verify(pedidoService, times(1)).registrarPago(any(PagoRequest.class));
        verify(pedidoHateoasService, times(1)).toModel(pedidoTest);
    }

    @Test
    @DisplayName("Debería fallar al crear pedido cuando el usuario no existe")
    void testCrearPedidoUsuarioNoExiste() {
        // Arrange
        when(pedidoService.usuarioExiste(1)).thenReturn(false);

        // Act
        ResponseEntity<?> respuesta = pedidoController.crearPedido(pedidoTest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
        assertEquals("El usuario no existe, no se puede crear el pedido.", respuesta.getBody());
        verify(pedidoService, times(1)).usuarioExiste(1);
        verify(pedidoService, never()).crearPedido(any(Pedido.class));
    }

    @Test
    @DisplayName("Debería fallar al crear pedido cuando no hay stock suficiente")
    void testCrearPedidoStockInsuficiente() {
        // Arrange
        when(pedidoService.usuarioExiste(1)).thenReturn(true);
        when(pedidoService.consultarStockProducto(1L)).thenReturn(1); // Stock menor que cantidad (2)

        // Act
        ResponseEntity<?> respuesta = pedidoController.crearPedido(pedidoTest);

        // Assert
        assertEquals(HttpStatus.CONFLICT, respuesta.getStatusCode());
        assertEquals("Stock insuficiente. Disponible: 1", respuesta.getBody());
        verify(pedidoService, times(1)).usuarioExiste(1);
        verify(pedidoService, times(1)).consultarStockProducto(1L);
        verify(pedidoService, never()).crearPedido(any(Pedido.class));
    }

    @Test
    @DisplayName("Debería fallar al crear pedido cuando el producto no existe")
    void testCrearPedidoProductoNoExiste() {
        // Arrange
        when(pedidoService.usuarioExiste(1)).thenReturn(true);
        when(pedidoService.consultarStockProducto(1L)).thenReturn(null);

        // Act
        ResponseEntity<?> respuesta = pedidoController.crearPedido(pedidoTest);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        assertEquals("Producto no encontrado en inventario", respuesta.getBody());
        verify(pedidoService, times(1)).usuarioExiste(1);
        verify(pedidoService, times(1)).consultarStockProducto(1L);
        verify(pedidoService, never()).crearPedido(any(Pedido.class));
    }

    @Test
    @DisplayName("Debería buscar un pedido por ID correctamente")
    void testBuscarPedido() {
        // Arrange
        when(pedidoService.buscarPedido(1)).thenReturn(pedidoTest);
        when(pedidoHateoasService.toModel(pedidoTest)).thenReturn(pedidoModelTest);

        // Act
        ResponseEntity<PedidoModel> respuesta = pedidoController.buscarPedido(1);

        // Assert
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals(pedidoModelTest.getId(), respuesta.getBody().getId());
        verify(pedidoService, times(1)).buscarPedido(1);
        verify(pedidoHateoasService, times(1)).toModel(pedidoTest);
    }

    @Test
    @DisplayName("Debería retornar 404 cuando busca un pedido que no existe")
    void testBuscarPedidoNoExiste() {
        // Arrange
        when(pedidoService.buscarPedido(999)).thenReturn(null);

        // Act
        ResponseEntity<PedidoModel> respuesta = pedidoController.buscarPedido(999);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        verify(pedidoService, times(1)).buscarPedido(999);
        verify(pedidoHateoasService, never()).toModel(any(Pedido.class));
    }

    @Test
    @DisplayName("Debería actualizar un pedido correctamente")
    void testActualizarPedido() {
        // Arrange
        Pedido pedidoActualizado = new Pedido();
        pedidoActualizado.setId(1);
        pedidoActualizado.setCantidad(5);
        PedidoModel pedidoModelActualizado = new PedidoModel(pedidoActualizado);
        
        when(pedidoService.actualizarPedido(1, pedidoActualizado)).thenReturn(pedidoActualizado);
        when(pedidoHateoasService.toModel(pedidoActualizado)).thenReturn(pedidoModelActualizado);

        // Act
        ResponseEntity<PedidoModel> respuesta = pedidoController.actualizarPedido(1, pedidoActualizado);

        // Assert
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals(5, respuesta.getBody().getCantidad());
        verify(pedidoService, times(1)).actualizarPedido(1, pedidoActualizado);
        verify(pedidoHateoasService, times(1)).toModel(pedidoActualizado);
    }

    @Test
    @DisplayName("Debería retornar 404 al actualizar un pedido que no existe")
    void testActualizarPedidoNoExiste() {
        // Arrange
        when(pedidoService.actualizarPedido(eq(999), any(Pedido.class))).thenReturn(null);

        // Act
        ResponseEntity<PedidoModel> respuesta = pedidoController.actualizarPedido(999, pedidoTest);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        verify(pedidoService, times(1)).actualizarPedido(eq(999), any(Pedido.class));
        verify(pedidoHateoasService, never()).toModel(any(Pedido.class));
    }

    @Test
    @DisplayName("Debería eliminar un pedido correctamente")
    void testEliminarPedido() {
        // Arrange
        doNothing().when(pedidoService).eliminarPedido(1);

        // Act
        ResponseEntity<Void> respuesta = pedidoController.eliminarPedido(1);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, respuesta.getStatusCode());
        verify(pedidoService, times(1)).eliminarPedido(1);
    }

    @Test
    @DisplayName("Debería verificar stock correctamente cuando hay stock suficiente")
    void testVerificarStockSuficiente() {
        // Arrange
        when(pedidoService.consultarStockProducto(1L)).thenReturn(10);

        // Act
        ResponseEntity<String> respuesta = pedidoController.verificarStock(1L, 5);

        // Assert
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals("Stock suficiente: 10", respuesta.getBody());
        verify(pedidoService, times(1)).consultarStockProducto(1L);
    }

    @Test
    @DisplayName("Debería verificar stock correctamente cuando no hay stock suficiente")
    void testVerificarStockInsuficiente() {
        // Arrange
        when(pedidoService.consultarStockProducto(1L)).thenReturn(3);

        // Act
        ResponseEntity<String> respuesta = pedidoController.verificarStock(1L, 5);

        // Assert
        assertEquals(HttpStatus.CONFLICT, respuesta.getStatusCode());
        assertEquals("Stock insuficiente. Disponible: 3", respuesta.getBody());
        verify(pedidoService, times(1)).consultarStockProducto(1L);
    }

    @Test
    @DisplayName("Debería verificar stock correctamente cuando el producto no existe")
    void testVerificarStockProductoNoExiste() {
        // Arrange
        when(pedidoService.consultarStockProducto(1L)).thenReturn(null);

        // Act
        ResponseEntity<String> respuesta = pedidoController.verificarStock(1L, 5);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        assertEquals("Producto no encontrado en inventario", respuesta.getBody());
        verify(pedidoService, times(1)).consultarStockProducto(1L);
    }
} 